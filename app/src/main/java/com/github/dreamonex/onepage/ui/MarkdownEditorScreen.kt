package com.github.dreamonex.onepage.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.dreamonex.onepage.data.DraftStore
import com.github.dreamonex.onepage.undo.UndoTree
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
fun MarkdownEditorScreen(ctx: Context = LocalContext.current) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    val tree = remember { UndoTree() }

    LaunchedEffect(Unit) {
        text = DraftStore.load(ctx)
        tree.commit(text)
    }

    fun commit(newText: String) {
        tree.commit(newText)
        text = newText
        scope.launch { DraftStore.save(ctx, newText) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { if (tree.canUndo()) text = tree.undo() }) {
                Text("↶")
            }
        }
    ) { pad ->
        Row(
            Modifier.fillMaxSize().padding(pad)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { commit(it) },
                modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                placeholder = { Text("开始写 Markdown...") }
            )
            Divider(Modifier.width(1.dp).fillMaxHeight())
            MarkdownText(
                markdown = text,
                modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp).verticalScroll(rememberScrollState())
            )
        }
    }
}