package com.akashicsoft.crm.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual object NumberFormatter {
    actual fun formatCurrency(amount: Double): String {
        val formatter = NSNumberFormatter()
        formatter.numberStyle = NSNumberFormatterDecimalStyle
        formatter.maximumFractionDigits = 0u
        return formatter.stringFromNumber(NSNumber(amount)) ?: amount.toString()
    }
}
