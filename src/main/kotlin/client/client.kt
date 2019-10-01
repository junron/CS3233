package client

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.cio.websocket.Frame.Text
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.runBlocking

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
        val onConnected: () -> Unit = { Unit }
) {
  private val client = HttpClient {
    install(WebSockets)
  }

  private lateinit var socket: WebSocketSession

  fun send(data: TextData) {
    runBlocking {
      socket.send(data.toString())
    }
  }

  fun init() {
    runBlocking {
      client.webSocket(
              method = HttpMethod.Get,
              host = "localhost",
              port = 8080,
              path = "/websockets"
      ) {
        socket = this
        onConnected()
        for (frame in incoming) {
          if (frame is Text) {
            val response = Gson().fromJson(frame.readText(), GenericResponse::class.java)
            if (response.error) {
              onError(response.getErrorResponse())
            } else {
              onResponse(response.getSuccessResponse())
            }
          }
        }
      }
    }
  }

  fun close() {
    runBlocking {
      socket.close()
    }
    client.close()
  }
}