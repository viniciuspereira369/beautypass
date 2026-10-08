package com.beautypass.app.model

data class Service(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val basePrice: Double,
    val category: String, // "hair", "nails", "barber", "massage", "esthetic"
    val description: String
)

data class Staff(
    val id: String,
    val name: String,
    val role: String,
    val rating: Double,
    val avatarUrl: String
)

data class LeadStaff(
    val name: String,
    val role: String,
    val avatarUrl: String,
    val verified: Boolean = true
)

data class MutualNetwork(
    val friendsCount: Int,
    val avatars: List<String>,
    val text: String
)

data class DiscountSlot(
    val time: String,
    val discountPct: Int,
    val type: String // "economy", "standard", "high_demand"
)

data class Salon(
    val id: String,
    val name: String,
    val neighborhood: String,
    val category: String,
    val rating: Double,
    val reviewsCount: Int,
    val distanceKm: Double,
    val lat: Double,
    val lng: Double,
    val address: String,
    val imageUrl: String,
    val socialProof: String,
    val leadStaff: LeadStaff,
    val mutualNetwork: MutualNetwork,
    val services: List<Service>,
    val discountSlots: List<DiscountSlot>,
    val galleryImages: List<String> = emptyList()
) {
    val walkTimeMinutes: Int
        get() = (distanceKm * 12).toInt().coerceAtLeast(1)
}

data class SlotPricing(
    val time: String,
    val basePrice: Double,
    val finalPrice: Double,
    val discountPct: Int,
    val hasDiscount: Boolean,
    val subtext: String,
    val badgeLabel: String
)

enum class AppointmentStatus {
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED_BY_USER,
    CANCELLED_BY_MERCHANT,
    EXPIRED
}

data class Appointment(
    val id: String,
    val salon: Salon,
    val service: Service,
    val staff: Staff?,
    val dateDisplay: String,
    val timeSlot: String,
    val finalPrice: Double,
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    val bookedAtIso: String,
    val voucherQrCode: String = id,
    val walkingTimeMin: Int? = null
)
