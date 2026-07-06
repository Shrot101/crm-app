package com.akashicsoft.crm.activity.data

import com.akashicsoft.crm.activity.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import com.akashicsoft.crm.activity.util.CalendarUtils

object FakeActivityData {
    private val _activities = MutableStateFlow<List<Activity>>(getInitialMockActivities())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    fun getMockActivities(date: LocalDate, today: LocalDate): List<Activity> {
        return _activities.value.filter { it.date == date }
    }

    fun addActivity(activity: Activity) {
        _activities.update { it + activity }
    }

    fun updateActivity(updatedActivity: Activity) {
        _activities.update { list ->
            list.map { if (it.id == updatedActivity.id) updatedActivity else it }
        }
    }

    fun deleteActivity(activityId: String) {
        _activities.update { list ->
            list.filter { it.id != activityId }
        }
    }

    fun toggleTaskCompletion(activityId: String) {
        _activities.update { list ->
            list.map { 
                if (it.id == activityId) it.copy(isCompleted = !it.isCompleted) else it 
            }
        }
    }

    private fun getInitialMockActivities(): List<Activity> {
        val today = CalendarUtils.today()
        return listOf(
            Activity(
                id = "1",
                title = "Demo call",
                description = "Initial product demonstration",
                time = "10:00 AM",
                duration = "30m",
                organization = "Nova Retail Corp.",
                type = ActivityType.CALL,
                date = today,
                source = ActivitySource.PHONE,
                priority = ActivityPriority.NORMAL
            ),
            Activity(
                id = "3",
                title = "Lunch meeting",
                description = "Discuss potential partnership",
                time = "01:30 PM",
                duration = "1h",
                organization = "Global Tech Solutions",
                type = ActivityType.MEETING,
                date = today,
                source = ActivitySource.OTHER,
                priority = ActivityPriority.HIGH
            )
        )
    }
}
