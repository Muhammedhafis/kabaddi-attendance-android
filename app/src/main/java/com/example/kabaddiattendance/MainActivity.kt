package com.example.kabaddiattendance

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KabaddiApp() }
    }

    @Composable
    fun KabaddiApp(vm: AttendanceViewModel = viewModel()) {
        var unlocked by remember { mutableStateOf(false) }
        var pin by remember { mutableStateOf("") }

        if (!unlocked) {
            PinScreen(pin, onPinChange = { pin = it }) {
                if (pin == "0000") unlocked = true
            }
        } else {
            AttendanceScreen(vm)
        }
    }

    @Composable
    fun PinScreen(pin: String, onPinChange: (String) -> Unit, onLogin: () -> Unit) {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Enter Coach PIN", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) onPinChange(it) },
                    placeholder = { Text("0000") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onLogin,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                ) { Text("Login") }
            }
        }
    }

    @Composable
    fun AttendanceScreen(vm: AttendanceViewModel) {
        val state by vm.state.collectAsState()

        Scaffold(
            topBar = { TopAppBar(title = { Text("Kabaddi Attendance") }) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Send") },
                    onClick = {
                        val msg = vm.generateMessage()
                        vm.saveAttendance {
                            val url = "https://wa.me/?text=${Uri.encode(msg)}"
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(Modifier.padding(padding).padding(12.dp)) {
                item { PlayerList(state, vm::setPlayerStatus) }
                item { ExtraPlayerAdder(vm::addExtra) }
                item { HistoryList(state, vm::generateMessage) }
            }
        }
    }

    @Composable
    fun PlayerList(
        state: AttendanceState,
        onStatus: (String, Players.Status) -> Unit
    ) {
        Text("Mark Attendance", fontWeight = FontWeight.Bold)
        Players.list.forEach { name ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        Players.images[name] ?: "https://ui-avatars.com/api/?name=$name"
                    ),
                    contentDescription = name,
                    modifier = Modifier.size(56.dp)
                )
                Text(name, Modifier.weight(1f).padding(start = 8.dp))
                var expanded by remember { mutableStateOf(false) }
                Box {
                    val current = state.statusMap[name] ?: Players.Status.PRESENT
                    OutlinedButton(onClick = { expanded = true }) { Text(current.label) }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        Players.Status.values().forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.label) },
                                onClick = {
                                    onStatus(name, s)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ExtraPlayerAdder(onAdd: (String, Players.Status) -> Unit) {
        var name by remember { mutableStateOf("") }
        var status by remember { mutableStateOf(Players.Status.PRESENT) }
        Spacer(Modifier.height(12.dp))
        Text("Add Extra Player", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Name") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            var expand by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expand = true }) { Text(status.label) }
                DropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                    Players.Status.values().forEach { s ->
                        DropdownMenuItem(text = { Text(s.label) }) {
                            status = s
                            expand = false
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (name.isNotBlank()) {
                    onAdd(name.trim(), status)
                    name = ""
                }
            }) { Text("Add") }
        }
    }

    @Composable
    fun HistoryList(state: AttendanceState, onLoad: () -> String) {
        Spacer(Modifier.height(16.dp))
        Text("Attendance History", fontWeight = FontWeight.Bold)
        Column {
            state.history.forEach { record ->
                Text(
                    "${record.date} (${record.time})",
                    Modifier.fillMaxWidth().clickable {
                        // Quickly view in WhatsApp formatting
                        val msg = with(onLoad) { invoke() } // steal existing generator
                        startActivity(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, msg)
                        })
                    }.padding(8.dp)
                )
                Divider()
            }
        }
    }
}
