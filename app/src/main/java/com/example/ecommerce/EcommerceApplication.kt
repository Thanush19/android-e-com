package com.example.ecommerce

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EcommerceApplication : Application() {
    // Hilt will handle dependency injection, so we don't need AppContainer anymore
}
