package com.example.myapplication.viewmodel.myplans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripRepository
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.model.Travel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _creationState = MutableStateFlow<Result<Travel>?>(null)
    val creationState: StateFlow<Result<Travel>?> = _creationState

    private val _previewTravel = MutableStateFlow<Travel?>(null)
    val previewTravel: StateFlow<Travel?> = _previewTravel

    fun setPreviewTravel(travel: Travel) {
        _previewTravel.value = travel
    }
    fun confirmTrip(
        travel: Travel,
        onSuccess: (Travel) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val token = CurrentUser.token
            if (token.isNullOrEmpty()) {
                onError("尚未登入，無法提交行程")
                return@launch
            }

            val result = tripRepository.createTrip(travel, token)
            result.onSuccess {
                onSuccess(it) // ✅ 把 Travel 回傳
            }.onFailure {
                onError("提交失敗：${it.message}")
            }
        }
    }
}