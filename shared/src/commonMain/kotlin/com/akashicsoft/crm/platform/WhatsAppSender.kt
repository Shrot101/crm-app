package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable

interface WhatsAppSender {
    fun launchWhatsApp(phoneNumber: String)
}

@Composable
expect fun rememberWhatsAppSender(): WhatsAppSender
