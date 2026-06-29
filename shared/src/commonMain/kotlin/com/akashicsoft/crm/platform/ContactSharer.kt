package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import com.akashicsoft.crm.model.Contact

interface ContactSharer {
    fun shareContact(contact: Contact)
}

@Composable
expect fun rememberContactSharer(): ContactSharer
