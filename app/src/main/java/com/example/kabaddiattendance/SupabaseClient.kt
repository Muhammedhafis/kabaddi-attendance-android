package com.example.kabaddiattendance

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

object Supa {
    private const val url =
        "https://yevffbfhcbovzqldncsl.supabase.co"          //  ⚠️ YOUR URL
    private const val key =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."           //  ⚠️ YOUR anon key

    val client = SupabaseClient {
        supabaseUrl = url
        supabaseKey = key
        install(io.github.jan.supabase.postgrest.Postgrest)
        install(io.github.jan.supabase.realtime.Realtime)
    }

    // ---------- data models ----------
    @Serializable
    data class Attendance(
        val id: Int? = null,
        val date: String,
        val time: String,
        val present: List<String>,
        val late: List<String>,
        val absent: List<String>,
        val excused: List<String>,
        val extra: List<ExtraPlayer>
    )

    @Serializable
    data class ExtraPlayer(val name: String, val status: String)
}
