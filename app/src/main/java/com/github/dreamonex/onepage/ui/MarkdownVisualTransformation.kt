package com.github.dreamonex.onepage.ui

import kotlin.text.Regex
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class MarkdownVisualTransformation : VisualTransformation {

    private val bold = Regex("\\*\\*(.+?)\\*\\*")
    private val italic = Regex("\\*(.+?)\\*")
    private val code = Regex("`(.+?)`")

    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val builder = AnnotatedString.Builder()

        val o2t = IntArray(src.length + 1)
        val t2o = mutableListOf<Int>()

        var s = 0
        fun copy(c: Char) {
            builder.append(c)
            o2t[s] = t2o.size
            t2o += s
            s++
        }

        while (s < src.length) {
            val match = listOf(bold, italic, code)
                .mapNotNull { regex ->
                    regex.find(src, s)?.let { regex to it }
                }
                .minByOrNull { (_, result) -> result.range.first }

            if (match == null || match.second.range.first != s) {
                copy(src[s])
                continue
            }

            val (regex, result) = match

            repeat(result.value.takeWhile { it == '*' || it == '`' }.length) {
                o2t[s++] = t2o.size
            }

            val content = result.groupValues[1]
            val style = when (regex) {
                bold -> SpanStyle(fontWeight = FontWeight.Bold)
                italic -> SpanStyle(fontStyle = FontStyle.Italic)
                else -> SpanStyle(
                    background = Color(0xFFE0E0E0),
                    fontFamily = FontFamily.Monospace
                )
            }
            builder.withStyle(style) {
                content.forEach { c -> copy(c) }
            }

            repeat(result.value.takeLastWhile { it == '*' || it == '`' }.length) {
                o2t[s++] = t2o.size
            }
        }

        o2t[src.length] = t2o.size
        t2o += src.length

        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                o2t.getOrElse(offset) { t2o.size }

            override fun transformedToOriginal(offset: Int): Int =
                t2o.getOrElse(offset) { src.length }
        }

        return TransformedText(builder.toAnnotatedString(), mapping)
    }
}
