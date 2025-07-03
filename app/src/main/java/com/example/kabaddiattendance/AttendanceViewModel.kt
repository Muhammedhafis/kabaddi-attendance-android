package com.example.kabaddiattendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceViewModel : ViewModel() {

    private val _state = MutableStateFlow(AttendanceState())
    val state: StateFlow<AttendanceState> = _state.asStateFlow()

    init { loadHistory() }

    fun setPlayerStatus(name: String, status: Players.Status) {
        _state.update { it.copy(statusMap = it.statusMap + (name to status)) }
    }

    fun addExtra(name: String, status: Players.Status) {
        val newExtra = Supa.ExtraPlayer(name, status.name.lowercase())
        _state.update { it.copy(extra = it.extra + newExtra) }
    }

    fun generateMessage(): String {
        val dfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val dfTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val date = dfDate.format(Date())
        val time = dfTime.format(Date())

        val grouped = Players.Status.values().associateWith { mutableListOf<String>() }

        Players.list.forEach { n ->
            val s = _state.value.statusMap[n] ?: Players.Status.PRESENT
            grouped[s]?.add(n)
        }
        _state.value.extra.forEach { e ->
            grouped[Players.Status.valueOf(e.status.uppercase())]?.add(e.name)
        }

        fun section(st: Players.Status, icon: String) =
            if (grouped[st]!!.isEmpty()) "" else
                "$icon *${st.name.lowercase().replaceFirstChar(Char::uppercase)} " +
                "(${grouped[st]!!.size}):*\n" +
                grouped[st]!!.joinToString("\n") { "${icon.trim()} $it" } + "\n\n"

        return buildString {
            append("🟢 *Kabaddi Attendance - $date ($time)*\n\n")
            append(section(Players.Status.PRESENT, "✅"))
            append(section(Players.Status.LATE, "🕒"))
            append(section(Players.Status.EXCUSED, "🟡"))
            append(section(Players.Status.ABSENT, "❌"))
        }
    }

    fun saveAttendance(onDone: () -> Unit) = viewModelScope.launch {
        val dfDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val dfTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val date = dfDate.format(Date())
        val time = dfTime.format(Date())

        val present = _state.value.ofStatus(Players.Status.PRESENT)
        val absent = _state.value.ofStatus(Players.Status.ABSENT)
        val late = _state.value.ofStatus(Players.Status.LATE)
        val excused = _state.value.ofStatus(Players.Status.EXCUSED)

        Supa.client.from("attendance").insert(
            Supa.Attendance(
                date = date,
                time = time,
                present = present,
                late = late,
                absent = absent,
                excused = excused,
                extra = _state.value.extra
            )
        )
        loadHistory()
        onDone()
    }

    private fun loadHistory() = viewModelScope.launch {
        val result = Supa.client.from("attendance")
            .select().order("created_at", ascending = false).limit(20).decodeList<Supa.Attendance>()
        _state.update { it.copy(history = result) }
    }
}

data class AttendanceState(
    val statusMap: Map<String, Players.Status> = emptyMap(),
    val extra: List<Supa.ExtraPlayer> = emptyList(),
    val history: List<Supa.Attendance> = emptyList()
) {
    fun ofStatus(s: Players.Status) =
        statusMap.filterValues { it == s }.keys + extra.filter { it.status == s.name.lowercase() }
}
