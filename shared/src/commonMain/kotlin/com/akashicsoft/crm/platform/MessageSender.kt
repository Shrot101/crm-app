package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable

interface MessageSender {
    fun sendMessage(phoneNumber: String)
}

@Composable
expect fun rememberMessageSender(): MessageSender
