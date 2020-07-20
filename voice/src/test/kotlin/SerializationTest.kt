import dev.kord.voice.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import kotlinx.serialization.stringify
import kotlin.test.Test
import kotlin.test.assertEquals

private fun file(name: String): String {
    val loader = SerializationTest::class.java.classLoader
    return loader.getResource("json/$name.json").readText()
}

class SerializationTest {
    @Test
    fun `heartbeat serializes correctly`() {
        val heartbeat = Json.stringify(VoiceCommand, VoiceCommand.Heartbeat(1501184119561))
        val json = Json.stringify(JsonObject.serializer(), json {

            "op" to VoiceOpCode.Heartbeat.code
            "d" to 1501184119561

        })

        assertEquals(json, heartbeat)
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
        val protocol = Json.stringify(
                VoiceCommand,
                VoiceCommand.SelectProtocol(
                        "udp",
                        VoiceCommand.SelectProtocolData("127.0.0.1", 1337, "xsalsa20_poly1305_lite")
                ))
        val json = Json.stringify(JsonObject.serializer(), json {
            "op" to VoiceOpCode.SelectProtocol.code
            "d" to json {
                "protocol" to "udp"
                "data" to json {
                    "address" to "127.0.0.1"
                    "port" to 1337
                    "mode" to "xsalsa20_poly1305_lite"
                }
            }
        })

        assertEquals(json, protocol)
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

    @Test
    fun `identify serializes correctly`() {
        val identify = Json.stringify(
                VoiceCommand,
                VoiceCommand.Identify(
                        "41771983423143937",
                        "104694319306248192",
                        "my_session_id",
                        "my_token"
                )

        )

        val json = Json.stringify(JsonObject.serializer(), json {
            "op" to VoiceOpCode.Identify.code
            "d" to json {
                "server_id" to "41771983423143937"
                "user_id" to "104694319306248192"
                "session_id" to "my_session_id"
                "token" to "my_token"
            }
        })

        assertEquals(json, identify)
    }
}