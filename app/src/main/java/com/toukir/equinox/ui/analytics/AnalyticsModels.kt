package com.toukir.equinox.ui.analytics

data class TimeOfDayStat(
    val slotNameRes: Int,
    val count: Int,
    val percentage: Int
)

data class DayOfWeekStat(
    val dayLabel: String, // Mon, Tue, Wed, ...
    val count: Int,
    val percentage: Int
)

data class ProtocolEffectivenessItem(
    val title: String,
    val winsCount: Int,
    val totalCount: Int,
    val successRate: Int
)

data class TriggerStatItem(
    val triggerName: String,
    val count: Int,
    val percentage: Int
)
