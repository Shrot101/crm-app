package com.akashicsoft.crm.data.local

import com.akashicsoft.crm.model.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object FakeOrgData {
    private val _organizations = MutableStateFlow(
        listOf(
            Organization(
                _id = "1",
                organizationName = "Akashic Soft",
                orgNo = "ORG-001",
                website = "www.akashicsoft.com",
                industry = "Technology",
                type = "Customer",
                leadSource = "Web",
                associatedCompany = "-",
                numberOfEmployees = 150,
                targetAmount = 50000.0,
                address = "123 Tech Park, Silicon Valley",
                city = "Bangalore",
                state = "Karnataka",
                assignedTo = "Alice Smith",
                createdAt = "2023-01-15T10:00:00Z"
            ),
            Organization(
                _id = "2",
                organizationName = "Global Corp",
                orgNo = "ORG-002",
                website = "www.globalcorp.net",
                industry = "Manufacturing",
                type = "Partner",
                leadSource = "Referral",
                associatedCompany = "Akashic Soft",
                numberOfEmployees = 1200,
                targetAmount = 250000.0,
                address = "45 Industrial Estate",
                city = "Mumbai",
                state = "Maharashtra",
                assignedTo = "Bob Johnson",
                createdAt = "2023-05-20T14:30:00Z"
            ),
            Organization(
                _id = "3",
                organizationName = "Innovate AI",
                orgNo = "ORG-003",
                website = "www.innovateai.io",
                industry = "Artificial Intelligence",
                type = "Prospect",
                leadSource = "Conference",
                associatedCompany = "-",
                numberOfEmployees = 45,
                targetAmount = 75000.0,
                address = "78 AI Hub, Tech City",
                city = "Hyderabad",
                state = "Telangana",
                assignedTo = "Charlie Davis",
                createdAt = "2023-11-05T09:15:00Z"
            )
        )
    )
    val organizations = _organizations.asStateFlow()

    fun addOrganization(org: Organization) {
        _organizations.value = _organizations.value + org
    }

    fun updateOrganization(updated: Organization) {
        _organizations.value = _organizations.value.map {
            if (it._id == updated._id) updated else it
        }
    }
}
