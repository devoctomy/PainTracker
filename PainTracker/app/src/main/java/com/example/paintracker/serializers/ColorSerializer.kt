package com.example.paintracker.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ColorSerializer : KSerializer<Int> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Int) {
        // Serialize as hex code for consistency
        val hexColor = String.format("#%08X", value)
        encoder.encodeString(hexColor)
    }

    override fun deserialize(decoder: Decoder): Int {
        val colorValue = decoder.decodeString()
        if (!colorValue.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?"))) {
            throw IllegalArgumentException("Unsupported color format: $colorValue")
        }
        return parseHexColor(colorValue)
    }

    private fun parseHexColor(hex: String): Int {
        val value = hex.substring(1).toLong(16) // Parse as a Long
        if (value > 0xFFFFFFFFL) {
            throw IllegalArgumentException("Hex color value out of range: $hex")
        }
        return value.toInt() // Safely cast to Int
    }
}