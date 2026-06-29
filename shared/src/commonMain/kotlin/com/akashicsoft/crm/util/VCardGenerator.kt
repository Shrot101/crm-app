package com.akashicsoft.crm.util

import com.akashicsoft.crm.model.Contact

object VCardGenerator {
    fun generate(contact: Contact): String {
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            appendLine("FN:${contact.name}")
            appendLine("EMAIL;TYPE=INTERNET;TYPE=HOME:${contact.email}")
            appendLine("TEL;TYPE=CELL:${contact.mobileNumber}")
            
            contact.landlineNumber?.let {
                appendLine("TEL;TYPE=WORK:${it}")
            }
            
            appendLine("ORG:${contact.organization}")
            appendLine("TITLE:${contact.designation}")
            
            contact.department?.let {
                appendLine("X-DEPARTMENT:${it}")
            }
            
            appendLine("END:VCARD")
        }
    }
}
