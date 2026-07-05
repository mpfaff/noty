package com.noty.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.noty.app.data.Note
import com.noty.app.data.NoteType
import com.noty.app.utils.QuickNoteTileService
import com.google.android.material.color.DynamicColors

class AddNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            NotyViewModelFactory(application)
        )[NotyViewModel::class.java]

        setContent {
            NotyTheme {
                NoteBottomSheet(
                    onDismiss = { finish() },
                    onSave = { title, description, isPinned ->
                        viewModel.insert(
                            Note(
                                title = title,
                                description = if (description.isEmpty()) null else description,
                                type = NoteType.NOTE,
                                isPinned = isPinned
                            )
                        )
                        finish()
                    }
                )
            }
        }
    }
}
