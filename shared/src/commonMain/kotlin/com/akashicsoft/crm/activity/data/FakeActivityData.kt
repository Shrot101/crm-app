package com.akashicsoft.crm.activity.data

import com.akashicsoft.crm.activity.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

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
        return listOf(
            Activity(
                id = "1",
                title = "Demo call",
                description = "Initial product demonstration",
                time = "10:00 AM",
                duration = "30m",
                organization = "Nova Retail Corp.",
                type = ActivityType.CALL,
                date = LocalDate(2026, 7, 7),
                source = ActivitySource.PHONE,
                priority = ActivityPriority.NORMAL
            ),
            Activity(
                id = "2",
                title = "Contract review",
                description = "Go through the new terms",
                time = "02:00 PM",
                duration = "45m",
                organization = "Nova Retail Corp.",
                type = ActivityType.MEETING,
                date = LocalDate(2026, 7, 7),
                source = ActivitySource.OTHER,
                priority = ActivityPriority.URGENT
            ),
            Activity(
                id = "3",
                title = "Lunch meeting",
                description = "Discuss potential partnership",
                time = "01:30 PM",
                duration = "1h",
                organization = "Global Tech Solutions",
                type = ActivityType.MEETING,
                date = LocalDate(2026, 7, 8),
                source = ActivitySource.OTHER,
                priority = ActivityPriority.HIGH
            ),
            Activity(
                id = "4",
                title = "Follow-up email",
                description = "Send project proposal",
                time = "09:00 AM",
                duration = "15m",
                organization = "Tech Innovators",
                type = ActivityType.MESSAGE,
                date = LocalDate(2026, 7, 9),
                source = ActivitySource.EMAIL,
                priority = ActivityPriority.LOW
            ),
            Activity(
                id = "5",
                title = "Strategy session",
                description = "Q3 Planning",
                time = "11:00 AM",
                duration = "2h",
                organization = "Enterprise Corp",
                type = ActivityType.EVENTS,
                date = LocalDate(2026, 7, 10),
                source = ActivitySource.OTHER,
                priority = ActivityPriority.NORMAL
            )
        )
    }
}
