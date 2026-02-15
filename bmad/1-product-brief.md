# Product Brief: Wakey

## 1. Vision
**Wakey** is an alarm clock application designed to ensure users *actually* wake up. Unlike traditional alarms that can be dismissed with a single tap, Wakey requires the user to complete a specific task (physical or mental) before the alarm stops ringing.

## 2. Core Problem & Solution
*   **Problem:** Users subconsciously dismiss alarms and go back to sleep.
*   **Solution:** Introduce a "Task Barrier" between the alarm ringing and the dismissal. The alarm cannot be stopped until the task is successfully completed.

## 3. User Flows
1.  **Setup:** User sets alarm time, selects days, and chooses a "Wake-up Task".
2.  **Trigger:** Alarm rings in full-screen mode. Back button and home button are blocked (Task Affinity).
3.  **Action:** User must complete the task (e.g., take 30 steps).
4.  **Dismissal:** Upon task completion, the alarm stops and the app closes.

## 4. Tasks (Features)
*   **🚶 Step Counter:** Walk X steps to dismiss.
*   **📱 Vertical Hold:** Keep phone upright for X seconds.
*   **⏳ Time Delay:** Wait for X seconds before button becomes active.
*   **🎤 Voice Command:** Say a specific phrase.
*   **💡 Light Sensor:** Increase ambient light to dismiss.

## 5. Technical Constraints & Requirements
*   **Platform:** Android (Kotlin).
*   **Architecture:** Clean Architecture + MVVM + Jetpack Compose.
*   **Critical Permissions:** `SYSTEM_ALERT_WINDOW` (Overlay), `USE_FULL_SCREEN_INTENT`, `SCHEDULE_EXACT_ALARM`.
*   **Persistence:** Room Database.
*   **Reliability:** Must ring even in Doze mode (AlarmManager).

## 6. Target Audience
*   Heavy sleepers.
*   Students and professionals who struggle with morning routine.
*   People who want to build wake-up discipline.
