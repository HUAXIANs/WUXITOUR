package com.example.wuxitour.data.model

import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@Keep
@IgnoreExtraProperties
data class Trip(
    @DocumentId
    @SerializedName("id")
    val id: String = "",
    
    @SerializedName("name")
    val name: String = "",
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("totalDays")
    val totalDays: Int = 0,
    
    @SerializedName("attractions")
    val attractions: List<TripAttraction> = emptyList(),
    
    @SerializedName("attractionIds")
    val attractionIds: List<String> = emptyList(),
    
    @SerializedName("status")
    val status: TripStatus = TripStatus.PLANNING,
    
    @SerializedName("startDate")
    val startDate: Long = 0L,
    
    @SerializedName("endDate")
    val endDate: Long = 0L,
    
    @SerializedName("estimatedCost")
    val estimatedCost: Double = 0.0,
    
    @SerializedName("userId")
    val userId: String = "",
    
    @SerializedName("createdAt")
    val createdAt: Long = System.currentTimeMillis(),
    
    @SerializedName("updatedAt")
    val updatedAt: Long = System.currentTimeMillis()
)

@Keep
@IgnoreExtraProperties
data class TripAttraction(
    @SerializedName("attraction")
    val attraction: Attraction,
    
    @SerializedName("visitDate")
    val visitDate: Long = 0L,
    
    @SerializedName("notes")
    val notes: String? = null
)

@Keep
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