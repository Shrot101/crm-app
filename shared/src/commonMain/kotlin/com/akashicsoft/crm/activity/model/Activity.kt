package com.akashicsoft.crm.activity.model

import com.akashicsoft.crm.model.AssignedOwner
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

data class Activity(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val duration: String? = null,
    val organization: String? = null,
    val type: ActivityType,
    val date: LocalDate,
    val isCompleted: Boolean = false,
    val source: ActivitySource? = null,
    val priority: ActivityPriority? = null,
    val contact: ActivityParticipant? = null,
    val product: String? = null,
    val deal: ActivityRelatedRecord? = null,
    val agenda: String? = null,
    val reminder: String? = null,
    val location: String? = null,
    val assignedTo: AssignedOwner? = null,
    val endTime: String? = null,
    val endDate: LocalDate? = null,
    // Keep meetingData for backward compatibility or legacy logic if needed, but primary fields are above
    val meetingData: MeetingData? = null
)

enum class ActivityType {
    CALL,
    MESSAGE,
    WHATSAPP,
    MEETING,
    EVENTS
}

enum class ActivitySource {
    PHONE, EMAIL, TWITTER, FACEBOOK, LINKEDIN, WHATSAPP, OTHER
}

enum class ActivityPriority {
    LOW, NORMAL, HIGH, URGENT
}

@Serializable
data class ActivityParticipant(
    val id: String,
    val name: String,
    val organization: String? = null
)

@Serializable
data class ActivityRelatedRecord(
    val id: String,
    val title: String
)

@Serializable
data class MeetingData(
    val location: String? = null,
    val meetingType: String? = null,
    val participants: List<Participant> = emptyList(),
    val relatedRecords: List<RelatedRecord> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
    val agenda: String? = null,
    val assignedTo: AssignedOwner? = null,
    val isAllDay: Boolean = false,
    val endDate: LocalDate? = null,
    val endTime: String? = null,
    val sendInvitations: Boolean = false
)

@Serializable
data class Participant(
    val id: String,
    val name: String,
    val type: ParticipantType,
    val email: String? = null,
    val organization: String? = null
)

enum class ParticipantType { CONTACT, LEAD, ORGANIZATION, USER }

@Serializable
data class RelatedRecord(
    val id: String,
    val name: String,
    val type: RecordType
)

enum class RecordType { DEAL, ORGANIZATION, CONTACT }

@Serializable
data class Reminder(
    val type: String,
    val minutesBefore: Int? = null
)
