package com.akashicsoft.crm.data.local

import com.akashicsoft.crm.model.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object FakeContactsData {
    private val _contacts = MutableStateFlow<List<Contact>>(getInitialMockContacts().sortedBy { it.name })
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    fun addContact(contact: Contact) {
        _contacts.value = (listOf(contact) + _contacts.value).sortedBy { it.name }
    }

    fun updateContact(updatedContact: Contact) {
        _contacts.value = _contacts.value.map {
            if (it.id == updatedContact.id) updatedContact else it
        }.sortedBy { it.name }
    }

    fun deleteContact(id: String) {
        _contacts.value = _contacts.value.filter { it.id != id }
    }

    fun getCountryCodes(): List<Pair<String, String>> = listOf(
        Pair("🇮🇳", "+91"),
        Pair("🇺🇸", "+1"),
        Pair("🇬🇧", "+44"),
        Pair("🇦🇺", "+61"),
        Pair("🇦🇪", "+971"),
        Pair("🇸🇬", "+65"),
        Pair("🇩🇪", "+49"),
        Pair("🇫🇷", "+33")
    )

    private fun getInitialMockContacts(): List<Contact> {
        return listOf(
            Contact(
                id = "c1",
                name = "Charlie Jones",
                designation = "Solutions Architect",
                organization = "FutureGrid Systems",
                email = "charlie.jones@example.com",
                mobileNumber = "+91 8100000000",
                landlineNumber = "+91 120 456 7890",
                department = "Engineering",
                contactNo = "CT-1001",
                source = "Website",
                isFavorite = true
            ),
            Contact(
                id = "c2",
                name = "Cody Fisher",
                designation = "Senior Designer",
                organization = "Creative Studio",
                email = "cody.f@creative.io",
                mobileNumber = "+91 9000000001",
                contactNo = "CT-1006",
                source = "Referral"
            ),
            Contact(
                id = "c3",
                name = "Catherine Pierce",
                designation = "Operation Manager",
                organization = "Logistics Pro",
                email = "catherine.p@logistics.com",
                mobileNumber = "+91 9000000002",
                contactNo = "CT-1007",
                source = "Website"
            ),
            Contact(
                id = "c4",
                name = "Chris Evans",
                designation = "Marketing Strategist",
                organization = "Brand Masters",
                email = "chris.e@brands.com",
                mobileNumber = "+91 9000000003",
                contactNo = "CT-1008",
                source = "LinkedIn"
            ),
            Contact(
                id = "c5",
                name = "Clara Oswald",
                designation = "Data Scientist",
                organization = "Insight Corp",
                email = "clara.o@insight.com",
                mobileNumber = "+91 9000000004",
                contactNo = "CT-1009",
                source = "Conference"
            ),
            Contact(
                id = "c6",
                name = "Cameron Diaz",
                designation = "Account Executive",
                organization = "Global Sales",
                email = "cameron.d@globalsales.com",
                mobileNumber = "+91 9000000005",
                contactNo = "CT-1010",
                source = "Direct"
            ),
            Contact(
                id = "c7",
                name = "Caleb Smith",
                designation = "DevOps Engineer",
                organization = "Tech infra",
                email = "caleb.s@techinfra.io",
                mobileNumber = "+91 9000000006",
                contactNo = "CT-1011",
                source = "Referral"
            ),
            Contact(
                id = "c8",
                name = "Cassandra Clare",
                designation = "Content Writer",
                organization = "Media Hub",
                email = "cassandra.c@media.com",
                mobileNumber = "+91 9000000007",
                contactNo = "CT-1012",
                source = "Website"
            ),
            Contact(
                id = "c9",
                name = "Cooper Sheldon",
                designation = "Research Lead",
                organization = "Science Lab",
                email = "sheldon.c@science.edu",
                mobileNumber = "+91 9000000008",
                contactNo = "CT-1013",
                source = "Referral"
            ),
            Contact(
                id = "c10",
                name = "Chloe Price",
                designation = "UI/UX Designer",
                organization = "Pixel Perfect",
                email = "chloe.p@pixel.io",
                mobileNumber = "+91 9000000009",
                contactNo = "CT-1014",
                source = "LinkedIn"
            ),
            Contact(
                id = "s1",
                name = "Samuel Jackson",
                designation = "Project Director",
                organization = "Build-It Co",
                email = "samuel.j@buildit.com",
                mobileNumber = "+91 8000000001",
                contactNo = "CT-1015",
                source = "Website"
            ),
            Contact(
                id = "s2",
                name = "Sophia Loren",
                designation = "HR Head",
                organization = "People First",
                email = "sophia.l@people.io",
                mobileNumber = "+91 8000000002",
                contactNo = "CT-1016",
                source = "LinkedIn"
            ),
            Contact(
                id = "s3",
                name = "Steven Rogers",
                designation = "Security Consultant",
                organization = "Shield Ops",
                email = "steven.r@shield.gov",
                mobileNumber = "+91 8000000003",
                contactNo = "CT-1017",
                source = "Direct"
            ),
            Contact(
                id = "s4",
                name = "Sara Lance",
                designation = "Legal Advisor",
                organization = "Law & Order",
                email = "sara.l@law.com",
                mobileNumber = "+91 8000000004",
                contactNo = "CT-1018",
                source = "Referral"
            ),
            Contact(
                id = "s5",
                name = "Simon Pegg",
                designation = "Technical Support",
                organization = "Quick Fix",
                email = "simon.p@quickfix.io",
                mobileNumber = "+91 8000000005",
                contactNo = "CT-1019",
                source = "Website"
            ),
            Contact(
                id = "s6",
                name = "Selina Kyle",
                designation = "Security Specialist",
                organization = "Night Watch",
                email = "selina.k@nightwatch.com",
                mobileNumber = "+91 8000000006",
                contactNo = "CT-1020",
                source = "LinkedIn"
            ),
            Contact(
                id = "s7",
                name = "Sherlock Holmes",
                designation = "Lead Investigator",
                organization = "Consulting Group",
                email = "sherlock@bakerstreet.com",
                mobileNumber = "+91 8000000007",
                contactNo = "CT-1021",
                source = "Referral"
            ),
            Contact(
                id = "s8",
                name = "Sansa Stark",
                designation = "Admin Lead",
                organization = "North Corp",
                email = "sansa.s@north.com",
                mobileNumber = "+91 8000000008",
                contactNo = "CT-1022",
                source = "Website"
            ),
            Contact(
                id = "s9",
                name = "Sebastian Stan",
                designation = "Public Relations",
                organization = "Star Media",
                email = "sebastian.s@starmedia.com",
                mobileNumber = "+91 8000000009",
                contactNo = "CT-1023",
                source = "LinkedIn"
            ),
            Contact(
                id = "s10",
                name = "Stella Gibson",
                designation = "Detective Chief Inspector",
                organization = "Public Safety",
                email = "stella.g@safety.gov",
                mobileNumber = "+91 8000000010",
                contactNo = "CT-1024",
                source = "Direct"
            ),
            Contact(
                id = "n1",
                name = "Nina Williams",
                designation = "Product Manager",
                organization = "Northstar Technologies",
                email = "nina.w@northstar.io",
                mobileNumber = "+91 9876543210",
                landlineNumber = "+91 11 2345 6789",
                department = "Product",
                contactNo = "CT-1002",
                source = "Referral"
            ),
            Contact(
                id = "o1",
                name = "Oscar Martinez",
                designation = "Senior Developer",
                organization = "MetreWorks Systems",
                email = "oscar.m@metreworks.com",
                mobileNumber = "+91 7778889990",
                department = "Engineering",
                contactNo = "CT-1003",
                source = "LinkedIn"
            )
        )
    }
}
