package client

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.features.websocket.wss
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame.Text
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeoutException

private data class GenericResponse(val error: Boolean, val broadcast: Boolean, val message: String) {
  fun getErrorResponse() = ErrorResponse(message)
  fun getSuccessResponse() = SuccessResponse(message, broadcast)
}


data class ErrorResponse(val message: String)
data class SuccessResponse(val message: String, val broadcast: Boolean)
data class UpdateRequest(val type: String, val data: String, val index: Int = -1) {
  override fun toString(): String = Gson().toJson(this)
}

data class DataSync(val objects: MutableList<String>)

data class TextData(val type: String, val data: String) {
  override fun toString(): String = Gson().toJson(this)
}


class Client(
        val onResponse: (success: SuccessResponse) -> Unit,
        val onError: (failure: ErrorResponse) -> Unit,
        val onException: (exception: Exception) -> Unit,
        val onConnected: () -> Unit = { Unit },
        private val ping: ((pingStart: Long) -> Unit)?
) {
  private val secure = true
  private var pingActive = false
  private val client = HttpClient {
    install(WebSockets)
  }

  private lateinit var socket: WebSocketSession

  fun send(data: TextData, onException: ((exception: Exception) -> Unit)? = null) {
    runBlocking {
      try {
        socket.send(data.toString())
      } catch (e: Exception) {
        onException?.invoke(e)
      }
    }
  }

  fun init() {
    runBlocking {
      try {
        if (!secure) {
          client.webSocket(
                  method = HttpMethod.Get,
                  host = "localhost",
                  port = 8080,
                  path = "/websockets",
                  block = ::handler
          )
        } else {
          client.wss(
                  method = HttpMethod.Get,
                  host = "raysim.latency-check.nushhwboard.tk",
                  port = 443,
                  path = "/websockets",
                  block = ::handler
          )
        }
      } catch (e: Exception) {
        onException(e)
      }
    }
  }

  private suspend fun handler(connection: DefaultClientWebSocketSession) = with(connection) {
    socket = this
    onConnected()
    if (ping != null) launch {
      while (client.isActive) {
        if (pingActive){
          onException(TimeoutException("Connection timeout"))
          socket.terminate()
        }
        send(TextData("ping", System.currentTimeMillis().toString()))
        pingActive = true
        delay(1000)
      }
    }
    for (frame in incoming) {
      if (frame !is Text) continue
      val response = Gson().fromJson(frame.readText(), GenericResponse::class.java)
      if (response.error) {
        onError(response.getErrorResponse())
        continue
      }
      if (response.message.startsWith("pong:") && !response.broadcast) {
        ping?.invoke(response.message.substringAfter("pong:").toLong())
        pingActive = false
        continue
      }
      onResponse(response.getSuccessResponse())
    }
  }

  fun close() {
    runBlocking {
      if (::socket.isInitialized) socket.close()
    }
    client.close()
  }
}