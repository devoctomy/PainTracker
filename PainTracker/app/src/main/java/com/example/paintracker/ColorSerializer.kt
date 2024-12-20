package com.example.paintracker

import android.graphics.Color
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
        return try {
            Color.parseColor(colorValue)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Unsupported color format: $colorValue", e)
        }
    }
}