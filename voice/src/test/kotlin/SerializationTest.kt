import dev.kord.voice.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/$name.json").readText()
}

class SerializationTest {
    @Test
    fun `heartbeat serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("heartbeat"))
        with(json as VoiceHeartbeat) {
            assertEquals(1501184119561, data)
        }
    }

    @Test
    fun `heartbeat ack serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("heartbeat_ack"))
        with(json as VoiceHeartbeatACK) {
            assertEquals(1501184119561, data)
        }
    }


    @Test
    fun `hello serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("hello"))
        with(json as VoiceHello) {
            assertEquals(41250, heartbeatInterval)
        }
    }

    @Test
    fun `protocol selection serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("protocol_selection"))
        with(json as SelectProtocol) {
            assertEquals("udp", protocol)
            assertEquals("127.0.0.1", data.address)
            assertEquals(1337, data.port)
            assertEquals("xsalsa20_poly1305_lite", data.mode)
        }
    }

    @Test
    fun `ready serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("ready"))
        with(json as VoiceReady) {
            assertEquals(1, ssrc)
            assertEquals("127.0.0.1", ip)
            assertEquals(1234, port)
            assertEquals(listOf("xsalsa20_poly1305", "xsalsa20_poly1305_suffix", "xsalsa20_poly1305_lite"), modes)
        }
    }

    @Test
    fun `resumed serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("resumed"))
        assert(json is Resumed)
    }

    @Test
    fun `session description serializes correctly`() {
        val json = Json.parse(VoiceEvent, file("session_description"))
        with(json as SessionDescription) {
            assertEquals("xsalsa20_poly1305_lite", mode)
            assertEquals(listOf(251, 100, 11), secretKey)
        }
    }
}