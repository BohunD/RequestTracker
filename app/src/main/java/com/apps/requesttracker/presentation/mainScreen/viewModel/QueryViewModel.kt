package com.apps.requesttracker.presentation.mainScreen.viewModel

import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.requesttracker.domain.repository.QueryRepository
import com.apps.requesttracker.data.db.QueryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QueryViewModel @Inject constructor(private val repository: QueryRepository) : ViewModel() {
    val allQueries: LiveData<List<QueryEntity>> = repository.allQueries


    fun delete(query: QueryEntity) = viewModelScope.launch {
        repository.delete(query)
    }

    private val _isAccessibilityServiceEnabled = MutableStateFlow<Boolean?>(null)
    val isAccessibilityServiceEnabled: StateFlow<Boolean?> = _isAccessibilityServiceEnabled

    fun checkAccessibilityService(context: Context, service: Class<*>) = viewModelScope.launch(Dispatchers.IO) {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        if (enabledServices.isNullOrEmpty()) {
            _isAccessibilityServiceEnabled.value = false
            return@launch
        }
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        var isEnabled = false
        while (colonSplitter.hasNext()) {
            val componentName = colonSplitter.next()
            if (componentName.equals("${context.packageName}/${service.name}", ignoreCase = true)) {
                isEnabled = true
                break
            }
        }
        _isAccessibilityServiceEnabled.value = isEnabled || am.isEnabled
    }
}


