package com.akashicsoft.crm.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.activity.data.FakeActivityData
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.model.CalendarUiState
import com.akashicsoft.crm.activity.util.CalendarUtils
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate
import kotlin.random.Random

class ActivityViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(CalendarUtils.today())

    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        FakeActivityData.activities
    ) { date, allActivities ->
        CalendarUiState(
            selectedDate = date,
            visibleWeek = CalendarUtils.getWeek(date),
            monthTitle = CalendarUtils.monthTitle(date),
            activities = allActivities.filter { it.date == date }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = buildCalendarUiState(CalendarUtils.today())
    )

    val showTodayButton: StateFlow<Boolean> = _selectedDate.map { date ->
        date != CalendarUtils.today()
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
            activities = FakeActivityData.getMockActivities(selectedDate, CalendarUtils.today())
        )
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun goToToday() {
        onDateSelected(CalendarUtils.today())
    }

    fun toggleTaskCompletion(activityId: String) {
        FakeActivityData.toggleTaskCompletion(activityId)
    }

    fun cloneActivity(activityId: String) {
        val activityToClone = uiState.value.activities.find { it.id == activityId }
        if (activityToClone != null) {
            val clonedActivity = activityToClone.copy(
                id = "cloned_${activityToClone.id}_${Random.nextInt()}",
                title = "${activityToClone.title} (Clone)",
                isCompleted = false
            )
            FakeActivityData.addActivity(clonedActivity)
        }
    }

    fun deleteActivity(activityId: String) {
        FakeActivityData.deleteActivity(activityId)
    }

    fun rescheduleActivity(activityId: String, newDate: LocalDate, newTime: String? = null) {
        val activity = FakeActivityData.activities.value.find { it.id == activityId }
        if (activity != null) {
            val updatedActivity = if (newTime != null) {
                activity.copy(date = newDate, time = newTime)
            } else {
                activity.copy(date = newDate)
            }
            FakeActivityData.updateActivity(updatedActivity)
            onDateSelected(newDate)
        }
    }
    
    fun addActivity(type: ActivityType) {
        // Implementation placeholder for future Milestones
    }
}
