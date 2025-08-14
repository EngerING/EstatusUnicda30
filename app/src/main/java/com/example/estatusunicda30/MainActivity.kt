package com.example.estatusunicda30

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.estatusunicda30.ui.theme.nav.AppNav
import com.example.estatusunicda30.ui.theme.EstatusTheme


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EstatusTheme { AppNav() } }
    }
}