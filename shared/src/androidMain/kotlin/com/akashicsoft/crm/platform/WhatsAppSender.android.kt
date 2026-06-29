package com.akashicsoft.crm.platform

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidWhatsAppSender(private val context: android.content.Context) : WhatsAppSender {
    override fun launchWhatsApp(phoneNumber: String) {
        // Clean the phone number: keep only digits
        val cleanNumber = phoneNumber.filter { it.isDigit() }
        val url = "https://wa.me/$cleanNumber"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

@Composable
actual fun rememberWhatsAppSender(): WhatsAppSender {
    val context = LocalContext.current
    return remember(context) { AndroidWhatsAppSender(context) }
}
