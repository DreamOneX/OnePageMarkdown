package com.github.dreamonex.onepage.ui

import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MarkdownVisualTransformation : VisualTransformation {

    private val bold   = Regex("\\*\\*(.+?)\\*\\*")
    private val italic = Regex("\\*(.+?)\\*")
    private val code   = Regex("`(.+?)`")

    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val builder = AnnotatedString.Builder()
        var idx = 0
        while (idx < src.length) {
            val matches = listOf(bold, italic, code).mapNotNull { r -> r.find(src, idx)?.let { r to it } }
            if (matches.isEmpty()) {
                builder.append(src.substring(idx))
                break
            }
            val (regexUsed, m) = matches.minBy { it.second.range.first }
            if (m.range.first > idx) builder.append(src.substring(idx, m.range.first))
            val content = m.groupValues[1]
            val style = when (regexUsed) {
                bold   -> SpanStyle(fontWeight = FontWeight.Bold)
                italic -> SpanStyle(fontStyle = FontStyle.Italic)
                else   -> SpanStyle(background = Color(0xFFE0E0E0), fontFamily = FontFamily.Monospace)
            }
            builder.withStyle(style) { append(content) }
            idx = m.range.last + 1
        }
        return TransformedText(builder.toAnnotatedString(), OffsetMapping.Identity)
    }
}