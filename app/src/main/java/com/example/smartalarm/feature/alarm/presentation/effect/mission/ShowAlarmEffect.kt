package com.example.smartalarm.feature.alarm.presentation.effect.mission

import com.example.smartalarm.core.utility.exception.DataError
import com.example.smartalarm.feature.alarm.domain.model.AlarmModel

sealed class ShowAlarmEffect {
    data class StartMissionFlow(val alarmModel : AlarmModel) : ShowAlarmEffect()
    data class ShowToastMessage(val toastMessage : String) : ShowAlarmEffect()
    data class ShowError(val error : DataError) : ShowAlarmEffect()
    object FinishActivity : ShowAlarmEffect()

}