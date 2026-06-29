package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosPhoneCaller : PhoneCaller {
    override fun call(phoneNumber: String) {
        val url = NSURL(string = "tel:$phoneNumber")
        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

@Composable
actual fun rememberPhoneCaller(): PhoneCaller = remember { IosPhoneCaller() }
