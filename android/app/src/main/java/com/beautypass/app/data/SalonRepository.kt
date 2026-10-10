package com.beautypass.app.data

import android.content.Context
import com.beautypass.app.data.local.BeautyPassDatabase
import com.beautypass.app.data.local.entity.AppointmentEntity
import com.beautypass.app.data.local.entity.FavoriteEntity
import com.beautypass.app.data.local.entity.SusEvaluationEntity
import com.beautypass.app.data.local.entity.UserProfileEntity
import com.beautypass.app.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// =====================================================================
// REPOSITÓRIO DETERMINÍSTICO OFICIAL BEAUTYPASS (SÃO PAULO)
// Catálogo completo dos 24 salões extraídos de app.js e Explorer Survey 2
// Bairros: Pinheiros, Jardins, Itaim Bibi, Vila Madalena, Consolação, Perdizes
// =====================================================================

object SalonRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var database: BeautyPassDatabase? = null
    private var isInitialized = false

    private val _favoriteIds = MutableStateFlow<Set<String>>(setOf())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfileEntity?>(null)
    val userProfile: StateFlow<UserProfileEntity?> = _userProfile.asStateFlow()

    private val _latestSusEvaluation = MutableStateFlow<SUSEvaluation?>(null)
    val latestSusEvaluation: StateFlow<SUSEvaluation?> = _latestSusEvaluation.asStateFlow()

    /**
     * Inicializa a camada de persistência com o banco Room SQLite.
     * Conecta os DAOs reativos diretamente aos StateFlows do repositório.
     */
    fun initialize(context: Context, databaseOverride: BeautyPassDatabase? = null) {
        if (isInitialized && databaseOverride == null) return
        val db = databaseOverride ?: BeautyPassDatabase.getDatabase(context)
        database = db
        isInitialized = true

        // Observador reativo de favoritos persistidos
        repositoryScope.launch {
            db.favoriteDao().getAllFavoriteIds().collectLatest { ids ->
                _favoriteIds.value = ids.toSet()
            }
        }

        // Observador reativo de agendamentos no SQLite (com hidratação pelo catálogo determinístico)
        repositoryScope.launch {
            db.appointmentDao().getAllAppointments().collectLatest { entities ->
                val domainList = entities.mapNotNull { entity ->
                    val salon = getSalonById(entity.salonId) ?: return@mapNotNull null
                    val service = salon.services.find { it.id == entity.serviceId }
                        ?: Service(
                            id = entity.serviceId,
                            name = "Serviço Especializado",
                            durationMinutes = 45,
                            basePrice = entity.finalPrice,
                            category = salon.category,
                            description = "Serviço agendado no BeautyPass"
                        )
                    val staff = salon.staff.find { it.id == entity.staffId }
                    entity.toDomain(salon = salon, service = service, staff = staff)
                }
                _appointments.value = domainList
            }
        }

        // Observador reativo de perfil do usuário
        repositoryScope.launch {
            db.userProfileDao().getUserProfile().collectLatest { profile ->
                _userProfile.value = profile
            }
        }

        // Observador reativo de avaliação SUS
        repositoryScope.launch {
            db.susEvaluationDao().getLatestEvaluation().collectLatest { entity ->
                _latestSusEvaluation.value = entity?.toDomain()
            }
        }
    }

    fun getDatabaseInstance(): BeautyPassDatabase? = database

    // Bancos de imagens de alta definição por categoria (Unsplash)
    private val HAIR_GALLERY = listOf(
        "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1595476108010-b4d1f102b1b1?auto=format&fit=crop&w=800&q=80"
    )

    private val NAILS_GALLERY = listOf(
        "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1522337094344-664f2c4256ea?auto=format&fit=crop&w=800&q=80"
    )

    private val BARBER_GALLERY = listOf(
        "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80"
    )

    private val MASSAGE_GALLERY = listOf(
        "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1600334129128-685c5582fd35?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=800&q=80"
    )

    private val FACIAL_GALLERY = listOf(
        "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1512290900672-1f41d3d62325?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=800&q=80",
        "https://images.unsplash.com/photo-1516975080664-ed2fc6a32937?auto=format&fit=crop&w=800&q=80"
    )

    private fun getGallery(category: String, mainImage: String): List<String> {
        val pool = when (category) {
            "hair" -> HAIR_GALLERY
            "nails" -> NAILS_GALLERY
            "barber" -> BARBER_GALLERY
            "massage" -> MASSAGE_GALLERY
            "facial", "esthetic" -> FACIAL_GALLERY
            else -> HAIR_GALLERY
        }
        return listOf(mainImage) + pool.drop(1).take(3)
    }

    val salons: List<Salon> = listOf(
        // -------------------------------------------------------------
        // Salão 1: Ateliê Belle Époque (Pinheiros - Hair)
        // -------------------------------------------------------------
        Salon(
            id = "s1",
            name = "Ateliê Belle Époque",
            neighborhood = "Pinheiros",
            category = "hair",
            rating = 4.90,
            reviewsCount = 238,
            distanceKm = 0.8,
            lat = -23.5628,
            lng = -46.6854,
            address = "R. Fradique Coutinho, 980 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Mariana agendou Escova há 12 min",
            socialAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Juliana Paes Mendonça",
                role = "Master Stylist & Fundadora",
                avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Mariana, Camila e Beatriz frequentam este espaço"
            ),
            services = listOf(
                Service("srv_s1_1", "Escova Modeladora & Nutrição", 45, 120.0, "hair", "Lavagem com produtos botânicos, hidratação profunda e escova modeladora de longa duração."),
                Service("srv_s1_2", "Corte Visagista Feminino", 50, 140.0, "hair", "Lavagem especial, corte alinhado ao formato do rosto e secagem com proteção térmica."),
                Service("srv_s1_3", "Hidratação Reconstrutora Moroccanoil", 40, 95.0, "hair", "Tratamento intensivo com óleo de argan para reposição lipídica e brilho imediato."),
                Service("srv_s1_4", "Manicure & Spa de Cutículas", 40, 55.0, "nails", "Cuidado completo das unhas com esmaltação tradicional e esfoliação de mãos."),
                Service("srv_s1_5", "Mechas & Luzes Pontuais Glow", 90, 260.0, "hair", "Iluminação estratégica dos fios com proteção plex e tonalização luminosa.")
            ),
            staff = listOf(
                Staff("st1", "Juliana Paes Mendonça", "Master Stylist", 4.98, "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80", "Destaque da Casa"),
                Staff("st2", "Rodrigo Faro", "Visagista & Colorista", 4.88, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Fernanda S.", 5, "Excelente atendimento, resultado incrível! Juliana é uma artista de mão cheia.", "Set 2026"),
                Review("Ana R.", 5, "Já é minha profissional fixa. Super pontual e cuidadosa com cada detalhe.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("13:30", 30, "economy"),
                DiscountSlot("14:00", 35, "economy"),
                DiscountSlot("14:30", 30, "economy"),
                DiscountSlot("15:00", 25, "economy"),
                DiscountSlot("17:30", 30, "urgent")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1560066984-138dadb4c035?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 2: Lumina Studio & Nail Bar (Pinheiros - Nails)
        // -------------------------------------------------------------
        Salon(
            id = "s2",
            name = "Lumina Studio & Nail Bar",
            neighborhood = "Pinheiros",
            category = "nails",
            rating = 4.80,
            reviewsCount = 174,
            distanceKm = 1.2,
            lat = -23.5682,
            lng = -46.6801,
            address = "R. dos Pinheiros, 412 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Larissa marcou Manicure Spa há 19 min",
            socialAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Beatriz Albuquerque",
                role = "Nail Designer & Especialista em Gel",
                avatarUrl = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Larissa, Juliana e Sofia frequentam aqui com frequência"
            ),
            services = listOf(
                Service("srv_s2_1", "Design de Sobrancelhas & Spa de Mãos", 50, 85.0, "nails", "Alinhamento com linha orgânica, esfoliação e hidratação com cera morna."),
                Service("srv_s2_2", "Manicure Russa & Blindagem Gel", 60, 120.0, "nails", "Cuticulagem técnica a seco com micromotor e blindagem de alta resistência."),
                Service("srv_s2_3", "Spa dos Pés & Reflexologia Express", 45, 75.0, "nails", "Esfoliação com sais marinhos, hidratação oclusiva e massagem podal relaxante."),
                Service("srv_s2_4", "Lash Lifting de Cílios com Tintura", 50, 110.0, "esthetic", "Curvatura e pigmentação dos fios naturais para realçar o olhar."),
                Service("srv_s2_5", "Esmaltação em Gel Longa Duração", 45, 65.0, "nails", "Secagem rápida em cabine LED com brilho espelhado por até 21 dias.")
            ),
            staff = listOf(
                Staff("st3", "Fernanda Lima", "Master Nail Artist", 4.96, "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80", "Destaque da Casa"),
                Staff("st4", "Carla Dias", "Lash & Brow Designer", 4.85, "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Bia M.", 5, "Melhor nail art que já fiz! Fernanda é incrível, traço perfeito.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 25, "economy"),
                DiscountSlot("13:30", 30, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("16:00", 20, "urgent")
            ),
            galleryImages = getGallery("nails", "https://images.unsplash.com/photo-1633681926022-84c23e8cb2d6?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 3: Serena Spa & Terapias (Jardins - Massage)
        // -------------------------------------------------------------
        Salon(
            id = "s3",
            name = "Serena Spa & Terapias",
            neighborhood = "Jardins",
            category = "massage",
            rating = 4.90,
            reviewsCount = 312,
            distanceKm = 1.9,
            lat = -23.5714,
            lng = -46.6712,
            address = "Al. Gabriel Monteiro da Silva, 1420 - Jardim Paulistano, SP",
            imageUrl = "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Carolina reservou Massagem Relaxante há 8 min",
            socialAvatar = "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Monique Soares",
                role = "Terapeuta Ayurvédica & Spa Director",
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Carolina, Rafaela e Bianca recomendaram este spa"
            ),
            services = listOf(
                Service("srv_s3_1", "Massagem Sueca com Óleos Essenciais", 60, 180.0, "massage", "Massagem terapêutica de corpo inteiro com aromaterapia de lavanda e alecrim."),
                Service("srv_s3_2", "Drenagem Linfática Desintoxicante", 50, 160.0, "massage", "Manobras suaves para redução imediata de retenção de líquidos e toxinas."),
                Service("srv_s3_3", "Massagem Craniofacial & Alívio de Tensão", 35, 95.0, "massage", "Foco exclusivo em pontos de estresse na cabeça, pescoço e trapézio."),
                Service("srv_s3_4", "Candle Massage com Velas Morna", 60, 195.0, "massage", "Manteiga vegetal cosmética aquecida para hidratação e relaxamento profundo."),
                Service("srv_s3_5", "Shiatsu Integrativo", 60, 170.0, "massage", "Pressão nos meridianos para equilíbrio bioenergético e alívio de nós musculares.")
            ),
            staff = listOf(
                Staff("st5", "Alessandra M.", "Fisioterapeuta", 4.98, "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Clara V.", 5, "Saí completamente renovada! As mãos da Alessandra são mágicas.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 30, "economy"),
                DiscountSlot("14:30", 35, "economy"),
                DiscountSlot("15:00", 30, "economy")
            ),
            galleryImages = getGallery("massage", "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 4: Dermacare Estética Facial (Itaim Bibi - Facial)
        // -------------------------------------------------------------
        Salon(
            id = "s4",
            name = "Dermacare Estética Facial",
            neighborhood = "Itaim Bibi",
            category = "facial",
            rating = 4.85,
            reviewsCount = 145,
            distanceKm = 2.3,
            lat = -23.5789,
            lng = -46.6765,
            address = "R. Amauri, 280 - Itaim Bibi, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1629909613654-28e377c37b09?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Gabriela agendou Limpeza Facial há 15 min",
            socialAvatar = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Dra. Camila Nogueira",
                role = "Médica Dermatologista (CRM/SP)",
                avatarUrl = "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Gabriela e Paula realizam protocolos estéticos aqui"
            ),
            services = listOf(
                Service("srv_s4_1", "Limpeza de Pele Ultrassônica & Peeling", 75, 210.0, "esthetic", "Remoção suave de impurezas com peeling de diamante e máscara calmante de camomila."),
                Service("srv_s4_2", "Hidratação Oclusiva de Ácido Hialurônico", 45, 140.0, "esthetic", "Revitalização celular intensa para viço imediato e preenchimento de linhas finas."),
                Service("srv_s4_3", "Revitalização Facial com Vitamina C Pura", 50, 165.0, "esthetic", "Ação antioxidante clareadora e estimulante de síntese de colágeno."),
                Service("srv_s4_4", "Drenagem Linfática Facial Anti-Olheiras", 35, 90.0, "esthetic", "Desinchaço do contorno dos olhos e definição do contorno da mandíbula."),
                Service("srv_s4_5", "Peeling Químico de Ácido Mandélico", 45, 180.0, "esthetic", "Renovação cutânea suave para equilíbrio de oleosidade e manchas solares.")
            ),
            staff = listOf(
                Staff("st6", "Dra. Vanessa", "Dermatologista Esteta", 4.96, "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Juliana F.", 5, "Dra. Vanessa é excepcional! Minha pele melhorou muito.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("09:30", 20, "economy"),
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("13:45", 35, "economy")
            ),
            galleryImages = getGallery("facial", "https://images.unsplash.com/photo-1629909613654-28e377c37b09?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 5: Barbearia Maestro (Vila Madalena - Barber)
        // -------------------------------------------------------------
        Salon(
            id = "s5",
            name = "Barbearia Maestro",
            neighborhood = "Vila Madalena",
            category = "barber",
            rating = 4.70,
            reviewsCount = 189,
            distanceKm = 2.1,
            lat = -23.5555,
            lng = -46.6920,
            address = "R. Aspicuelta, 78 - Vila Madalena, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Lucas agendou Barboterapia há 22 min",
            socialAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Marcos Vinicius Silva",
                role = "Mestre Barbeiro & Visagista",
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "3 contatos da sua rede cortam o cabelo neste espaço"
            ),
            services = listOf(
                Service("srv_s5_1", "Corte Masculino & Barba Tradicional", 40, 70.0, "barber", "Corte estilizado com navalha, barba na toalha quente e finalização com produtos premium."),
                Service("srv_s5_2", "Corte Cabelo Degradê Fade & Tesoura", 35, 50.0, "barber", "Degradê na navalha ou corte na tesoura clássico com lavagem mentolada refrescante."),
                Service("srv_s5_3", "Barboterapia Completa com Vapor de Ozônio", 30, 45.0, "barber", "Toalha quente, vapor de ozônio, hidratação de barba e massagem facial relaxante."),
                Service("srv_s5_4", "Selagem Capilar Masculina Redutora", 45, 85.0, "barber", "Alinhamento dos fios rebeldes sem formol e com brilho acetinado."),
                Service("srv_s5_5", "Sobrancelha Masculina na Navalha", 15, 25.0, "barber", "Limpeza natural das sobrancelhas preservando os traços masculinos.")
            ),
            staff = listOf(
                Staff("st7", "Marcus V.", "Mestre Barbeiro", 4.85, "https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=120&q=80"),
                Staff("st8", "Thiago R.", "Barbeiro Senior", 4.72, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Ricardo G.", 5, "Melhor barbearia da Vila. Marcus é um artista!", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("09:00", 25, "economy"),
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("14:00", 25, "economy"),
                DiscountSlot("17:00", 20, "urgent")
            ),
            galleryImages = getGallery("barber", "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 6: Arte Nail Studio (Consolação - Nails)
        // -------------------------------------------------------------
        Salon(
            id = "s6",
            name = "Arte Nail Studio",
            neighborhood = "Consolação",
            category = "nails",
            rating = 4.60,
            reviewsCount = 98,
            distanceKm = 2.8,
            lat = -23.5540,
            lng = -46.6590,
            address = "R. da Consolação, 2345 - Consolação, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Sofia fez Alongamento em Gel há 31 min",
            socialAvatar = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Letícia Hashimoto",
                role = "Nail Artist & Lash Designer",
                avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Sofia e Beatriz são clientes assíduas deste estúdio"
            ),
            services = listOf(
                Service("srv_s6_1", "Nail Art Premium & Gel Glitter", 55, 95.0, "nails", "Aplicação de gel com nail art personalizado, glitter 3D e acabamento em top coat fosco ou brilhante."),
                Service("srv_s6_2", "Manicure Clássica & Esmaltação Nacional", 40, 48.0, "nails", "Cuticulagem delicada e esmaltação tradicional com secagem rápida."),
                Service("srv_s6_3", "Alongamento de Fibra de Vidro", 90, 190.0, "nails", "Extensão ultra natural com filamentos de fibra e acabamento em gel moldado."),
                Service("srv_s6_4", "Banho de Gel Fortalecedor", 50, 80.0, "nails", "Camada protetora em unhas naturais para prevenir descamações e quebras."),
                Service("srv_s6_5", "Pedicure Spa Esfoliante", 45, 60.0, "nails", "Cuidado completo dos pés com lixamento podal e hidratação nutritiva profunda.")
            ),
            staff = listOf(
                Staff("st9", "Tatiane Cruz", "Nail Artist", 4.78, "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Gabi L.", 5, "Tatiane é super criativa! Amei o resultado.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:30", 20, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("15:30", 25, "economy"),
                DiscountSlot("17:30", 30, "urgent")
            ),
            galleryImages = getGallery("nails", "https://images.unsplash.com/photo-1604654894610-df63bc536371?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 7: Glow Skin & Beauty (Pinheiros - Facial)
        // -------------------------------------------------------------
        Salon(
            id = "s7",
            name = "Glow Skin & Beauty",
            neighborhood = "Pinheiros",
            category = "facial",
            rating = 4.80,
            reviewsCount = 127,
            distanceKm = 1.5,
            lat = -23.5610,
            lng = -46.6820,
            address = "R. Teodoro Sampaio, 1040 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Fernanda agendou Peeling de Diamante há 11 min",
            socialAvatar = "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Dra. Viviane Guimarães",
                role = "Biomédica Esteta & Cosmiatra",
                avatarUrl = "https://images.unsplash.com/photo-1594744803329-e58b31de8bf5?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Fernanda, Camila e Carolina cuidam da pele aqui"
            ),
            services = listOf(
                Service("srv_s7_1", "Tratamento Skincare Personalizado", 60, 150.0, "esthetic", "Análise de pele, limpeza profunda, hidratação intensiva e proteção UV com produtos veganos."),
                Service("srv_s7_2", "Limpeza de Pele com Extração a Vácuo", 55, 130.0, "esthetic", "Remoção de cravos e impurezas por sucção delicada sem marcas avermelhadas."),
                Service("srv_s7_3", "Máscara Hidroplástica Calmante de Calêndula", 40, 95.0, "esthetic", "Infusão biológica para regeneração de peles sensibilizadas pelo sol ou poluição."),
                Service("srv_s7_4", "Massagem Modeladora Facial com Gua Sha", 35, 85.0, "esthetic", "Estímulo circulatório com pedras nobres de quartzo verde para efeito lifting."),
                Service("srv_s7_5", "Microagulhamento com Fatores de Crescimento", 60, 220.0, "esthetic", "Indução percutânea de colágeno para firmeza cutânea e suavização de poros.")
            ),
            staff = listOf(
                Staff("st10", "Bianca Costa", "Esteticista Senior", 4.89, "https://images.unsplash.com/photo-1487412720507-e7ab37603c6f?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Renata A.", 5, "Minha pele nunca ficou tão bonita! Bianca é excepcional.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 25, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("15:00", 30, "economy"),
                DiscountSlot("17:00", 25, "urgent")
            ),
            galleryImages = getGallery("facial", "https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 8: Studio Mix Beleza & Bem-Estar (Perdizes - Mixed)
        // -------------------------------------------------------------
        Salon(
            id = "s8",
            name = "Studio Mix Beleza & Bem-Estar",
            neighborhood = "Perdizes",
            category = "mixed",
            rating = 4.75,
            reviewsCount = 203,
            distanceKm = 3.2,
            lat = -23.5335,
            lng = -46.6690,
            address = "R. Cardoso de Almeida, 542 - Perdizes, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Paula agendou Corte & Nutrição há 25 min",
            socialAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Sabrina Sato Oliveira",
                role = "Hair Concept & Visagista",
                avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Paula e Mariana avaliaram este salão com 5 estrelas"
            ),
            services = listOf(
                Service("srv_s8_1", "Combo Cabelo & Maquiagem", 90, 195.0, "hair", "Escova modeladora, maquiagem natural e finalização com penteado para eventos especiais."),
                Service("srv_s8_2", "Escova Lisa com Chapinha Cerâmica", 45, 80.0, "hair", "Lavagem com shampoo purificante e acabamento extra liso polido."),
                Service("srv_s8_3", "Penteado Semi-Preso para Festas", 50, 110.0, "hair", "Tranças, babyliss com ondas soltas ou coques despojados com fixação flexível."),
                Service("srv_s8_4", "Design de Sobrancelhas com Henna Orgânica", 40, 55.0, "esthetic", "Definição e preenchimento de falhas do olhar com tintura vegetal hipoalergênica."),
                Service("srv_s8_5", "Spa de Mãos & Pés Simultâneo", 60, 115.0, "nails", "Agilidade e cuidado premium para mãos e pés realizados simultaneamente por duas profissionais.")
            ),
            staff = listOf(
                Staff("st12", "Vanessa Lima", "Hair & Makeup Artist", 4.82, "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Isabela G.", 5, "Perfeito para eventos! Saí completamente deslumbrante.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 20, "economy"),
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("14:00", 35, "economy"),
                DiscountSlot("16:00", 25, "urgent")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 9: Vintage Barber Club (Pinheiros - Barber)
        // -------------------------------------------------------------
        Salon(
            id = "s9",
            name = "Vintage Barber Club",
            neighborhood = "Pinheiros",
            category = "barber",
            rating = 4.85,
            reviewsCount = 164,
            distanceKm = 1.1,
            lat = -23.5645,
            lng = -46.6880,
            address = "R. Mourato Coelho, 612 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Gabriel agendou Fade & Barba há 14 min",
            socialAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Ricardo Telles",
                role = "Grooming Specialist & Master Barber",
                avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Amigos da sua região indicam o Ricardo para corte clássico"
            ),
            services = listOf(
                Service("srv_s9_1", "Corte Executivo & Camuflagem de Barba", 45, 85.0, "barber", "Corte refinado na tesoura, alinhamento de barba com vapor de ozônio e tônico fortalecedor."),
                Service("srv_s9_2", "Corte Cabelo Clássico", 35, 55.0, "barber", "Tesoura ou máquina com acabamento de pezinho e costeletas na lâmina navalhete."),
                Service("srv_s9_3", "Barboterapia Tradicional com Toalha Quente", 30, 45.0, "barber", "Toalha quente com essência de menta e óleo amaciante anti-irritação."),
                Service("srv_s9_4", "Hidratação Capilar Masculina", 30, 50.0, "barber", "Nutrição profunda para fios secos e alívio de descamações do couro cabeludo."),
                Service("srv_s9_5", "Acabamento Rápido de Pezinho e Barba", 15, 25.0, "barber", "Manutenção rápida dos contornos entre visitas completas ao barbeiro.")
            ),
            staff = listOf(
                Staff("st14", "Leandro Torres", "Barbeiro Especialista", 4.90, "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Guilherme P.", 5, "Atendimento impecável! Leandro manja demais de corte clássico.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:30", 20, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("17:30", 25, "urgent")
            ),
            galleryImages = getGallery("barber", "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 10: Espaço Capelli D'Oro (Jardins - Hair)
        // -------------------------------------------------------------
        Salon(
            id = "s10",
            name = "Espaço Capelli D'Oro",
            neighborhood = "Jardins",
            category = "hair",
            rating = 4.92,
            reviewsCount = 310,
            distanceKm = 2.0,
            lat = -23.5680,
            lng = -46.6660,
            address = "R. Oscar Freire, 1120 - Cerqueira César, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1562322140-8baeececf3df?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Rafaela agendou Balayage Glow há 40 min",
            socialAvatar = "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Patrícia Kallas",
                role = "Master Balayage & Morenas Iluminadas",
                avatarUrl = "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Rafaela, Juliana e Camila fazem mechas aqui"
            ),
            services = listOf(
                Service("srv_s10_1", "Corte Visagista & Escova Glow", 60, 160.0, "hair", "Consultoria de visagismo facial, corte personalizado e finalização com protetor térmico orgânico."),
                Service("srv_s10_2", "Terapia Capilar Antiqueda & Ozonioterapia", 50, 150.0, "hair", "Desobstrução folicular profunda com vapor e laser infravermelho estimulante."),
                Service("srv_s10_3", "Hidratação Kérastase Fusio-Dose", 45, 170.0, "hair", "Tratamento sob medida com concentrado e booster de alta performance para cabelos sensibilizados."),
                Service("srv_s10_4", "Escova Modeladora com Babyliss Ondas", 50, 110.0, "hair", "Ondas volumosas e duradouras com fixação sedosa sem ressecar os fios."),
                Service("srv_s10_5", "Matização e Banho de Brilho", 45, 130.0, "hair", "Neutralização de reflexos indesejados em cabelos loiros e mechas com nutrição.")
            ),
            staff = listOf(
                Staff("st15", "Claudio Mantovani", "Diretor Criativo", 4.96, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Marina C.", 5, "O Claudio transformou minha autoestima. Corte mais elegante que já fiz.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("13:00", 35, "economy"),
                DiscountSlot("13:45", 30, "economy"),
                DiscountSlot("16:30", 25, "urgent")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1562322140-8baeececf3df?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 11: Esmalteria Petit Spa (Itaim Bibi - Nails)
        // -------------------------------------------------------------
        Salon(
            id = "s11",
            name = "Esmalteria Petit Spa",
            neighborhood = "Itaim Bibi",
            category = "nails",
            rating = 4.78,
            reviewsCount = 156,
            distanceKm = 2.5,
            lat = -23.5840,
            lng = -46.6780,
            address = "R. Joaquim Floriano, 871 - Itaim Bibi, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Bianca marcou Esmaltação em Gel há 6 min",
            socialAvatar = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Vanessa Dumont",
                role = "Esmaltação em Gel & Cutilagem Russa",
                avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Bianca, Sofia e Carolina fazem manutenção de unhas aqui"
            ),
            services = listOf(
                Service("srv_s11_1", "Manicure Russa Combinada & Esmaltação Gel", 60, 110.0, "nails", "Cuticulagem a seco com micromotor, blindagem de queratina e cor durável por 20 dias."),
                Service("srv_s11_2", "Esmaltação Simples Mãos com Lixamento", 35, 45.0, "nails", "Lixamento técnico, retirada suave de pelinhas e esmalte premium de alta cobertura."),
                Service("srv_s11_3", "Spa dos Pés com Parafina Líquida", 50, 85.0, "nails", "Amolecimento de asperezas e calosidades com botas térmicas e nutrição intensiva."),
                Service("srv_s11_4", "Francesinha Reversa em Gel Estruturado", 70, 130.0, "nails", "Técnica artística avançada com borda livre duradoura e simetria perfeita."),
                Service("srv_s11_5", "Remoção Segura de Alongamento sem Danos", 40, 50.0, "nails", "Retirada mecânica cuidadosa preservando 100% da integridade da lâmina ungueal.")
            ),
            staff = listOf(
                Staff("st16", "Tatiana Smirnova", "Nail Master", 4.91, "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Bruna M.", 5, "A manicure russa da Tatiana é a mais perfeita de SP!", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:30", 25, "economy"),
                DiscountSlot("14:30", 30, "economy")
            ),
            galleryImages = getGallery("nails", "https://images.unsplash.com/photo-1519014816548-bf5fe059798b?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 12: Lotus Terapias & Spa (Vila Madalena - Massage)
        // -------------------------------------------------------------
        Salon(
            id = "s12",
            name = "Lotus Terapias & Spa",
            neighborhood = "Vila Madalena",
            category = "massage",
            rating = 4.88,
            reviewsCount = 220,
            distanceKm = 1.8,
            lat = -23.5510,
            lng = -46.6905,
            address = "R. Harmonia, 340 - Vila Madalena, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Mariana fez Massagem com Pedras Quentes há 17 min",
            socialAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Clara Fontana",
                role = "Terapeuta Holística & Aromaterapeuta",
                avatarUrl = "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Mariana e Beatriz elogiaram a calma deste espaço"
            ),
            services = listOf(
                Service("srv_s12_1", "Massagem com Pedras Quentes Vulcânicas", 70, 195.0, "massage", "Termoterapia profunda com pedras de basalto aquecidas para alívio imediato de tensão."),
                Service("srv_s12_2", "Drenagem Corporal Anti-Inchaço", 50, 150.0, "massage", "Estimulação do sistema linfático com manobras rítmicas e óleos botânicos."),
                Service("srv_s12_3", "Quick Massage Express na Cadeira Ergonômica", 25, 65.0, "massage", "Alívio rápido de dores cervicais e lombares perfeito para pausas no meio da rotina."),
                Service("srv_s12_4", "Massagem Terapêutica Desportiva", 60, 180.0, "massage", "Liberação miofascial com pressão firme para alívio de fadiga muscular pós-treino."),
                Service("srv_s12_5", "Banho de Imersão Relaxante com Sais e Ervas", 40, 120.0, "massage", "Banheira aromaterápica em ofurô com extratos calmantes e cromoterapia.")
            ),
            staff = listOf(
                Staff("st17", "Miriam Tanaka", "Terapeuta Corporal", 4.94, "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Luciana R.", 5, "Lugar de paz absoluta no meio da correria de SP.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 30, "economy"),
                DiscountSlot("15:00", 35, "economy")
            ),
            galleryImages = getGallery("massage", "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 13: Pureza Estética Avançada (Perdizes - Facial)
        // -------------------------------------------------------------
        Salon(
            id = "s13",
            name = "Pureza Estética Avançada",
            neighborhood = "Perdizes",
            category = "facial",
            rating = 4.70,
            reviewsCount = 112,
            distanceKm = 3.5,
            lat = -23.5380,
            lng = -46.6740,
            address = "R. Monte Alegre, 980 - Perdizes, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1512290900672-1f02a64c483a?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Paula agendou Microagulhamento há 23 min",
            socialAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Dra. Danielle Meirelles",
                role = "Farmacêutica Esteta Especialista em Peelings",
                avatarUrl = "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Paula e Gabriela são atendidas pela Dra. Danielle"
            ),
            services = listOf(
                Service("srv_s13_1", "Revitalização Facial & Máscara de Ouro", 60, 175.0, "esthetic", "Esfoliação com ácido glicólico suave e hidratação com partículas biominerais luminosas."),
                Service("srv_s13_2", "Peeling de Diamante com Máscara de Colágeno", 50, 145.0, "esthetic", "Microdermoabrasão superficial para uniformizar textura e poros dilatados."),
                Service("srv_s13_3", "Limpeza de Pele Profunda Tradicional", 70, 160.0, "esthetic", "Emoliência térmica, extração manual sem cicatrizes e alta frequência antibacteriana."),
                Service("srv_s13_4", "Radiofrequência Facial Efeito Cinderela", 45, 190.0, "esthetic", "Aquecimento controlado das camadas dérmicas para retração imediata da flacidez."),
                Service("srv_s13_5", "Clareamento de Axilas e Virilhas com Peeling", 40, 90.0, "depilation", "Protocolo despigmentante com ácidos suaves formulado especificamente para áreas sensíveis.")
            ),
            staff = listOf(
                Staff("st18", "Dra. Carolina Rios", "Biomédica Esteta", 4.86, "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Monica T.", 5, "Pele viçosa e luminosa logo após a sessão.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("13:30", 25, "economy"),
                DiscountSlot("16:00", 30, "urgent")
            ),
            galleryImages = getGallery("facial", "https://images.unsplash.com/photo-1512290900672-1f02a64c483a?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 14: Barbearia República (Consolação - Barber)
        // -------------------------------------------------------------
        Salon(
            id = "s14",
            name = "Barbearia República",
            neighborhood = "Consolação",
            category = "barber",
            rating = 4.65,
            reviewsCount = 138,
            distanceKm = 2.9,
            lat = -23.5505,
            lng = -46.6540,
            address = "R. Augusta, 1420 - Consolação, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Matheus agendou Barba Clássica há 10 min",
            socialAvatar = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Thiago Barone",
                role = "Barbeiro Tradicional & Visagista",
                avatarUrl = "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Excelente recomendação para cortes urbanos no centro"
            ),
            services = listOf(
                Service("srv_s14_1", "Corte Clássico na Tesoura & Barba", 45, 65.0, "barber", "Estilo clássico e despojado na tesoura e navalha com finalização em pomada matte."),
                Service("srv_s14_2", "Corte de Cabelo Máquina e Tesoura", 30, 45.0, "barber", "Corte ágil e prático para o dia a dia com lavagem refrescante inclusa."),
                Service("srv_s14_3", "Barba Modelada na Navalha Tradicional", 25, 35.0, "barber", "Alinhamento preciso das linhas das bochechas e pescoço com óleo hidratante vegetal."),
                Service("srv_s14_4", "Camuflagem de Fios Brancos Masculina", 35, 60.0, "barber", "Tonalização sutil e discreta para amenizar cabelos e barba grisalhos sem aspecto pintado."),
                Service("srv_s14_5", "Higienização e Esfoliação Facial Masculina", 30, 40.0, "esthetic", "Limpeza facial rápida com sabonete antioleosidade, esfoliação suave e hidratação.")
            ),
            staff = listOf(
                Staff("st19", "Otavio Lima", "Barbeiro Tradicional", 4.79, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Eduardo F.", 5, "Rápido, pontual e muito gente fina. Recomendo!", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 20, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("17:00", 20, "urgent")
            ),
            galleryImages = getGallery("barber", "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 15: Velvet Hair Design (Vila Madalena - Hair)
        // -------------------------------------------------------------
        Salon(
            id = "s15",
            name = "Velvet Hair Design",
            neighborhood = "Vila Madalena",
            category = "hair",
            rating = 4.83,
            reviewsCount = 195,
            distanceKm = 2.2,
            lat = -23.5570,
            lng = -46.6950,
            address = "R. Girassol, 210 - Vila Madalena, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Larissa agendou Corte Orgânico há 5 min",
            socialAvatar = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Lucas Alencar",
                role = "Colorista Criativo & Hair Designer",
                avatarUrl = "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Larissa, Beatriz e Camila cortam o cabelo na Vila Madalena aqui"
            ),
            services = listOf(
                Service("srv_s15_1", "Corte Moderno Bob & Nutrição Lipídica", 50, 135.0, "hair", "Alinhamento de fios, corte moderno e nutrição à base de óleos nobres."),
                Service("srv_s15_2", "Escova Modeladora Suave", 40, 75.0, "hair", "Lavagem relaxante no lavatório e escovação com brilho sedoso acetinado."),
                Service("srv_s15_3", "Cauterização Capilar com Queratina Hidrolisada", 60, 160.0, "hair", "Selamento das cutículas abertas e preenchimento de massa para cabelos com química."),
                Service("srv_s15_4", "Cronograma Capilar Fase Nutrição com Óleos", 45, 90.0, "hair", "Nutrição lipídica profunda com óleos de macadâmia, mirra e manteiga de karité."),
                Service("srv_s15_5", "Ajuste e Corte de Franja", 20, 40.0, "hair", "Ajuste rápido de comprimento e caimento da franja com secagem direcionada.")
            ),
            staff = listOf(
                Staff("st20", "Renata Vasconcellos", "Hair Stylist", 4.90, "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Priscila N.", 5, "Corte super moderno, amei o caimento!", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:30", 25, "economy"),
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("16:00", 25, "urgent")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1521590832167-7bcbfaa6381f?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 16: Nails & Co. Express (Jardins - Nails)
        // -------------------------------------------------------------
        Salon(
            id = "s16",
            name = "Nails & Co. Express",
            neighborhood = "Jardins",
            category = "nails",
            rating = 4.74,
            reviewsCount = 142,
            distanceKm = 1.7,
            lat = -23.5660,
            lng = -46.6695,
            address = "Al. Lorena, 1380 - Jardins, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Camila fez Blindagem de Diamante há 14 min",
            socialAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Amanda Prado",
                role = "Nail Designer de Alongamento Fibra",
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Camila e Rafaela frequentam este espaço nos Jardins"
            ),
            services = listOf(
                Service("srv_s16_1", "Manicure Express & Hidratação de Mãos", 40, 65.0, "nails", "Esmaltação rápida com produtos importados e massagem relaxante nas mãos."),
                Service("srv_s16_2", "Combo Manicure e Pedicure Tradicional", 65, 95.0, "nails", "Cuidado completo das unhas das mãos e pés em sequência contínua e sem esperas."),
                Service("srv_s16_3", "Esmaltação Rápida com Lixamento", 25, 38.0, "nails", "Lixamento, alinhamento de bordas e aplicação de esmalte de alta durabilidade."),
                Service("srv_s16_4", "Blindagem de Queratina para Unhas Frágeis", 45, 70.0, "nails", "Camada de proteção acrílica em gel para evitar lascas e quebras de unhas fracas."),
                Service("srv_s16_5", "Depilação de Buço e Queixo com Linha Egípcia", 20, 35.0, "depilation", "Epilação facial higiênica e hipoalergênica que arranca os pelos pela raiz.")
            ),
            staff = listOf(
                Staff("st21", "Solange Prado", "Manicure", 4.82, "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Beatriz H.", 5, "Prático e rápido para o dia a dia!", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("12:00", 20, "economy"),
                DiscountSlot("14:30", 30, "economy")
            ),
            galleryImages = getGallery("nails", "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 17: Zen Terapia Corporal (Pinheiros - Massage)
        // -------------------------------------------------------------
        Salon(
            id = "s17",
            name = "Zen Terapia Corporal",
            neighborhood = "Pinheiros",
            category = "massage",
            rating = 4.91,
            reviewsCount = 180,
            distanceKm = 0.9,
            lat = -23.5615,
            lng = -46.6890,
            address = "R. Simão Álvares, 415 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Juliana agendou Shiatsu Integrativo há 28 min",
            socialAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Rodrigo Zanin",
                role = "Fisioterapeuta & Massoterapeuta",
                avatarUrl = "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Juliana, Bianca e Carolina fazem liberação miofascial aqui"
            ),
            services = listOf(
                Service("srv_s17_1", "Sessão de Shiatsu & Reflexologia Podal", 60, 165.0, "massage", "Pressão pontual nos meridianos energéticos e massagem revigorante nos pés."),
                Service("srv_s17_2", "Massagem Relaxante com Aromaterapia Botânica", 50, 140.0, "massage", "Manobras lentas e envolventes com óleo morno enriquecido com bergamota e camomila."),
                Service("srv_s17_3", "Reflexologia Podal Terapêutica", 40, 85.0, "massage", "Estimulação reflexa dos pontos correspondentes aos órgãos na sola dos pés."),
                Service("srv_s17_4", "Drenagem Linfática Corporal com Desintoxicação", 60, 175.0, "massage", "Aceleração do retorno linfático com foco em membros inferiores e abdômen."),
                Service("srv_s17_5", "Massagem Ayurvédica com Óleos Aquecidos", 70, 190.0, "massage", "Técnica milenar indiana que atua no sistema circulatório e descompressão articular.")
            ),
            staff = listOf(
                Staff("st22", "Kenji Sato", "Mestre em Shiatsu", 4.97, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Rodrigo B.", 5, "Kenji é incrível. Saí sem nenhuma dor.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 25, "economy"),
                DiscountSlot("13:00", 35, "economy"),
                DiscountSlot("17:00", 30, "urgent")
            ),
            galleryImages = getGallery("massage", "https://images.unsplash.com/photo-1519823551278-64ac92734fb1?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 18: DermoLaser Estética (Itaim Bibi - Facial)
        // -------------------------------------------------------------
        Salon(
            id = "s18",
            name = "DermoLaser Estética",
            neighborhood = "Itaim Bibi",
            category = "facial",
            rating = 4.80,
            reviewsCount = 165,
            distanceKm = 2.6,
            lat = -23.5820,
            lng = -46.6730,
            address = "R. Pedroso Alvarenga, 1200 - Itaim Bibi, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Beatriz fez Peeling Ultrassônico há 16 min",
            socialAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Dra. Luciana Bicalho",
                role = "Médica Dermatologista Estética",
                avatarUrl = "https://images.unsplash.com/photo-1598256989800-fe5f95da9787?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Beatriz e Mariana recomendam os tratamentos a laser"
            ),
            services = listOf(
                Service("srv_s18_1", "Protocolo Glow Facial & Peeling de Ácido Hialurônico", 60, 220.0, "esthetic", "Laser de baixa intensidade com infusão de ácido hialurônico para hidratação máxima."),
                Service("srv_s18_2", "Limpeza de Pele Fotônica com Luz LED Azul", 65, 170.0, "esthetic", "Ação bactericida contra acne associada a extração indolor de comedões."),
                Service("srv_s18_3", "Drenagem Facial Pós-Procedimento", 45, 130.0, "esthetic", "Redução acelerada de edemas faciais e reativação da microcirculação cutânea."),
                Service("srv_s18_4", "Tratamento Revitalizante de Olheiras e Pálpebras", 40, 110.0, "esthetic", "Microcorrentes e sérum de cafeína pura para clareamento da região periocular."),
                Service("srv_s18_5", "Depilação a Laser Alexandrite Região Facial", 30, 120.0, "depilation", "Destruição do folículo piloso com tecnologia de resfriamento para conforto total.")
            ),
            staff = listOf(
                Staff("st23", "Dra. Gabriela Fontes", "Dermatofuncional", 4.92, "https://images.unsplash.com/photo-1559839734-2b71ea197ec2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Ana Paula G.", 5, "Resultado sensacional já na primeira sessão.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 20, "economy"),
                DiscountSlot("14:00", 30, "economy")
            ),
            galleryImages = getGallery("facial", "https://images.unsplash.com/photo-1516549655169-df83a0774514?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 19: Barba & Navalha (Perdizes - Barber)
        // -------------------------------------------------------------
        Salon(
            id = "s19",
            name = "Barba & Navalha",
            neighborhood = "Perdizes",
            category = "barber",
            rating = 4.72,
            reviewsCount = 130,
            distanceKm = 3.1,
            lat = -23.5350,
            lng = -46.6710,
            address = "R. Desembargador do Vale, 320 - Perdizes, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Felipe fez Corte Tesoura há 21 min",
            socialAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Alexandre Fonseca",
                role = "Barber Designer & Barboterapeuta",
                avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Espaço muito bem avaliado por conhecidos em Perdizes"
            ),
            services = listOf(
                Service("srv_s19_1", "Navalhete Clássico & Tratamento Capilar", 45, 75.0, "barber", "Corte tradicional com navalhete, massagem capilar e loção pós-barba refrescante."),
                Service("srv_s19_2", "Corte Masculino Degradê Navalhado", 35, 50.0, "barber", "Fade de alta precisão com acabamento milimétrico e penteado modelado."),
                Service("srv_s19_3", "Barba Esculpida com Esfoliação Térmica", 30, 40.0, "barber", "Linhas desenhadas com precisão e esfoliação contra foliculite no pescoço."),
                Service("srv_s19_4", "Descoloração e Platinado Global", 90, 180.0, "barber", "Abertura de tom uniforme com proteção dos fios e neutralização fria platinada."),
                Service("srv_s19_5", "Lavagem Mentolada & Massagem Craniana", 20, 30.0, "barber", "Shampoo mentolado adstringente com massagem nos pontos de alívio craniano.")
            ),
            staff = listOf(
                Staff("st24", "Diego Ramos", "Barbeiro Chefe", 4.84, "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Leonardo C.", 5, "Lugar nota 10, atendimento pontual e cerveja cortesia.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("09:30", 25, "economy"),
                DiscountSlot("13:30", 30, "economy")
            ),
            galleryImages = getGallery("barber", "https://images.unsplash.com/photo-1621605815971-fbc98d665033?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 20: Studio Blondie & More (Consolação - Hair)
        // -------------------------------------------------------------
        Salon(
            id = "s20",
            name = "Studio Blondie & More",
            neighborhood = "Consolação",
            category = "hair",
            rating = 4.68,
            reviewsCount = 110,
            distanceKm = 2.7,
            lat = -23.5530,
            lng = -46.6570,
            address = "R. Bela Cintra, 890 - Consolação, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1470259078437-5e97af7720b1?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Sofia agendou Iluminação & Brilho há 13 min",
            socialAvatar = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Isabela Ferraz",
                role = "Blond Specialist & Terapeuta Capilar",
                avatarUrl = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Sofia, Rafaela e Larissa cuidam do loiro aqui"
            ),
            services = listOf(
                Service("srv_s20_1", "Tonalização Express & Escova Modeladora", 60, 145.0, "hair", "Banho de brilho para iluminar mechas e escova polida com óleo de argan."),
                Service("srv_s20_2", "Corte Feminino com Texturização Suave", 45, 110.0, "hair", "Corte que confere balanço natural e leveza para cabelos finos ou volumosos."),
                Service("srv_s20_3", "Reconstrução Joico K-Pak 4 Passos", 50, 150.0, "hair", "Reposição de aminoácidos estruturais para fios elásticos e quebradiços."),
                Service("srv_s20_4", "Escova Modeladora Glamour Ondulada", 40, 70.0, "hair", "Finalização impecável para eventos com volume na raiz e pontas modeladas."),
                Service("srv_s20_5", "Design de Sobrancelhas na Pinça com Visagismo", 30, 45.0, "esthetic", "Harmonização do formato das sobrancelhas respeitando a simetria facial.")
            ),
            staff = listOf(
                Staff("st25", "Carla Nogueira", "Colorista", 4.77, "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Jessica R.", 4, "Meu loiro ficou renovado e sem frizz.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 20, "economy"),
                DiscountSlot("14:00", 35, "economy")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1470259078437-5e97af7720b1?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 21: Bella Donna Nail Bar (Vila Madalena - Nails)
        // -------------------------------------------------------------
        Salon(
            id = "s21",
            name = "Bella Donna Nail Bar",
            neighborhood = "Vila Madalena",
            category = "nails",
            rating = 4.79,
            reviewsCount = 140,
            distanceKm = 2.4,
            lat = -23.5585,
            lng = -46.6970,
            address = "R. Fradique Coutinho, 1380 - Vila Madalena, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Mariana agendou Spa dos Pés há 35 min",
            socialAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Natália Castro",
                role = "Nail Artist & Spa Podal",
                avatarUrl = "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Mariana e Juliana frequentam este espaço no Itaim"
            ),
            services = listOf(
                Service("srv_s21_1", "Spa dos Pés com Esfoliação & Parafina Morna", 50, 85.0, "nails", "Hidratação profunda para pés ressecados com esfoliação botânica e cera de parafina."),
                Service("srv_s21_2", "Combo Manicure e Pedicure Express", 50, 75.0, "nails", "Atendimento ágil para mãos e pés com cuticulagem e esmaltação perfeita."),
                Service("srv_s21_3", "Esmaltação em Gel nas Mãos", 45, 65.0, "nails", "Cor vibrante e película de gel resistente que não descasca ao lavar louça ou digitar."),
                Service("srv_s21_4", "Alongamento Polygel com Acabamento Natural", 80, 160.0, "nails", "Híbrido de pó acrílico e gel de cura rápida com curvatura C anatômica."),
                Service("srv_s21_5", "Depilação Meia Perna com Cera Hidrossolúvel de Mel", 30, 45.0, "depilation", "Epilação suave com resíduo removível em água para menor sensibilidade na pele.")
            ),
            staff = listOf(
                Staff("st26", "Lucia Barros", "Podóloga & Nail Designer", 4.88, "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Camila S.", 5, "Pés macios como de bebê! Atendimento nota 1000.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 30, "economy"),
                DiscountSlot("15:30", 30, "economy")
            ),
            galleryImages = getGallery("nails", "https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 22: Equilibrium Spa Urbano (Jardins - Massage)
        // -------------------------------------------------------------
        Salon(
            id = "s22",
            name = "Equilibrium Spa Urbano",
            neighborhood = "Jardins",
            category = "massage",
            rating = 4.89,
            reviewsCount = 245,
            distanceKm = 1.6,
            lat = -23.5670,
            lng = -46.6700,
            address = "R. Haddock Lobo, 950 - Cerqueira César, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Carolina agendou Drenagem Linfática há 9 min",
            socialAvatar = "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Helena Rangel",
                role = "Especialista em Drenagem Integrativa",
                avatarUrl = "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1548142813-c348350df52b?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Carolina, Bianca e Camila elogiam o atendimento acolhedor"
            ),
            services = listOf(
                Service("srv_s22_1", "Drenagem Linfática Corporal Método Renata França", 60, 210.0, "massage", "Manobras manuais precisas que reduzem edemas e remodelam as curvas imediatamente."),
                Service("srv_s22_2", "Massagem Relaxante com Óleo Puro de Amêndoas", 50, 155.0, "massage", "Descompressão muscular suave para aliviar o cansaço do dia a dia."),
                Service("srv_s22_3", "Massagem Modeladora Redutora Turbinada", 50, 165.0, "massage", "Manobras vigorosas e profundas com foco em gordura localizada e celulite."),
                Service("srv_s22_4", "Massagem a Quatro Mãos em Perfeita Sincronia", 50, 270.0, "massage", "Duas terapeutas trabalhando em ritmo unificado para desconexão sensorial absoluta."),
                Service("srv_s22_5", "Esfoliação Corporal com Argila Verde e Chá Verde", 45, 130.0, "esthetic", "Remoção de células mortas e remineralização dérmica para toque de seda.")
            ),
            staff = listOf(
                Staff("st27", "Flavia Mendonça", "Especialista em Drenagem", 4.95, "https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Leticia O.", 5, "Resultado visível na hora! Desinchei horrores.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:30", 25, "economy"),
                DiscountSlot("14:00", 30, "economy"),
                DiscountSlot("17:00", 20, "urgent")
            ),
            galleryImages = getGallery("massage", "https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 23: Face & Care Concept (Pinheiros - Facial)
        // -------------------------------------------------------------
        Salon(
            id = "s23",
            name = "Face & Care Concept",
            neighborhood = "Pinheiros",
            category = "facial",
            rating = 4.86,
            reviewsCount = 155,
            distanceKm = 1.3,
            lat = -23.5655,
            lng = -46.6845,
            address = "R. Artur de Azevedo, 780 - Pinheiros, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1515377905703-c4788e51af15?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Gabriela agendou Hidragloss Lips há 27 min",
            socialAvatar = "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Dra. Gabriela Vasconcelos",
                role = "Cirurgiã Dentista & Harmonização Facial",
                avatarUrl = "https://images.unsplash.com/photo-1580894732444-8ecded7900cd?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 2,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1531746020798-e6953c6e8e04?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Gabriela e Danielle realizam cuidados faciais com a Dra. Gabriela"
            ),
            services = listOf(
                Service("srv_s23_1", "Fototerapia LED & Hidratação de Colágeno", 50, 160.0, "esthetic", "Luz emitida por diodos para regeneração celular e máscara oclusiva de colágeno marinho."),
                Service("srv_s23_2", "Limpeza de Pele Detox com Máscara de Carvão Ativado", 60, 145.0, "esthetic", "Purificação dos poros e remoção de poluição urbana com propriedades adstringentes."),
                Service("srv_s23_3", "Peeling Enzimático Suave de Romã e Mamão", 45, 135.0, "esthetic", "Renovação cutânea biológica sem irritação ou descamações agressivas."),
                Service("srv_s23_4", "Lifting Facial com Microcorrentes Dermoestimulantes", 45, 180.0, "esthetic", "Eletroestimulação de baixa intensidade para reeducação muscular facial e viço."),
                Service("srv_s23_5", "Depilação de Buço e Sobrancelha com Cera Calmante", 25, 45.0, "depilation", "Fórmula enriquecida com óleo de camomila para evitar vermelhidão na pele sensível.")
            ),
            staff = listOf(
                Staff("st28", "Dr. Bruno Silveira", "Dermatologista", 4.91, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Carolina D.", 5, "Clínica linda e procedimento super relaxante.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("11:00", 30, "economy"),
                DiscountSlot("15:00", 35, "economy")
            ),
            galleryImages = getGallery("facial", "https://images.unsplash.com/photo-1515377905703-c4788e51af15?auto=format&fit=crop&w=800&q=80")
        ),

        // -------------------------------------------------------------
        // Salão 24: Blend Beleza & Estilo (Itaim Bibi - Mixed)
        // -------------------------------------------------------------
        Salon(
            id = "s24",
            name = "Blend Beleza & Estilo",
            neighborhood = "Itaim Bibi",
            category = "mixed",
            rating = 4.77,
            reviewsCount = 178,
            distanceKm = 2.2,
            lat = -23.5795,
            lng = -46.6750,
            address = "R. Tabapuã, 620 - Itaim Bibi, São Paulo",
            imageUrl = "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?auto=format&fit=crop&w=800&q=80",
            socialProof = "#Paula agendou Corte Moderno há 18 min",
            socialAvatar = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
            leadStaff = LeadStaff(
                name = "Felipe Morais",
                role = "Hair Director & Expert em Texturas",
                avatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?auto=format&fit=crop&w=200&h=200&q=80",
                verified = true
            ),
            mutualNetwork = MutualNetwork(
                friendsCount = 3,
                avatars = listOf(
                    "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=100&h=100&q=80",
                    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=100&h=100&q=80"
                ),
                text = "Paula, Mariana e Beatriz frequentam este lounge"
            ),
            services = listOf(
                Service("srv_s24_1", "Combo Corte Unissex & Escova Polida", 55, 110.0, "hair", "Atendimento unissex prático com lavagem refrescante e modelagem rápida."),
                Service("srv_s24_2", "Barba Completa na Navalha & Penteado Masculino", 45, 65.0, "barber", "Alinhamento visual completo para compromissos e reuniões de trabalho."),
                Service("srv_s24_3", "Manicure Express Unissex de Higienização", 30, 38.0, "nails", "Unhas limpas, lixadas e com polimento fosco ou base incolor protetora."),
                Service("srv_s24_4", "Massagem Craniofacial no Lavatório", 20, 35.0, "massage", "Descompressão da nuca e têmporas com loção aromática durante a lavagem."),
                Service("srv_s24_5", "Escova Progressiva Orgânica Sem Formol", 90, 230.0, "hair", "Redução duradoura de volume com ativos botânicos e brilho intenso espelhado.")
            ),
            staff = listOf(
                Staff("st29", "Andre Vianna", "Master Hair Stylist", 4.86, "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=120&q=80")
            ),
            reviews = listOf(
                Review("Renato S.", 5, "Sempre salvo meu visual com o Andre. Rápido e perfeito.", "Set 2026")
            ),
            discountSlots = listOf(
                DiscountSlot("10:00", 20, "economy"),
                DiscountSlot("13:00", 30, "economy"),
                DiscountSlot("16:30", 25, "urgent")
            ),
            galleryImages = getGallery("hair", "https://images.unsplash.com/photo-1527799820374-dcf8d9d4a388?auto=format&fit=crop&w=800&q=80")
        )
    )

    fun getSalonById(salonId: String): Salon? {
        return salons.find { it.id == salonId }
    }

    fun toggleFavorite(salonId: String) {
        val current = _favoriteIds.value
        val isFav = current.contains(salonId)
        _favoriteIds.value = if (isFav) {
            current - salonId
        } else {
            current + salonId
        }
        database?.let { db ->
            repositoryScope.launch {
                if (isFav) {
                    db.favoriteDao().deleteFavorite(salonId)
                } else {
                    db.favoriteDao().insertFavorite(FavoriteEntity(salonId = salonId))
                }
            }
        }
    }

    fun isFavorite(salonId: String): Boolean = _favoriteIds.value.contains(salonId)

    /**
     * Calcula precificação do slot integrando com o PricingEngine canônico.
     */
    fun calculatePricing(salon: Salon, slotTime: String, service: Service): SlotPricing {
        val slot = salon.discountSlots.find { it.time == slotTime }
        val discount = slot?.discountPct ?: 0
        val isUrgent = slot?.type == "urgent"
        return PricingEngine.calculateSlotPricing(
            time = slotTime,
            basePrice = service.basePrice,
            discountPct = discount,
            isUrgent = isUrgent
        )
    }

    fun addAppointment(appointment: Appointment) {
        _appointments.value = listOf(appointment) + _appointments.value.filter { it.id != appointment.id }
        database?.let { db ->
            repositoryScope.launch {
                db.appointmentDao().insertAppointment(AppointmentEntity.fromDomain(appointment))
            }
        }
    }

    fun cancelAppointment(
        appointmentId: String,
        reason: String? = null,
        cancellationFee: Double? = null
    ) {
        _appointments.value = _appointments.value.map {
            if (it.id == appointmentId) {
                it.copy(
                    status = AppointmentStatus.CANCELLED_BY_USER,
                    cancellationReason = reason,
                    cancellationFee = cancellationFee
                )
            } else {
                it
            }
        }
        database?.let { db ->
            repositoryScope.launch {
                db.appointmentDao().cancelAppointment(
                    id = appointmentId,
                    status = AppointmentStatus.CANCELLED_BY_USER.name,
                    reason = reason,
                    fee = cancellationFee
                )
            }
        }
    }

    fun saveUserProfile(
        participantId: String,
        name: String,
        phone: String,
        lgpdAccepted: Boolean
    ) {
        val entity = UserProfileEntity(
            participantId = participantId,
            name = name,
            phone = phone,
            lgpdAccepted = lgpdAccepted,
            acceptedAtTimestamp = System.currentTimeMillis()
        )
        _userProfile.value = entity
        database?.let { db ->
            repositoryScope.launch {
                db.userProfileDao().insertOrUpdateProfile(entity)
            }
        }
    }

    fun saveSusEvaluation(evaluation: SUSEvaluation) {
        _latestSusEvaluation.value = evaluation
        database?.let { db ->
            repositoryScope.launch {
                db.susEvaluationDao().insertEvaluation(SusEvaluationEntity.fromDomain(evaluation))
            }
        }
    }

    fun getAppointmentsForSalon(salonId: String): List<Appointment> {
        return _appointments.value.filter { it.salon.id == salonId }
    }

    fun getAppointmentsForDate(dateDisplay: String): List<Appointment> {
        return _appointments.value.filter { it.dateDisplay == dateDisplay }
    }

    /**
     * Hard Delete LGPD: Exclui permanentemente todos os registros do usuário
     * nas 4 tabelas SQLite (appointments, favorites, user_profile, sus_evaluations)
     * e redefine todos os StateFlows para estado inicial.
     */
    fun clearAllData() {
        _favoriteIds.value = emptySet()
        _appointments.value = emptyList()
        _userProfile.value = null
        _latestSusEvaluation.value = null

        database?.let { db ->
            repositoryScope.launch {
                db.appointmentDao().deleteAllAppointments()
                db.favoriteDao().deleteAllFavorites()
                db.userProfileDao().deleteUserProfile()
                db.susEvaluationDao().deleteAllEvaluations()
            }
        }
    }

    /**
     * Versão suspensa para exclusão atômica síncrona em rotinas de teste.
     */
    suspend fun clearAllDataSuspend() {
        _favoriteIds.value = emptySet()
        _appointments.value = emptyList()
        _userProfile.value = null
        _latestSusEvaluation.value = null

        database?.let { db ->
            withContext(Dispatchers.IO) {
                db.appointmentDao().deleteAllAppointments()
                db.favoriteDao().deleteAllFavorites()
                db.userProfileDao().deleteUserProfile()
                db.susEvaluationDao().deleteAllEvaluations()
            }
        }
    }
}
