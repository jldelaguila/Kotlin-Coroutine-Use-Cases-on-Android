package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.lukaslechner.coroutineusecasesonandroid.mock.mockVersionFeaturesAndroid10
import com.lukaslechner.coroutineusecasesonandroid.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class Perform2SequentialNetworkRequestsViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get: Rule
    val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

    private val receivedUiStates = mutableListOf<UiState>()

    @Test
    fun `should return Success when both network requests are successful`() {
        // Arrange
        val viewModel = Perform2SequentialNetworkRequestsViewModel(FakeSuccessApi())
        viewModel.observe()

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Success(mockVersionFeaturesAndroid10)
            ),
            receivedUiStates
        )
    }

    @Test
    fun `should return Error when first network requests fails`() {
        // Arrange
        val viewModel = Perform2SequentialNetworkRequestsViewModel(FakeVersionsErrorApi())
        viewModel.observe()

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Something went wrong!")
            ),
            receivedUiStates
        )
    }

    @Test
    fun `should return Error when second network requests fails`() {
        // Arrange
        val viewModel = Perform2SequentialNetworkRequestsViewModel(FakeFeaturesErrorApi())
        viewModel.observe()

        // Act
        viewModel.perform2SequentialNetworkRequest()

        // Assert
        assertEquals(
            listOf(
                UiState.Loading,
                UiState.Error("Something went wrong!")
            ),
            receivedUiStates
        )
    }

    private fun Perform2SequentialNetworkRequestsViewModel.observe() {
        uiState().observeForever { uiState ->
            if (uiState != null) {
                receivedUiStates.add(uiState)
            }
        }
    }
}