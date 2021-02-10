package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.*
import timber.log.Timber

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        viewModelScope.launch {
            try {
                val features27 = async {
                    retryWithTimeout(numberOfRetries, timeout) {
                        api.getAndroidVersionFeatures(27)
                    }
                }
                val features28 = async {
                    retryWithTimeout(numberOfRetries, timeout) {
                        api.getAndroidVersionFeatures(28)
                    }
                }
                val features = awaitAll(features27, features28)
                uiState.value = UiState.Success(features)
            } catch (e: Exception) {
                uiState.value = UiState.Error("Network error failed")
            }
        }
    }

    private suspend fun <T> retryWithTimeout(
        numberOfRetries: Int,
        timeout: Long,
        block: suspend () -> T
    ) = retry(numberOfRetries) {
        withTimeout(timeout) {
            block()
        }
    }

    private suspend fun <T> retry(
        numberOfRetries: Int,
        delayBetweenRetries: Long = 100,
        block: suspend () -> T
    ): T {
        repeat(numberOfRetries) {
            try {
                return block()
            } catch (exception: Exception) {
                Timber.e(exception)
            }
            delay(delayBetweenRetries)
        }
        return block() // last attempt
    }

}