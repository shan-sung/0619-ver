package com.example.myapplication.viewmodel.myplans

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.TripRepository
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.Travel
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
            Log.d("PreviewVM", "取得的 token: $token")
            if (token.isNullOrEmpty()) {
                onError("尚未登入，無法提交行程")
                return@launch
            }

            val result = tripRepository.createTrip(travel, token)
            result.onSuccess {
                Log.d("PreviewVM", "建立成功：${it.title}")
                onSuccess(it)
            }.onFailure {
                Log.e("PreviewVM", "建立失敗：${it.message}")
                onError("提交失敗：${it.message}")
            }
        }
    }
}