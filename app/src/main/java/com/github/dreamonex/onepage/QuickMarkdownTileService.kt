package com.github.dreamonex.onepage

import android.content.Intent
import android.service.quicksettings.TileService

class QuickMarkdownTileService : TileService() {
    override fun onClick() {
        val launch = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapse(launch)
    }
}
