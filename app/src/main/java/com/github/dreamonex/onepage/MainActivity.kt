package com.github.dreamonex.onepage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable           // rememberSaveable ✔
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey   // 使用 stringPreferencesKey ✔
import androidx.datastore.preferences.preferencesDataStore
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/* DataStore delegate */
private val Context.dataStore by preferencesDataStore(name = "draft")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OnePageApp() }
    }
}

@Composable
fun OnePageApp() {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) darkColorScheme() else lightColorScheme()
    ) { EditorScreen() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen() {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val draftKey = stringPreferencesKey("draft")

    var text by rememberSaveable { mutableStateOf("") }

    /* 读取草稿 */
    LaunchedEffect(Unit) {
        text = context.dataStore.data.first()[draftKey] ?: ""
    }

    /* 节流保存 */
    LaunchedEffect(text) {
        snapshotFlow { text }
            .debounce(500)
            .collect { latest ->
                context.dataStore.edit { it[draftKey] = latest }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OnePage Markdown") },
                actions = {
                    IconButton(onClick = {
                        clipboard.setText(AnnotatedString(text))
                    }) { Icon(Icons.Default.ContentCopy, "Copy") }

                    IconButton(onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/markdown"
                            putExtra(Intent.EXTRA_TEXT, text as String)  // 指明 String，避免重载歧义
                        }
                        context.startActivity(Intent.createChooser(share, "Share Markdown"))
                    }) { Icon(Icons.Default.Share, "Share") }
                },
                modifier = Modifier.padding(4.dp)
            )
        }
    ) { inner ->
        MarkdownText(
            markdown = text,
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        )
    }
}
