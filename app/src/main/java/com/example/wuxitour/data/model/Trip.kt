package com.example.wuxitour.data.model
// 内容与上次相同，确保统一
data class Trip(val id: String, val name: String, val description: String?, val totalDays: Int, val attractions: List<TripAttraction>, val status: TripStatus, val startDate: Long, val endDate: Long, val estimatedCost: Double)
data class TripAttraction(val attraction: Attraction, val visitDate: Long, val notes: String? = null)
enum class TripStatus(val displayName: String) { PLANNING("计划中"), IN_PROGRESS("进行中"), COMPLETED("已完成"), CANCELLED("已取消") }