package com.knightleo.bateponto.domain.model

import java.time.OffsetTime

data class Job(
    val name: String,
    val startTime: OffsetTime? = null,
    val lunchTime: OffsetTime? = null,
    val lunchEndTime: OffsetTime? = null,
    val endTime: OffsetTime? = null,
    val id: Int = 0,
)
