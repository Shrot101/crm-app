package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akashicsoft.crm.activity.model.Reminder

@Composable
fun ReminderChip(reminder: Reminder, onRemove: () -> Unit) {
    SuggestionChip(
        onClick = onRemove,
        label = { Text(reminder.type) },
        icon = { Icon(Icons.Default.Notifications, null, modifier = Modifier.size(16.dp)) }
    )
}
