package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityType

@Composable
fun ActivityTimelineItem(
    activity: Activity,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (activity.type) {
        ActivityType.CALL -> Icons.Default.Call to Color(0xFF27AE60)
        ActivityType.MEETING -> Icons.Default.Groups to Color(0xFF2D9CDB)
        ActivityType.TASK -> Icons.Default.TaskAlt to Color(0xFF3B229D)
        else -> Icons.Default.TaskAlt to Color.Gray
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline Column (Icon + Vertical Line)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            // Top portion of line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(12.dp)
                    .background(if (isFirst) Color.Transparent else Color.LightGray.copy(alpha = 0.5f))
            )

            // Icon Circle
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

            // Bottom portion of line
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .weight(1f)
                    .background(if (isLast) Color.Transparent else Color.LightGray.copy(alpha = 0.5f))
                    .minHeight(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Activity Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
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

                    activity.duration?.let {
                        Surface(
                            color = Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = it,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
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

// Utility extension for background line
private fun Modifier.minHeight(height: androidx.compose.ui.unit.Dp): Modifier = this.defaultMinSize(minHeight = height)
