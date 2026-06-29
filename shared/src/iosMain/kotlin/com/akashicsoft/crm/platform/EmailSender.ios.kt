package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosEmailSender : EmailSender {
    override fun sendEmail(email: String) {
        val url = NSURL(string = "mailto:$email")
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

@Composable
actual fun rememberEmailSender(): EmailSender = remember { IosEmailSender() }
