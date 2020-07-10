package dev.kord.voice

import kotlinx.serialization.*

enum class VoiceOpCode(val code: Int) {

    Unknown(Int.MAX_VALUE),
    Identify(0),
    SelectProtocol(1),
    Ready(2),
    Heartbeat(3),
    SessionDescription(4),
    Speaking(5),
    HearbeatACK(6),
    Resume(7),
    Hello(8),
    Resumed(9),
    ClientDisconnect(13);

    @Serializer(forClass = VoiceOpCode::class)
    companion object VoiceOpCodeSerializer : KSerializer<VoiceOpCode> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("VoiceOp", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): VoiceOpCode {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: VoiceOpCode) {
            encoder.encodeInt(obj.code)
        }
    }

}