package com.wakey.app.ui.alarmring

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.wakey.app.data.model.TaskType
import com.wakey.app.ui.alarmring.tasks.DelayTask
import com.wakey.app.ui.alarmring.tasks.StepCounterTask
import com.wakey.app.ui.alarmring.tasks.VerticalHoldTask
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Composable that starts and manages task execution based on task type.
 */
@Composable
fun TaskRunner(
    taskType: TaskType,
    viewModel: AlarmRingingViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(taskType) {
        when (taskType) {
            TaskType.STEPS -> {
                // Step counter task
                val task = StepCounterTask(context)
                if (task.isAvailable()) {
                    scope.launch {
                        task.observeSteps().collect { steps ->
                            viewModel.updateStepCount(steps)
                        }
                    }
                }
            }
            
            TaskType.HOLD_VERTICAL -> {
                // Vertical hold task - track time phone is held vertical
                val task = VerticalHoldTask(context)
                
                if (task.isAvailable()) {
                    var elapsedSeconds = 0
                    var lastUpdateTime = System.currentTimeMillis()
                    
                    scope.launch {
                        task.observeVerticalState().collect { isVertical ->
                            val currentTime = System.currentTimeMillis()
                            
                            if (isVertical) {
                                // Increment elapsed time if held vertical for ~1 second
                                if (currentTime - lastUpdateTime >= 1000) {
                                    elapsedSeconds++
                                    lastUpdateTime = currentTime
                                    viewModel.updateVerticalHold(true, elapsedSeconds)
                                }
                            } else {
                                // Reset if not vertical
                                elapsedSeconds = 0
                                lastUpdateTime = currentTime
                                viewModel.updateVerticalHold(false, 0)
                            }
                        }
                    }
                }
            }
            
            TaskType.TIME_DELAY -> {
                // Delay timer task
                val task = DelayTask()
                scope.launch {
                    task.observeTimer().collect { elapsedSeconds ->
                        viewModel.updateDelayTimer(elapsedSeconds)
                    }
                }
            }
        }
    }
}
