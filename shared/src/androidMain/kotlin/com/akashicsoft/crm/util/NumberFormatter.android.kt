package com.akashicsoft.crm.util

import java.util.Locale

actual object NumberFormatter {
    actual fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "%,.0f", amount)
    }
}
