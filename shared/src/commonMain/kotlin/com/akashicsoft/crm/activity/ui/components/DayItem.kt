package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.activity.util.CalendarUtils
import kotlinx.datetime.LocalDate

private val PrimaryPurple = Color(0xFF6E40FF)
private val SelectedDayBg = Color(0xFF3B229D) // Darker purple from image
private val IndicatorGray = Color(0xFFBDBDBD)
private val WeekendGray = Color(0xFFBDBDBD)

@Composable
fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasActivity: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayName = CalendarUtils.dayLabel(date)
    val dayNumber = date.day.toString()
    val isWeekend = date.dayOfWeek.name == "SATURDAY" || date.dayOfWeek.name == "SUNDAY"

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SelectedDayBg else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else if (isWeekend) WeekendGray else Color.Gray
        )

        Text(
            text = dayNumber,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else if (isWeekend) WeekendGray else Color.Black
        )

        // Activity indicator dot
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else IndicatorGray)
        )
    }
}
