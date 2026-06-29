package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable

interface EmailSender {
    fun sendEmail(email: String)
}

@Composable
expect fun rememberEmailSender(): EmailSender
