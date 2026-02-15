package com.wakey.app.ui.alarmedit

import androidx.lifecycle.SavedStateHandle
import com.wakey.app.data.model.Alarm
import com.wakey.app.data.repository.AlarmRepository
import com.wakey.app.service.AlarmScheduler
import com.wakey.app.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmEditViewModelTest {

    @Mock
    private lateinit var alarmRepository: AlarmRepository
    @Mock
    private lateinit var alarmScheduler: AlarmScheduler
    @Mock
    private lateinit var settingsRepository: SettingsRepository
    
    private lateinit var viewModel: AlarmEditViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Mock default behavior
        val savedStateHandle = SavedStateHandle(mapOf("alarmId" to 1))
        viewModel = AlarmEditViewModel(alarmRepository, alarmScheduler, settingsRepository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun deleteAlarm_shouldCancelAndDeletes() = runTest {
        // Arrange
        val alarmId = 1
        
        // Act
        viewModel.deleteAlarm()
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutine

        // Assert
        verify(alarmScheduler).cancelAlarm(alarmId)
        verify(alarmRepository).deleteAlarmById(alarmId)
        
        val state = viewModel.uiState.value
        assertTrue(state.isSaved)
        assertEquals("Alarm deleted", state.toastMessage)
    }
}
