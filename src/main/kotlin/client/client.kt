package client

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
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


data class UpdateRequest(val type: String, val data: String, val index: Int = -1) {
  override fun toString(): String = Gson().toJson(this)
}

open class GenericData(val error: Boolean)
data class TextData(val type: String, val data: String, val broadcast: Boolean = false) : GenericData(false) {
  constructor(type: String, data: String) : this(type, data, false)

  override fun toString(): String = Gson().toJson(this)
}

data class ErrorResponse(val message: String) : GenericData(true) {
  val broadcast = false
  override fun toString(): String = Gson().toJson(this)
}


class Client(
        val onResponse: (success: TextData) -> Unit,
        val onError: (failure: ErrorResponse) -> Unit,
        val onException: (exception: Exception) -> Unit,
        val onConnected: () -> Unit = { Unit },
        private val ping: ((pingStart: Long) -> Unit)?
) {
  private var pingActive = false
  private var stop = false
  private var client = HttpClient {
    install(WebSockets)
  }

  companion object {
    var room: String? = null
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

  fun join(roomName: String) {
    send(TextData("setRoom", roomName))
  }

  private fun init() {
    runBlocking {
      try {
        client = HttpClient {
          install(WebSockets)
        }
        println("try")
        client.wss(
                method = HttpMethod.Get,
                host = "raysim.latency-check.nushhwboard.tk",
                port = 443,
                path = "/websockets",
                block = ::handler
        )
        println("Succede")
        pingActive = false
      } catch (e: Exception) {
        onException(e)
      }
    }
  }

  private suspend fun handler(connection: DefaultClientWebSocketSession) = with(connection) {
    socket = this
    room?.let {
      join(room!!)
    }
    onConnected()
    if (ping != null) launch {
      while (socket.isActive) {
        if (pingActive) {
          onException(TimeoutException("Connection timeout"))
          if (::socket.isInitialized) socket.close()
          stop = true
          client.close()
        }
        send(TextData("ping", System.currentTimeMillis().toString()))
        pingActive = true
        delay(2000)
      }
    }
    for (frame in incoming) {
      if (frame !is Text) continue
      val response = Gson().fromJson(frame.readText(), GenericData::class.java)
      if (response.error) {
        onError(Gson().fromJson(frame.readText(), ErrorResponse::class.java))
        continue
      }
      val successResponse = Gson().fromJson(frame.readText(), TextData::class.java)
      if (successResponse.type == "pong") {
        ping?.invoke(successResponse.data.toLong())
        pingActive = false
        continue
      }
      if (successResponse.type == "roomCreate" || successResponse.type == "setRoom") {
        room = successResponse.data
      }
      onResponse(successResponse)
    }
  }

  fun reconnect() {
    while (!(::socket.isInitialized && socket.isActive) && !stop) {
      runBlocking {
        init()
        delay(1000)
      }
    }
  }

  fun close() {
    runBlocking {
      if (::socket.isInitialized) socket.close()
    }
    stop = true
    client.close()
  }
}