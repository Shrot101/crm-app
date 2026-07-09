package com.akashicsoft.crm.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.activity.data.FakeActivityData
import com.akashicsoft.crm.activity.model.ActivityType
import com.akashicsoft.crm.activity.model.CalendarUiState
import com.akashicsoft.crm.activity.util.CalendarUtils
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlin.random.Random

class ActivityViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(CalendarUtils.today())
    private val _currentFilter = MutableStateFlow<Map<String, String>>(emptyMap())

    fun setFilter(filter: Map<String, String>) {
        _currentFilter.value = filter
    }

    val uiState: StateFlow<CalendarUiState> = combine(
        _selectedDate,
        FakeActivityData.activities,
        _currentFilter
    ) { date, allActivities, filters ->
        val filteredActivities = allActivities.filter { activity ->
            val startDate = activity.date
            val endDate = activity.endDate ?: activity.date
            val dateMatches = date in startDate..endDate
            val filterMatches = matchesFilter(activity, filters)
            dateMatches && filterMatches
        }.sortedWith(compareBy({ activity ->
            // 1. Ongoing multi-day activities (started before today) go to top
            val isOngoing = activity.date < date
            if (isOngoing) -2 else 0
        }, { activity ->
            // 2. All Day / Timed activities
            CalendarUtils.parseTimeToMinutes(activity.time)
        }))

        CalendarUiState(
            selectedDate = date,
            visibleWeek = CalendarUtils.getWeek(date),
            monthTitle = CalendarUtils.monthTitle(date),
            activities = filteredActivities
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

    val datesWithActivities: StateFlow<Set<LocalDate>> = combine(
        FakeActivityData.activities,
        _currentFilter
    ) { activities, filters ->
        val dateSet = mutableSetOf<LocalDate>()
        activities.forEach { activity ->
            if (matchesFilter(activity, filters)) {
                val start = activity.date
                val end = activity.endDate ?: activity.date

                var current = start
                while (current <= end) {
                    dateSet.add(current)
                    current = current.plus(1, DateTimeUnit.DAY)
                }
            }
        }
        dateSet
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    private fun matchesFilter(activity: com.akashicsoft.crm.activity.model.Activity, filters: Map<String, String>): Boolean {
        if (filters.isEmpty()) return true

        return filters.all { (key, value) ->
            if (value.isBlank() || value == "Select Type" || value == "Select Source" || 
                value == "Select Priority" || value == "Select Reminder" || 
                value == "From Date" || value == "To Date") return@all true
            
            when (key) {
                "title" -> activity.title.contains(value, ignoreCase = true)
                "type" -> activity.type.name.equals(value, ignoreCase = true)
                "source" -> activity.source?.name?.equals(value, ignoreCase = true) ?: false
                "priority" -> activity.priority?.name?.equals(value, ignoreCase = true) ?: false
                "contact" -> activity.contact?.name?.equals(value, ignoreCase = true) ?: false
                "organization" -> activity.organization?.contains(value, ignoreCase = true) ?: false
                "deal" -> activity.deal?.title?.equals(value, ignoreCase = true) ?: false
                "location" -> activity.location?.contains(value, ignoreCase = true) ?: false
                "reminder" -> activity.reminder?.equals(value, ignoreCase = true) ?: false
                "agenda" -> activity.agenda?.contains(value, ignoreCase = true) ?: false
                "product" -> activity.product?.contains(value, ignoreCase = true) ?: false
                "assignedTo" -> activity.assignedTo?.name?.equals(value, ignoreCase = true) ?: false
                "notes" -> activity.description.contains(value, ignoreCase = true)
                "dateFrom" -> {
                    try {
                        val fromDate = LocalDate.parse(value)
                        activity.date >= fromDate
                    } catch (e: Exception) { true }
                }
                "dateTo" -> {
                    try {
                        val toDate = LocalDate.parse(value)
                        val activityEndDate = activity.endDate ?: activity.date
                        activityEndDate <= toDate
                    } catch (e: Exception) { true }
                }
                else -> true
            }
        }
    }

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
            val oldStartDate = activity.date
            val oldEndDate = activity.endDate ?: activity.date
            val dayDuration = oldEndDate.toEpochDays() - oldStartDate.toEpochDays()
            
            val updatedEndDate = newDate.plus(dayDuration.toInt(), DateTimeUnit.DAY)

            val updatedActivity = if (newTime != null) {
                activity.copy(date = newDate, endDate = updatedEndDate, time = newTime)
            } else {
                activity.copy(date = newDate, endDate = updatedEndDate)
            }
            FakeActivityData.updateActivity(updatedActivity)
            onDateSelected(newDate)
        }
    }

    fun addActivity(type: ActivityType) {
        // Implementation placeholder for future Milestones
    }
}
