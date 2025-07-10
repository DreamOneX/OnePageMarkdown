package com.github.dreamonex.onepage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.dreamonex.onepage.ui.MarkdownEditorScreen
import com.github.dreamonex.onepage.ui.theme.OnePageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OnePageTheme { MarkdownEditorScreen() } }
    }
}