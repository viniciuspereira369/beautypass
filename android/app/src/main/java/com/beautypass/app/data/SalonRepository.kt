package com.beautypass.app.data

import com.beautypass.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object SalonRepository {

    private val _favoriteIds = MutableStateFlow<Set<String>>(setOf())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    val salons: List<Salon> = listOf(
        Salon(
            id = "s1",
            name = "Ateliê Belle Époque",
            neighborhood = "Pinheiros",
            category = "hair",
            rating = 4.92,
            reviewsCount = 184,
            distanceKm = 0.8,
            lat = -23.5614,
            lng = -46.6853,
            address = "R. Fradique Coutinho, 980 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
            socialProof = "Mariana agendou Escova há 12 min",
            leadStaff = LeadStaff(
                name = "Juliana Paes Mendonça",
                role = "Fundadora & Master Stylist",
                avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200&q=80"
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Mariana, Camila e Beatriz frequentam este espaço"
            ),
            services = listOf(
                Service("srv_s1_1", "Escova Modeladora & Nutrição", 45, 120.0, "hair", "Finalização brilhante com sérum termoativo"),
                Service("srv_s1_2", "Corte & Design Visagista", 60, 160.0, "hair", "Corte personalizado de acordo com traços faciais"),
                Service("srv_s1_3", "Tratamento Reconstrutor Kérastase", 50, 190.0, "hair", "Reconstrução profunda da fibra capilar")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 25, "economy"),
                DiscountSlot("11:30", 30, "economy"),
                DiscountSlot("14:00", 0, "standard"),
                DiscountSlot("16:30", 15, "economy")
            ),
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1562322140-8baeececf3df?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?auto=format&fit=crop&w=800&q=80"
            )
        ),
        Salon(
            id = "s2",
            name = "L'Élégance Jardins",
            neighborhood = "Jardins",
            category = "nails",
            rating = 4.88,
            reviewsCount = 210,
            distanceKm = 1.4,
            lat = -23.5658,
            lng = -46.6672,
            address = "R. Oscar Freire, 1120 - Jardins, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
            socialProof = "Camila agendou Manicure há 8 min",
            leadStaff = LeadStaff(
                name = "Renata B. Silveira",
                role = "Nail Artist & Instrutora Internacional",
                avatarUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=200&h=200&q=80"
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Camila e Larissa agendam unhas aqui quinzenalmente"
            ),
            services = listOf(
                Service("srv_s2_1", "Manicure Russa & Esmaltação em Gel", 60, 110.0, "nails", "Cuticulagem a seco com acabamento impecável de até 3 semanas"),
                Service("srv_s2_2", "Alongamento em Fibra de Vidro", 120, 240.0, "nails", "Unhas naturais e resistentes com curvatura C perfeita")
            ),
            discountSlots = listOf(
                DiscountSlot("09:00", 20, "economy"),
                DiscountSlot("13:00", 35, "economy"),
                DiscountSlot("15:30", 0, "standard")
            ),
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1632345031435-8727f6897d53?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1522337094346-29759c258385?auto=format&fit=crop&w=800&q=80"
            )
        ),
        Salon(
            id = "s3",
            name = "Studio Barber & Lounge",
            neighborhood = "Vila Madalena",
            category = "barber",
            rating = 4.95,
            reviewsCount = 340,
            distanceKm = 1.9,
            lat = -23.5543,
            lng = -46.6892,
            address = "R. Harmonia, 450 - Vila Madalena, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
            socialProof = "Lucas agendou Barba Terapia há 4 min",
            leadStaff = LeadStaff(
                name = "Marcos Vinicius",
                role = "Master Barber & Visagista Masculino",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200&q=80"
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 4,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Gabriel, Pedro e mais 2 amigos frequentam este espaço"
            ),
            services = listOf(
                Service("srv_s3_1", "Corte Degradê na Tesoura & Máquina", 40, 95.0, "barber", "Alinhamento com lavagem mentolada e finalização matte"),
                Service("srv_s3_2", "Barboterapia com Toalha Quente", 35, 75.0, "barber", "Esfoliação facial, hidratação com óleos essenciais e navalha descartável")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 15, "economy"),
                DiscountSlot("14:30", 30, "economy")
            ),
            galleryImages = listOf(
                "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80"
            )
        )
    )

    fun toggleFavorite(salonId: String) {
        val current = _favoriteIds.value
        _favoriteIds.value = if (current.contains(salonId)) {
            current - salonId
        } else {
            current + salonId
        }
    }

    fun isFavorite(salonId: String): Boolean = _favoriteIds.value.contains(salonId)

    fun calculatePricing(salon: Salon, slotTime: String, service: Service): SlotPricing {
        val slot = salon.discountSlots.find { it.time == slotTime }
        val discount = slot?.discountPct ?: 0
        val basePrice = service.basePrice
        val finalPrice = if (discount > 0) basePrice * (1.0 - discount / 100.0) else basePrice

        val subtext = if (discount > 0) {
            "preço menor em horário de menor procura (Economia de R$ ${String.format("%.2f", basePrice - finalPrice)})"
        } else {
            "horário de alta procura (preço integral)"
        }

        val badgeLabel = if (discount >= 30) "Melhor Desconto" else if (discount > 0) "Horário Econômico" else "Padrão"

        return SlotPricing(
            time = slotTime,
            basePrice = basePrice,
            finalPrice = finalPrice,
            discountPct = discount,
            hasDiscount = discount > 0,
            subtext = subtext,
            badgeLabel = badgeLabel
        )
    }

    fun addAppointment(appointment: Appointment) {
        _appointments.value = listOf(appointment) + _appointments.value
    }

    fun cancelAppointment(appointmentId: String) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = AppointmentStatus.CANCELLED_BY_USER) else it
        }
    }
}
