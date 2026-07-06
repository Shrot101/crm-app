package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.Participant
import com.akashicsoft.crm.activity.model.ParticipantType

@Composable
fun ParticipantChip(participant: Participant, onRemove: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FE),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = when(participant.type) {
                    ParticipantType.LEAD -> Color(0xFF2D9CDB)
                    ParticipantType.CONTACT -> Color(0xFF6200EE)
                    else -> Color.Gray
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = participant.name.firstOrNull()?.toString() ?: "",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(participant.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(participant.type.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
