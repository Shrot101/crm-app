package com.akashicsoft.crm.platform

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidEmailSender(private val context: android.content.Context) : EmailSender {
    override fun sendEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberEmailSender(): EmailSender {
    val context = LocalContext.current
    return remember(context) { AndroidEmailSender(context) }
}
