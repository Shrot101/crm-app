package com.akashicsoft.crm.activity.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.akashicsoft.crm.activity.data.FakeActivityData
import com.akashicsoft.crm.activity.model.*
import com.akashicsoft.crm.activity.util.CalendarUtils
import com.akashicsoft.crm.data.local.*
import com.akashicsoft.crm.model.AssignedOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import kotlin.random.Random

class CreateActivityViewModel : ViewModel() {

    // 1. Activity Title
    var title = mutableStateOf("")

    // 2 & 3. Dates and Times
    var startDate = mutableStateOf(CalendarUtils.today())
    var startTime = mutableStateOf("10:00 AM")
    var endDate = mutableStateOf(CalendarUtils.today())
    var endTime = mutableStateOf("11:00 AM")
    var isAllDay = mutableStateOf(false)

    // 4. Activity Type
    var activityType = mutableStateOf(ActivityType.CALL)

    // 5. Source
    var source = mutableStateOf(ActivitySource.OTHER)

    // 6. Priority
    var priority = mutableStateOf(ActivityPriority.NORMAL)

    // 7. Contact (Single)
    var selectedContact = mutableStateOf<ActivityParticipant?>(null)

    // 8. Organization Name (Auto-filled)
    var organizationName = mutableStateOf("")

    // 9. Product/Service
    var product = mutableStateOf("")

    // 10. Deal
    var selectedDeal = mutableStateOf<ActivityRelatedRecord?>(null)

    // 11. Agenda
    var agenda = mutableStateOf("")

    // 12. Reminder
    var reminder = mutableStateOf("None")

    // 13. Location
    var location = mutableStateOf("")

    // 14. Notes
    var notes = mutableStateOf("")

    // 15. Assigned To
    var assignedTo = mutableStateOf<AssignedOwner?>(null)

    private val _isSaved = MutableStateFlow(false)
    val isSaved = _isSaved.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // Dropdown Options
    val activityTypeOptions = ActivityType.values().toList()
    val sourceOptions = ActivitySource.values().toList()
    val priorityOptions = ActivityPriority.values().toList()
    val reminderOptions = listOf("None", "At time of meeting", "5 minutes before", "10 minutes before", "15 minutes before", "30 minutes before", "1 hour before", "2 hours before", "1 day before")
    val availableOwners = FakeLeadsData.getMockUsers()

    init {
        assignedTo.value = availableOwners.firstOrNull()
    }

    fun resetForm(initialDate: LocalDate? = null) {
        val baseDate = initialDate ?: CalendarUtils.today()
        title.value = ""
        startDate.value = baseDate
        startTime.value = "10:00 AM"
        endDate.value = baseDate
        endTime.value = "11:00 AM"
        isAllDay.value = false
        activityType.value = ActivityType.CALL
        source.value = ActivitySource.OTHER
        priority.value = ActivityPriority.NORMAL
        selectedContact.value = null
        organizationName.value = ""
        product.value = ""
        selectedDeal.value = null
        agenda.value = ""
        reminder.value = "None"
        location.value = ""
        notes.value = ""
        assignedTo.value = availableOwners.firstOrNull()
        _isSaved.value = false
        _error.value = null
    }

    fun onContactSelected(contact: ActivityParticipant) {
        selectedContact.value = contact
        // Auto-fill Organization Name
        organizationName.value = contact.organization ?: ""
    }

    fun saveActivity() {
        if (title.value.isBlank()) {
            _error.value = "Activity Title is required"
            return
        }

        if (endDate.value < startDate.value) {
            _error.value = "End Date cannot be before Start Date"
            return
        }

        val newActivity = Activity(
            id = "act_${Random.nextInt()}",
            title = title.value,
            description = notes.value,
            time = if (isAllDay.value) "All Day" else startTime.value,
            duration = if (isAllDay.value) null else "1h",
            organization = organizationName.value,
            type = activityType.value,
            date = startDate.value,
            source = source.value,
            priority = priority.value,
            contact = selectedContact.value,
            product = product.value,
            deal = selectedDeal.value,
            agenda = agenda.value,
            reminder = reminder.value,
            location = location.value,
            assignedTo = assignedTo.value,
            endTime = endTime.value,
            endDate = endDate.value
        )

        FakeActivityData.addActivity(newActivity)
        _isSaved.value = true
    }

    // Search functions for bottom sheet
    fun searchContacts(query: String): List<ActivityParticipant> {
        return FakeContactsData.contacts.value
            .filter { it.name.contains(query, ignoreCase = true) }
            .map { ActivityParticipant(it.id, it.name, it.organization) }
    }

    fun searchDeals(query: String): List<ActivityRelatedRecord> {
        return FakeDealsData.deals.value
            .filter { it.title.contains(query, ignoreCase = true) }
            .map { ActivityRelatedRecord(it._id, it.title) }
    }
}
