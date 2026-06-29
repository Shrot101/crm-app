package com.akashicsoft.crm.platform

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidMessageSender(private val context: android.content.Context) : MessageSender {
    override fun sendMessage(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberMessageSender(): MessageSender {
    val context = LocalContext.current
    return remember(context) { AndroidMessageSender(context) }
}
