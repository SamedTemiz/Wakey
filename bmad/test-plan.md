# Test Plan: Wakey

## 1. Introduction
This document outlines the testing strategy for the "Wakey" Android application.

## 2. Scope
*   **Functional Testing:** Alarms, Tasks, Settings.
*   **System Integration:** AlarmManager, Boot Receiver, Doze Mode.
*   **UI/UX:** Responsiveness, Animations, Dark Mode.

## 3. Test Cases

### 3.1 Core Alarm Functionality
| ID | Title | Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| TC-01 | Create Alarm | Set time -> Save | Alarm appears in list, Helper shows "Alarm in X" | |
| TC-02 | Trigger Alarm | Wait for time | Full screen activity launches | |
| TC-03 | Snooze | Trigger -> Click Snooze | Alarm stops, reschedules for +5/10 mins | |
| TC-04 | Dismiss (Steps) | Trigger -> Walk steps | Count decreases, Alarm stops at 0 | |
| TC-05 | Delete Alarm | Edit -> Delete | Alarm removed from list & DB | |

### 3.2 System Resilience
| ID | Title | Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- |
| TC-06 | Reboot Device | Restart phone | Alarms persist and reschedule | |
| TC-07 | Doze Mode | Leave phone idle 1h+ | Alarm rings on time | |
| TC-08 | Permissions | Revoke Overlay perm | App prompts user to grant it | |

## 4. Automation Strategy
*   **Unit Tests:** ViewModel logic (e.g., `AlarmEditViewModelTest`).
*   **UI Tests:** Compose Rule tests for Screens.

## 5. Tools
*   JUnit 4/5
*   Espresso / Compose Test Rule
*   Mockk
