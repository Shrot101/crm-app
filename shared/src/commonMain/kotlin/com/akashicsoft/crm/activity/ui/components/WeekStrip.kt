package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.akashicsoft.crm.activity.util.CalendarUtils
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
fun WeekStrip(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onWeekSwiped: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // Large number to simulate infinite scrolling
    val pageCount = 10000
    val initialPage = 5000
    
    // The anchor date for the middle of the pager (index 5000)
    val anchorDate = remember { selectedDate }
    
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }
    
    // Sync pager when selectedDate changes from outside (e.g., Today button)
    LaunchedEffect(selectedDate) {
        val daysDiff = selectedDate.toEpochDays() - anchorDate.toEpochDays()
        val weeksDiff = (daysDiff / 7).toInt()
        val targetPage = (initialPage + weeksDiff).toInt()
        
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    // Trigger onWeekSwiped when page changes
    LaunchedEffect(pagerState.currentPage) {
        val weeksDiff = pagerState.currentPage - initialPage
        val targetDate = anchorDate.plus(weeksDiff * 7L, DateTimeUnit.DAY)
        
        // Check if swiped to a different week
        val currentWeek = CalendarUtils.getWeek(selectedDate)
        val targetWeek = CalendarUtils.getWeek(targetDate)
        
        if (currentWeek != targetWeek) {
            onWeekSwiped(targetDate)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        beyondViewportPageCount = 1 
    ) { page ->
        val weeksDiff = page - initialPage
        val weekBaseDate = anchorDate.plus(weeksDiff * 7L, DateTimeUnit.DAY)
        val week = CalendarUtils.getWeek(weekBaseDate)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            week.forEach { date ->
                DayItem(
                    modifier = Modifier.weight(1f),
                    date = date,
                    isSelected = date == selectedDate,
                    isToday = CalendarUtils.isToday(date),
                    hasActivity = false,
                    onClick = { onDateSelected(date) }
                )
            }
        }
    }
}
