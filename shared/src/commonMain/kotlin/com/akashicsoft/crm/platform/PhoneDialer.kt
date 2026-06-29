package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable

interface PhoneCaller {
    fun call(phoneNumber: String)
}

@Composable
expect fun rememberPhoneCaller(): PhoneCaller
