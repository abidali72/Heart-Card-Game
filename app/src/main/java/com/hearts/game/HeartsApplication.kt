package com.hearts.game

import android.app.Application

class HeartsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: HeartsApplication
            private set
    }
}
