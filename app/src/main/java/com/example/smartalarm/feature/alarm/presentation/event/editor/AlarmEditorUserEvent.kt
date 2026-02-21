package com.example.smartalarm.feature.alarm.presentation.event.editor

import com.example.smartalarm.feature.alarm.domain.model.Mission


sealed interface AlarmEditorUserEvent {

    /* ---------------------------- */
    /* 1️⃣ Alarm Settings Events     */
    /* ---------------------------- */
    sealed interface AlarmEvent : AlarmEditorUserEvent {
        data class LabelChanged(val label: String) : AlarmEvent
        data class TimeChanged(val hour: Int, val minute: Int, val amPm: Int) : AlarmEvent
        data class IsDailyChanged(val isDaily: Boolean) : AlarmEvent
        data class DayToggled(val dayIndex: Int) : AlarmEvent
    }

    /* ---------------------------- */
    /* 2️⃣ Mission Events            */
    /* ---------------------------- */
    sealed interface MissionEvent : AlarmEditorUserEvent {

        // Interaction
        data class PlaceholderClicked(val position: Int) : MissionEvent
        data class ItemClicked(val position: Int, val mission: Mission) : MissionEvent
        data class RemoveClicked(val position: Int) : MissionEvent

        // Modification
        data class Selected(val position: Int, val mission: Mission) : MissionEvent
        data class Updated(val position: Int, val mission: Mission) : MissionEvent

        // Preview
        data class Preview(val mission: Mission) : MissionEvent
    }


    /* ---------------------------- */
    /* 3️⃣ Sound Settings Events     */
    /* ---------------------------- */
    sealed interface SoundEvent : AlarmEditorUserEvent {
        data class VolumeChanged(val volume: Int) : SoundEvent
        data class VibrationToggled(val enabled: Boolean) : SoundEvent
        data object LaunchPicker : SoundEvent
        data class RingtoneSelected(val uri: String) : SoundEvent
    }

    /* ---------------------------- */
    /* 4️⃣ Snooze & Save Events      */
    /* ---------------------------- */
    sealed interface ActionEvent : AlarmEditorUserEvent {
        data object EditSnooze : ActionEvent
        data object SaveOrUpdate : ActionEvent
    }

    /* ---------------------------- */
    /* 5️⃣ Navigation Events         */
    /* ---------------------------- */
    sealed interface NavigationEvent : AlarmEditorUserEvent {
        data object SystemBack : NavigationEvent
        data object ToolbarBack : NavigationEvent
    }
}