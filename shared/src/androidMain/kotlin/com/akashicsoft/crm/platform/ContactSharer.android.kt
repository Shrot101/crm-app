package com.akashicsoft.crm.platform

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.akashicsoft.crm.model.Contact
import com.akashicsoft.crm.util.VCardGenerator
import java.io.File

class AndroidContactSharer(private val context: Context) : ContactSharer {
    override fun shareContact(contact: Contact) {
        val vCardString = VCardGenerator.generate(contact)
        val fileName = "${contact.name.replace(" ", "_")}.vcf"
        val file = File(context.cacheDir, fileName)
        
        try {
            file.writeText(vCardString)
            
            val contentUri = FileProvider.getUriForFile(
                context,
                "com.akashicsoft.crm.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/vcard"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(shareIntent, "Share Contact")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
actual fun rememberContactSharer(): ContactSharer {
    val context = LocalContext.current
    return remember(context) { AndroidContactSharer(context) }
}
