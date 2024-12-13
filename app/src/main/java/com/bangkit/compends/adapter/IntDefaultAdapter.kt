package com.bangkit.compends.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class IntDefaultAdapter : TypeAdapter<Int?>() {
    override fun write(out: JsonWriter, value: Int?) {
        out.value(value)
    }

    override fun read(`in`: JsonReader): Int? {
        return try {
            val value = `in`.nextString()
            if (value.isEmpty()) null else value.toInt()
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }
}
