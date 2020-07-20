package dev.kord.voice

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonObject

sealed class VoiceCommand {
    @Serializable
    data class Identify(
            @SerialName("server_id")
            val serverId: String,
            @SerialName("user_id")
            val userId: String,
            @SerialName("session_id")
            val sessionId: String,
            val token: String
    ) : VoiceCommand()


    @Serializable
    data class Heartbeat(val data: Long) : VoiceCommand() {
        @Serializer(Heartbeat::class)
        companion object : SerializationStrategy<Heartbeat> {
            override val descriptor: SerialDescriptor = PrimitiveDescriptor("VoiceHeartbeat", PrimitiveKind.LONG)

            override fun serialize(encoder: Encoder, value: Heartbeat) {
                encoder.encodeLong(value.data)
            }
        }
    }


    @Serializable
    data class Resume(
            val serverId: String,
            val sessionId: String,
            val token: String
    ) : VoiceCommand()

    @Serializable
    data class SelectProtocol(val protocol: String, val data: SelectProtocolData) : VoiceCommand()

    @Serializable
    data class SelectProtocolData(
            val address: String,
            val port: Int,
            val mode: String
    )

    @Serializable
    data class Speaking(
            val speaking: Int,
            val delay: Int,
            val ssrc: Int
    ) : VoiceCommand()


    companion object : SerializationStrategy<VoiceCommand> {
        override val descriptor = SerialDescriptor("VoiceCommandSerializer") {
            element("op", VoiceOpCode.descriptor)
            element("d", JsonObject.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: VoiceCommand) {
            val composite = encoder.beginStructure(descriptor)
            when (value) {
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), value)
                }

                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Heartbeat)
                    composite.encodeSerializableElement(descriptor, 1, Heartbeat.serializer(), value)
                }

                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), value)
                }

                is SelectProtocol -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.SelectProtocol)
                    composite.encodeSerializableElement(descriptor, 1, SelectProtocol.serializer(), value)
                }
                is Speaking -> {
                    composite.encodeSerializableElement(descriptor, 0, VoiceOpCode, VoiceOpCode.Speaking)
                    composite.encodeSerializableElement(descriptor, 1, Speaking.serializer(), value)
                }

            }
            composite.endStructure(descriptor)
        }
    }

}