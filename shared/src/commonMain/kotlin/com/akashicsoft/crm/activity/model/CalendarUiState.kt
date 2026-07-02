package com.akashicsoft.crm.activity.model

import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val selectedDate: LocalDate,
    val visibleWeek: List<LocalDate>,
    val monthTitle: String,
    val activities: List<Activity> = emptyList(),
    val isLoading: Boolean = false
)
