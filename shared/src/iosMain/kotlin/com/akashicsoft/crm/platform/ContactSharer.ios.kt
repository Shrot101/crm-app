package com.akashicsoft.crm.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.akashicsoft.crm.model.Contact
import com.akashicsoft.crm.util.VCardGenerator
import platform.Foundation.*
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import kotlinx.cinterop.BetaInteropApi

class IosContactSharer : ContactSharer {
    @OptIn(BetaInteropApi::class)
    override fun shareContact(contact: Contact) {
        val vCardString = VCardGenerator.generate(contact)
        val fileName = "${contact.name.replace(" ", "_")}.vcf"
        val tempDir = NSTemporaryDirectory()
        val fileURL = NSURL.fileURLWithPath(tempDir).URLByAppendingPathComponent(fileName)
        
        if (fileURL == null) return

        val nsString = platform.Foundation.NSString.create(string = vCardString)
        val data = nsString.dataUsingEncoding(NSUTF8StringEncoding)
        
        data?.writeToURL(fileURL, true)

        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileURL),
            applicationActivities = null
        )

        val window = UIApplication.sharedApplication.windows.first() as? UIWindow
        window?.rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}

@Composable
actual fun rememberContactSharer(): ContactSharer = remember { IosContactSharer() }
