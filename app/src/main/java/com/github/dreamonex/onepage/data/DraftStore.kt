package com.github.dreamonex.onepage.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DraftStore {
    private const val FILE = "onepage.md"
    suspend fun save(ctx: Context, txt: String) = withContext(Dispatchers.IO) {
        ctx.openFileOutput(FILE, Context.MODE_PRIVATE).use { it.write(txt.toByteArray()) }
    }
    suspend fun load(ctx: Context): String = withContext(Dispatchers.IO) {
        runCatching { ctx.openFileInput(FILE).bufferedReader().readText() }.getOrDefault("")
    }
}