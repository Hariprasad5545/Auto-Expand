package com.example.autoexpand

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class AppInfo(val packageName: String, val label: String, var selected: Boolean = false)

class MainActivity : ComponentActivity() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager(applicationContext)

        setContent {
            val pm = packageManager
            var enabled by remember { mutableStateOf(settingsManager.isEnabled()) }
            var delaySec by remember { mutableStateOf(settingsManager.getDelayMillis().toFloat() / 1000f) }
            var apps by remember { mutableStateOf(listOf<AppInfo>()) }

            LaunchedEffect(true) {
                apps = loadAllApps(pm, settingsManager)
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Column {
                        Button(onClick = {
                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        }) {
                            Text(text = getString(R.string.open_accessibility))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Auto-expand notifications")
                            Switch(checked = enabled, onCheckedChange = {
                                enabled = it
                                settingsManager.setEnabled(it)
                                enabled = settingsManager.isEnabled()
                            })
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "${'$'}{getString(R.string.delay_label)}: ${'$'}{String.format("%.1f", delaySec)} s")
                        Slider(value = delaySec, onValueChange = {
                            delaySec = it
                        }, valueRange = 0.0f..3.0f, steps = 5, modifier = Modifier.fillMaxWidth())
                        Button(onClick = {
                            settingsManager.setDelayMillis((delaySec * 1000).toLong())
                        }) {
                            Text(text = "Save delay")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = getString(R.string.select_apps))
                        // Select All / Deselect All Buttons
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                apps = apps.map { it.copy(selected = true) }
                                settingsManager.setSelectedApps(apps.map { it.packageName }.toSet())
                            }) {
                                Text(text = "Select All")
                            }
                            Button(onClick = {
                                apps = apps.map { it.copy(selected = false) }
                                settingsManager.setSelectedApps(emptySet())
                            }) {
                                Text(text = "Deselect All")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.fillMaxHeight()) {
                            items(apps) { app ->
                                AppRow(app) { pkg, sel ->
                                    // update and persist selection set
                                    val newList = apps.map {
                                        if (it.packageName == pkg) it.copy(selected = sel) else it
                                    }
                                    apps = newList
                                    val selectedSet = newList.filter { it.selected }.map { it.packageName }.toSet()
                                    settingsManager.setSelectedApps(selectedSet)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadAllApps(pm: PackageManager, settings: SettingsManager): List<AppInfo> {
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val selected = settings.getSelectedApps()
        val list = installed.mapNotNull { ai ->
            val label = ai.loadLabel(pm)?.toString() ?: return@mapNotNull null
            AppInfo(ai.packageName, label, selected.contains(ai.packageName))
        }.sortedBy { it.label.lowercase() }
        return list
    }
}

@Composable
fun AppRow(app: AppInfo, onToggle: (String, Boolean) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onToggle(app.packageName, !app.selected) },
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = app.label)
        Row {
            Text(text = app.packageName, modifier = Modifier.padding(end = 8.dp))
            Checkbox(checked = app.selected, onCheckedChange = { checked -> onToggle(app.packageName, checked) })
        }
    }
}
