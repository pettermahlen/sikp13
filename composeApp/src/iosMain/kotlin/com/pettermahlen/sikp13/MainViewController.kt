package com.pettermahlen.sikp13

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    // Initialize the database before creating the view
    DatabaseProvider.initialize(IosDriverFactory())
    App()
}