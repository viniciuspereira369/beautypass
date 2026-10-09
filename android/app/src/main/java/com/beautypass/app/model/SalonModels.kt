package com.beautypass.app.model

// =====================================================================
// MODELO DE DADOS OFICIAL BEAUTYPASS (THE TRIPLE FUSION)
// Suporte integral ao catálogo dos 24 salões de São Paulo e ciclo de vida
// =====================================================================

data class Service(
    val id: String,
    val name: String,
    val durationMinutes: Int,
    val basePrice: Double,
    val category: String, // "hair", "nails", "barber", "massage", "esthetic", "depilation"
    val description: String
)

data class Staff(
    val id: String,
    val name: String,
    val role: String,
    val rating: Double,
    val avatarUrl: String,
    val highlightBadge: String? = null
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
    val type: String // "economy", "urgent", "standard"
)

data class Review(
    val author: String,
    val rating: Int,
    val comment: String,
    val date: String
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
    val socialAvatar: String? = null,
    val leadStaff: LeadStaff,
    val mutualNetwork: MutualNetwork,
    val services: List<Service>,
    val staff: List<Staff> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val discountSlots: List<DiscountSlot> = emptyList(),
    val galleryImages: List<String> = emptyList()
) {
    val walkTimeMinutes: Int
        get() = (distanceKm * 12).toInt().coerceAtLeast(1)

    val walkDistanceMeters: Int
        get() = (distanceKm * 1000).toInt()
}

data class SlotPricing(
    val time: String = "",
    val basePrice: Double,
    val finalPrice: Double,
    val discountPct: Int,
    val hasDiscount: Boolean,
    val subtext: String,
    val badgeLabel: String,
    val isUrgent: Boolean = false,
    val snapshotFrozenAt: Long? = null
)

data class PricingSnapshot(
    val salonId: String,
    val serviceId: String,
    val time: String,
    val priceBase: Double,
    val priceFinal: Double,
    val discountPct: Int,
    val badgeLabel: String,
    val priceExplanation: String,
    val frozenAtTimestamp: Long
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
    val staff: Staff? = null,
    val dateDisplay: String,
    val timeSlot: String,
    val finalPrice: Double,
    val status: AppointmentStatus = AppointmentStatus.CONFIRMED,
    val bookedAtIso: String,
    val voucherQrCode: String = id,
    val walkingTimeMin: Int? = null,
    val pricingSnapshot: PricingSnapshot? = null,
    val cancellationReason: String? = null,
    val cancellationFee: Double? = null
)

data class BookingValidationResult(
    val isValid: Boolean,
    val reason: String? = null,
    val conflictAppointmentId: String? = null
)

data class SUSQuestion(
    val id: Int,
    val questionText: String,
    val isPositive: Boolean
)

data class SUSEvaluation(
    val participantCode: String,
    val answers: Map<Int, Int>, // 1..10 -> 1..5
    val susScore: Double,
    val retentionYes: Boolean,
    val evaluatedAtIso: String
)
