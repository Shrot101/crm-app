package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosMessageSender : MessageSender {
    override fun sendMessage(phoneNumber: String) {
        val url = NSURL(string = "sms:$phoneNumber")
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

@Composable
actual fun rememberMessageSender(): MessageSender = remember { IosMessageSender() }
