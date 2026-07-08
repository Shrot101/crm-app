package com.akashicsoft.crm.activity.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akashicsoft.crm.activity.data.FakeActivityData
import com.akashicsoft.crm.activity.model.Activity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailActivityViewModel : ViewModel() {
    private val _activity = MutableStateFlow<Activity?>(null)
    val activity: StateFlow<Activity?> = _activity.asStateFlow()

    private val _isLoading = MutableStateFlow(value = false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadActivity(activityId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // Find activity in FakeActivityData
            val foundActivity = FakeActivityData.activities.value.find { it.id == activityId }
            _activity.value = foundActivity
            _isLoading.value = false
        }
    }

    fun deleteActivity(activityId: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            FakeActivityData.deleteActivity(activityId)
            onDeleted()
        }
    }
}
