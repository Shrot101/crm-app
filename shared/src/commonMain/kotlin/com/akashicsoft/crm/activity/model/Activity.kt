package com.akashicsoft.crm.activity.model

import kotlinx.datetime.LocalDate

data class Activity(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val duration: String? = null,
    val organization: String? = null,
    val type: ActivityType,
    val date: LocalDate
)

enum class ActivityType {
    CALL,
    MEETING,
    EMAIL,
    TASK
}
