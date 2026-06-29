package com.akashicsoft.crm.data.local

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.akashicsoft.crm.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FakeLeadsData {
    private val _leads = MutableStateFlow<List<LeadListItem>>(getInitialMockLeads())
    val leads: StateFlow<List<LeadListItem>> = _leads.asStateFlow()

    fun addLead(lead: LeadListItem) {
        _leads.value = listOf(lead) + _leads.value
    }

    fun updateLead(updatedLead: LeadListItem) {
        _leads.value = _leads.value.map {
            if (it._id == updatedLead._id) updatedLead else it
        }
    }

    fun getMockUsers(): List<AssignedOwner> {
        return listOf(
            AssignedOwner("owner1", "Alice Smith"),
            AssignedOwner("owner2", "Bob Johnson"),
            AssignedOwner("owner3", "Charlie Davis")
        )
    }

    fun getMockDepartments(): List<String> {
        return listOf("Sales", "Marketing", "Support", "Engineering", "Operations")
    }
    
    fun getCountryCodes(): List<Pair<String, String>> {
        return listOf(
            "🇮🇳" to "+91",
            "🇺🇸" to "+1",
            "🇬🇧" to "+44",
            "🇦🇪" to "+971",
            "🇸🇬" to "+65",
            "🇨🇦" to "+1",
            "🇦🇺" to "+61"
        )
    }

    private fun getInitialMockLeads(): List<LeadListItem> {
        return listOf(
            LeadListItem(
                _id = "1",
                salutation = "Mr.",
                firstName = "John",
                lastName = "Doe",
                organizationName = "Google",
                designation = "Senior Manager",
                status = "NEW",
                leadSource = "Website",
                leadNumber = "LD-001",
                createdAt = "2024-03-20T10:00:00Z",
                emails = listOf(EmailContact("john.doe@google.com", isPrimary = true)),
                phones = listOf(PhoneContact("+1 234 567 8901", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner1", "Alice Smith"),
                notes = "Interested in cloud solutions."
            ),
            LeadListItem(
                _id = "2",
                salutation = "Ms.",
                firstName = "Jane",
                lastName = "Smith",
                organizationName = "Microsoft",
                designation = "CTO",
                status = "QUALIFIED",
                leadSource = "Referral",
                leadNumber = "LD-002",
                createdAt = "2024-03-19T14:30:00Z",
                emails = listOf(EmailContact("jane.smith@microsoft.com", isPrimary = true)),
                phones = listOf(PhoneContact("+1 987 654 3210", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner2", "Bob Johnson"),
                notes = "Follow up next week."
            ),
            LeadListItem(
                _id = "3",
                salutation = "Mr.",
                firstName = "Mike",
                lastName = "Brown",
                organizationName = "Apple",
                designation = "Lead Engineer",
                status = "PROPOSAL_SENT",
                leadSource = "LinkedIn",
                leadNumber = "LD-003",
                createdAt = "2024-03-18T09:15:00Z",
                phones = listOf(PhoneContact("+1 555 123 4567", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner1", "Alice Smith")
            ),
            LeadListItem(
                _id = "4",
                salutation = "Mrs.",
                firstName = "Sarah",
                lastName = "Wilson",
                organizationName = "Amazon",
                designation = "HR Director",
                status = "CONTACTED",
                leadSource = "Cold Call",
                leadNumber = "LD-004",
                createdAt = "2024-03-17T11:20:00Z",
                emails = listOf(EmailContact("sarah.w@amazon.com", isPrimary = true)),
                phones = listOf(PhoneContact("+1 444 987 6543", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner2", "Bob Johnson")
            ),
            LeadListItem(
                _id = "5",
                salutation = "Dr.",
                firstName = "David",
                lastName = "Lee",
                organizationName = "Netflix",
                designation = "Product VP",
                status = "NEGOTIATION",
                leadSource = "Conference",
                leadNumber = "LD-005",
                createdAt = "2024-03-16T16:45:00Z",
                emails = listOf(EmailContact("d.lee@netflix.com", isPrimary = true)),
                phones = listOf(PhoneContact("+1 222 333 4444", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner1", "Alice Smith")
            ),
            LeadListItem(
                _id = "6",
                salutation = "Ms.",
                firstName = "Emma",
                lastName = "Garcia",
                organizationName = "Tesla",
                designation = "Supply Chain Manager",
                status = "WON",
                leadSource = "Website",
                leadNumber = "LD-006",
                createdAt = "2024-03-15T10:10:00Z",
                emails = listOf(EmailContact("emma.g@tesla.com", isPrimary = true)),
                phones = listOf(PhoneContact("+1 777 888 9999", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner3", "Charlie Davis")
            ),
            LeadListItem(
                _id = "7",
                salutation = "Mr.",
                firstName = "Robert",
                lastName = "Miller",
                organizationName = "Meta",
                designation = "Marketing Lead",
                status = "LOST",
                leadSource = "Referral",
                leadNumber = "LD-007",
                createdAt = "2024-03-14T09:00:00Z",
                phones = listOf(PhoneContact("+1 666 555 4444", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner2", "Bob Johnson")
            ),
            LeadListItem(
                _id = "8",
                salutation = "Ms.",
                firstName = "Olivia",
                lastName = "Taylor",
                organizationName = "Uber",
                designation = "Operations Analyst",
                status = "NEW",
                leadSource = "LinkedIn",
                leadNumber = "LD-008",
                createdAt = "2024-03-13T13:25:00Z",
                phones = listOf(PhoneContact("+1 111 222 3333", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner1", "Alice Smith")
            ),
            LeadListItem(
                _id = "9",
                salutation = "Mr.",
                firstName = "James",
                lastName = "Anderson",
                organizationName = "Spotify",
                designation = "Technical Recruiter",
                status = "QUALIFIED",
                leadSource = "Website",
                leadNumber = "LD-009",
                createdAt = "2024-03-12T15:50:00Z",
                phones = listOf(PhoneContact("+1 999 000 1111", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner3", "Charlie Davis")
            ),
            LeadListItem(
                _id = "10",
                salutation = "Ms.",
                firstName = "Sophia",
                lastName = "Martinez",
                organizationName = "Adobe",
                designation = "Account Executive",
                status = "CONTACTED",
                leadSource = "Partner",
                leadNumber = "LD-010",
                createdAt = "2024-03-11T10:40:00Z",
                phones = listOf(PhoneContact("+1 888 777 6666", isPrimary = true, type = "Mobile")),
                assignedOwner = AssignedOwner("owner2", "Bob Johnson")
            )
        )
    }
}

object LeadListUIHelpers {
    fun getStatusColor(status: String?): Color {
        return when (status?.uppercase()) {
            "NEW" -> Color(0xFF8AB4F8)
            "QUALIFIED" -> Color(0xFF27AE60)
            "CONTACTED" -> Color(0xFFF2994A)
            "PROPOSAL_SENT" -> Color(0xFFBB6BD9)
            "NEGOTIATION" -> Color(0xFF2D9CDB)
            "WON" -> Color(0xFF219653)
            "LOST" -> Color(0xFFEB5757)
            else -> Color(0xFF9E9E9E)
        }
    }

    fun getStatusIcon(status: String?): ImageVector {
        return when (status?.uppercase()) {
            "NEW" -> Icons.Default.Add
            "QUALIFIED" -> Icons.Default.CheckCircle
            "CONTACTED" -> Icons.Default.Phone
            "PROPOSAL_SENT" -> Icons.Default.Description
            "NEGOTIATION" -> Icons.Default.ArrowForward
            "WON" -> Icons.Default.Star
            "LOST" -> Icons.Default.ThumbDown
            else -> Icons.Default.Help
        }
    }
}
