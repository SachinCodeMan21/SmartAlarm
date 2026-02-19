package com.example.smartalarm.feature.timer.framework.di.annotations

import com.example.smartalarm.feature.timer.framework.notification.enums.TimerNotificationDataMapperKey
import dagger.MapKey

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class TimerNotificationMapDataMapperKey(val value: TimerNotificationDataMapperKey)
