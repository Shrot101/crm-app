package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosWhatsAppSender : WhatsAppSender {
    override fun launchWhatsApp(phoneNumber: String) {
        val cleanNumber = phoneNumber.filter { it.isDigit() }
        val url = NSURL(string = "https://wa.me/$cleanNumber")
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

@Composable
actual fun rememberWhatsAppSender(): WhatsAppSender = remember { IosWhatsAppSender() }
