package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch

class Perform2SequentialNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun perform2SequentialNetworkRequest() {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val versions = mockApi.getRecentAndroidVersions()
                val lastVersion = versions.last()
                val features = mockApi.getAndroidVersionFeatures(lastVersion.apiLevel)
                uiState.value = UiState.Success(features)
            } catch (e: Exception) {
                uiState.value = UiState.Error("Something went wrong!")
            }
        }
    }
}