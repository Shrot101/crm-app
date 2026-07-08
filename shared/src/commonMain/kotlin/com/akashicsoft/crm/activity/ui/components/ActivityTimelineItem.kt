package com.akashicsoft.crm.activity.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityType

@Composable
fun ActivityTimelineItem(
    activity: Activity,
    isFirst: Boolean,
    isLast: Boolean,
    onMenuClick: () -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (activity.type) {
        ActivityType.CALL -> Icons.Default.Call to Color(0xFF27AE60)
        ActivityType.MESSAGE -> Icons.Default.Message to Color(0xFF2D9CDB)
        ActivityType.WHATSAPP -> Icons.Default.Chat to Color(0xFF25D366)
        ActivityType.MEETING -> Icons.Default.Groups to Color(0xFF6E40FF)
        ActivityType.EVENTS -> Icons.Default.Event to Color(0xFFF2994A)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(if (isFirst) Color.Transparent else Color.LightGray.copy(alpha = 0.5f))
            )

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .weight(1f)
                    .background(if (isLast) Color.Transparent else Color.LightGray.copy(alpha = 0.5f))
                    .defaultMinSize(minHeight = 40.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            onClick = onItemClick,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.time,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = activity.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                activity.organization?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
