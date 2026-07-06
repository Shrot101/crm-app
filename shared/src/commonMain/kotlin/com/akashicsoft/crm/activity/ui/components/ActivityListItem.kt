package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityType

@Composable
fun ActivityListItem(
    activity: Activity,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (activity.type) {
        ActivityType.CALL -> Icons.Default.Call to Color(0xFF27AE60)
        ActivityType.MESSAGE -> Icons.Default.Message to Color(0xFF2D9CDB)
        ActivityType.WHATSAPP -> Icons.Default.Chat to Color(0xFF25D366)
        ActivityType.MEETING -> Icons.Default.Groups to Color(0xFF6E40FF)
        ActivityType.EVENTS -> Icons.Default.Event to Color(0xFFF2994A)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = "${activity.time}${if (activity.organization != null) " • ${activity.organization}" else ""}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Gray)
            }
        }
    }
}
