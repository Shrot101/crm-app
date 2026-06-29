package com.akashicsoft.crm.util

expect object NumberFormatter {
    fun formatCurrency(amount: Double): String
}
