package com.github.dreamonex.onepage.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * 精简版 Markdown 所见即所得可视化转换，仅示范双星号 **粗体** 的定界符删除。
 * 保留完善的 offset 映射，避免光标错位 / 崩溃。
 */
class MarkdownVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val builder = StringBuilder()

        /* original -> transformed 映射表；下标即 original offset，值为 transformed offset */
        val o2t = IntArray(src.length + 1)
        /* transformed -> original；index 为 transformed offset，值为 original offset */
        val t2o = ArrayList<Int>(src.length + 1)

        var s = 0   // original offset
        var t = 0   // transformed offset

        fun append(c: Char) {
            builder.append(c)
            o2t[s] = t
            t2o.add(s)
            s++; t++
        }

        while (s < src.length) {
            if (s + 1 < src.length && src[s] == '*' && src[s + 1] == '*') {
                /* 删除一对 ** 定界符，但写入占位保持映射表长度一致 */
                repeat(2) {
                    o2t[s] = t2o.size
                    t2o.add(s)
                    s++
                }
            } else {
                append(src[s])
            }
        }

        /* 终止位 */
        o2t[src.length] = t
        t2o.add(src.length)

        val transformed = AnnotatedString(builder.toString())
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                o2t[offset.coerceIn(0, src.length)]

            override fun transformedToOriginal(offset: Int): Int =
                t2o[offset.coerceIn(0, t2o.lastIndex)]
        }

        return TransformedText(transformed, mapping)
    }
}
