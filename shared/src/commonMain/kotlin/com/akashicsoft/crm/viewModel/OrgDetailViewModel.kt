package com.akashicsoft.crm.viewModel

import com.akashicsoft.crm.data.local.FakeOrgData
import com.akashicsoft.crm.model.Organization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrgDetailViewModel {
    private val _org = MutableStateFlow<Organization?>(null)
    val org = _org.asStateFlow()

    fun loadOrg(orgId: String) {
        val found = FakeOrgData.organizations.value.find { it._id == orgId }
        _org.value = found
    }
}