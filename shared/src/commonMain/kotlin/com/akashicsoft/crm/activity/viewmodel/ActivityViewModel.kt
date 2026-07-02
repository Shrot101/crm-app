package com.akashicsoft.crm.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.activity.model.Activity
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.model.CalendarUiState
import com.akashicsoft.crm.activity.util.CalendarUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

class ActivityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        buildCalendarUiState(CalendarUtils.today())
    )
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    val showTodayButton: StateFlow<Boolean> = _uiState.map { state ->
        state.selectedDate != CalendarUtils.today()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
    
    private fun buildCalendarUiState(selectedDate: LocalDate): CalendarUiState {
        return CalendarUiState(
            selectedDate = selectedDate,
            visibleWeek = CalendarUtils.getWeek(selectedDate),
            monthTitle = CalendarUtils.monthTitle(selectedDate),
            activities = getMockActivities(selectedDate)
        )
    }

    private fun getMockActivities(date: LocalDate): List<Activity> {
        return if (date == CalendarUtils.today()) {
            listOf(
                Activity(
                    id = "1",
                    title = "Demo call",
                    description = "Initial product demonstration",
                    time = "10:00 AM",
                    duration = "30m",
                    organization = "Nova Retail Corp.",
                    type = ActivityType.CALL,
                    date = date
                ),
                Activity(
                    id = "2",
                    title = "Send proposal",
                    description = "Follow-up with quarterly proposal",
                    time = "12:30 PM",
                    duration = "Task",
                    organization = "Skyline Enterprises",
                    type = ActivityType.TASK,
                    date = date
                ),
                Activity(
                    id = "3",
                    title = "Lunch meeting",
                    description = "Discuss potential partnership",
                    time = "01:30 PM",
                    duration = "1h",
                    organization = "Global Tech Solutions",
                    type = ActivityType.MEETING,
                    date = date
                )
            )
        } else {
            emptyList()
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = buildCalendarUiState(date)
    }

    fun goToToday() {
        onDateSelected(CalendarUtils.today())
    }

    fun addActivity(type: ActivityType) {
        // Implementation placeholder for future Milestones
    }
}
