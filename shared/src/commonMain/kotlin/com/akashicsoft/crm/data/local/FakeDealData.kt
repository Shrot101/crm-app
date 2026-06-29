package com.akashicsoft.crm.data.local

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FakeDealsData {
    private val _deals = MutableStateFlow<List<DealListItem>>(getInitialMockDeals())
    val deals: StateFlow<List<DealListItem>> = _deals.asStateFlow()

    fun addDeal(deal: DealListItem) {
        _deals.value = listOf(deal) + _deals.value
    }

    fun updateDeal(updatedDeal: DealListItem) {
        _deals.value = _deals.value.map {
            if (it._id == updatedDeal._id) updatedDeal else it
        }
    }

    fun getMockProducts(): List<String> {
        return listOf("AWS Enterprise", "Custom NLP", "SafeGuard Pro", "Kotlin Multiplatform", "Snowflake Setup", "Others")
    }

    fun getCurrencies(): List<Pair<String, String>> {
        return listOf(
            "$" to "USD",
            "₹" to "INR",
            "€" to "EUR",
            "£" to "GBP",
            "¥" to "JPY"
        )
    }

    private fun getInitialMockDeals(): List<DealListItem> {
        return listOf(
            DealListItem(
                _id = "d1",
                title = "Cloud Infrastructure Migration",
                dealNumber = "DL-1001",
                owner = AssignedOwner("owner1", "Alice Smith"),
                organization = "SpaceX",
                rating = "Hot",
                assignedTo = AssignedOwner("owner2", "Bob Johnson"),
                stage = "NEGOTIATION",
                product = "AWS Enterprise",
                tags = listOf("Migration", "High Priority"),
                dealValue = 150000.0,
                closeDate = "2024-12-15",
                category = "Customer",
                contactPerson = "Elon Musk",
                currency = "$"
            ),
            DealListItem(
                _id = "d2",
                title = "AI Chatbot Integration",
                dealNumber = "DL-1002",
                owner = AssignedOwner("owner2", "Bob Johnson"),
                organization = "Tesla",
                rating = "Warm",
                assignedTo = AssignedOwner("owner3", "Charlie Davis"),
                stage = "OPPORTUNITY",
                product = "Custom NLP",
                tags = listOf("AI", "Innovation"),
                dealValue = 85000.0,
                closeDate = "2024-06-30",
                category = "Customer",
                contactPerson = "Tesla Sales Team",
                currency = "$"
            ),
            DealListItem(
                _id = "d3",
                title = "Cybersecurity Audit & Setup",
                dealNumber = "DL-1003",
                owner = AssignedOwner("owner1", "Alice Smith"),
                organization = "OpenAI",
                rating = "Hot",
                assignedTo = AssignedOwner("owner1", "Alice Smith"),
                stage = "PROPOSAL_MADE",
                product = "SafeGuard Pro",
                tags = listOf("Security", "Security Audit"),
                dealValue = 220000.0,
                closeDate = "2024-05-10",
                category = "Channel Partner",
                contactPerson = "Sam Altman",
                currency = "$"
            )
        )
    }
}

object DealUIHelpers {
    fun getStageColor(stage: String?): Color {
        return when (stage?.uppercase()) {
            "APPOINTMENT" -> Color(0xFF8AB4F8)
            "OPPORTUNITY" -> Color(0xFFF2C94C)
            "PROPOSAL_MADE" -> Color(0xFFBB6BD9)
            "REVIEW" -> Color(0xFF2D9CDB)
            "WON" -> Color(0xFF27AE60)
            "LOST" -> Color(0xFFEB5757)
            else -> Color(0xFF9E9E9E)
        }
    }

    fun getStageIcon(stage: String?): ImageVector {
        return when (stage?.uppercase()) {
            "APPOINTMENT" -> Icons.Default.Event
            "OPPORTUNITY" -> Icons.Default.Lightbulb
            "PROPOSAL_MADE" -> Icons.Default.Description
            "REVIEW" -> Icons.Default.RateReview
            "WON" -> Icons.Default.CheckCircle
            "LOST" -> Icons.Default.Cancel
            else -> Icons.Default.Info
        }
    }
    
    fun getRatingColor(rating: String?): Color {
        return when (rating?.lowercase()) {
            "hot" -> Color(0xFFEB5757)
            "warm" -> Color(0xFFF2994A)
            "cold" -> Color(0xFF2D9CDB)
            else -> Color.Gray
        }
    }

    fun getStages(): List<String> = listOf("Appointment", "Opportunity", "Proposal Made", "Review", "Won", "Lost")
    fun getCategories(): List<String> = listOf("Vendor", "Channel Partner", "Distributor", "Dealer", "Customer")
    fun getRatings(): List<String> = listOf("Hot", "Warm", "Cold")
}
