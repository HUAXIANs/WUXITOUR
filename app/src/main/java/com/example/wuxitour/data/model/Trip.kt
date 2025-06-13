package com.example.wuxitour.data.model

import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("totalDays")
    val totalDays: Int,
    
    @SerializedName("attractions")
    val attractions: List<TripAttraction>,
    
    @SerializedName("status")
    val status: TripStatus,
    
    @SerializedName("startDate")
    val startDate: Long,
    
    @SerializedName("endDate")
    val endDate: Long,
    
    @SerializedName("estimatedCost")
    val estimatedCost: Double
)

data class TripAttraction(
    @SerializedName("attraction")
    val attraction: Attraction,
    
    @SerializedName("visitDate")
    val visitDate: Long,
    
    @SerializedName("notes")
    val notes: String? = null
)

enum class TripStatus(
    @SerializedName("displayName")
    val displayName: String
) {
    @SerializedName("planning")
    PLANNING("计划中"),
    
    @SerializedName("in_progress")
    IN_PROGRESS("进行中"),
    
    @SerializedName("completed")
    COMPLETED("已完成"),
    
    @SerializedName("cancelled")
    CANCELLED("已取消")
}