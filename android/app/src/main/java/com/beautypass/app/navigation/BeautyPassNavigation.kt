package com.beautypass.app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beautypass.app.R
import com.beautypass.app.theme.*
import com.beautypass.app.ui.components.bounceClick
import com.beautypass.app.ui.screens.appointments.AppointmentsScreen
import com.beautypass.app.ui.screens.checkout.CheckoutScreen
import com.beautypass.app.ui.screens.confirm.ConfirmScreen
import com.beautypass.app.ui.screens.detail.SalonDetailScreen
import com.beautypass.app.ui.screens.home.HomeScreen
import com.beautypass.app.ui.screens.map.MapScreen
import com.beautypass.app.ui.screens.onboarding.OnboardingScreen
import com.beautypass.app.ui.screens.ondemand.OnDemandScreen
import com.beautypass.app.ui.screens.partner.PartnerB2BScreen
import com.beautypass.app.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Boas-vindas")
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Map : Screen("map", "Mapa", Icons.Default.LocationOn)
    object OnDemand : Screen("on_demand", "Pedir Agora", Icons.Default.Bolt)
    object Appointments : Screen("appointments", "Agenda", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Ajustes", Icons.Default.Person)

    object Detail : Screen("detail/{salonId}", "Detalhes") {
        fun createRoute(salonId: String) = "detail/$salonId"
    }

    object Checkout : Screen("checkout/{salonId}/{serviceId}/{slotTime}?fulfillment={fulfillmentMode}", "Checkout") {
        fun createRoute(salonId: String, serviceId: String, slotTime: String, fulfillmentMode: String = "salon") =
            "checkout/$salonId/$serviceId/$slotTime?fulfillment=$fulfillmentMode"
    }

    object Confirm : Screen("confirm/{appointmentId}", "Confirmação") {
        fun createRoute(appointmentId: String) = "confirm/$appointmentId"
    }

    object PartnerB2B : Screen("partner_b2b", "Salão Parceiro B2B")
}

/**
 * BeautyPassApp: Orquestrador Geral de Navegação Nativa em Jetpack Compose (MVI / UDF)
 * com Bottom Navigation Bar espelhada 1:1 com a versão web (beautypass_app.html).
 */
@Composable
fun BeautyPassApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var participantCode by remember { mutableStateOf("P01") }
    var participantName by remember { mutableStateOf("") }

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Map.route,
        Screen.OnDemand.route,
        Screen.Appointments.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    // Barra de navegação com cantos superiores arredondados (24.dp) igual ao .bottom-nav da Web
                    Surface(
                        color = WarmWhite.copy(alpha = 0.96f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        shadowElevation = 14.dp,
                        border = BorderStroke(1.dp, Color(0xFFF0EFEA)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Aba 1: Início
                            BottomNavTabItem(
                                icon = Icons.Default.Home,
                                label = "Início",
                                isSelected = currentRoute == Screen.Home.route,
                                onClick = {
                                    if (currentRoute != Screen.Home.route) {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Aba 2: Mapa
                            BottomNavTabItem(
                                icon = Icons.Default.LocationOn,
                                label = "Mapa",
                                isSelected = currentRoute == Screen.Map.route,
                                onClick = {
                                    if (currentRoute != Screen.Map.route) {
                                        navController.navigate(Screen.Map.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Espaçador central para dar folga ao botão elevado "Pedir Agora"
                            Spacer(modifier = Modifier.weight(1.15f))

                            // Aba 3: Agenda
                            BottomNavTabItem(
                                icon = Icons.Default.CalendarMonth,
                                label = "Agenda",
                                isSelected = currentRoute == Screen.Appointments.route,
                                onClick = {
                                    if (currentRoute != Screen.Appointments.route) {
                                        navController.navigate(Screen.Appointments.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )

                            // Aba 4: Ajustes (espelhado 1:1 com a Web)
                            BottomNavTabItem(
                                icon = Icons.Default.Person,
                                label = "Ajustes",
                                isSelected = currentRoute == Screen.Profile.route,
                                onClick = {
                                    if (currentRoute != Screen.Profile.route) {
                                        navController.navigate(Screen.Profile.route) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Botão Central Flutuante Elevado com Silhueta Feminina Branca sobre Gradiente #00685F -> #0D9488 -> #14B8A6
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-16).dp)
                            .bounceClick(scaleDown = 0.92f) {
                                navController.navigate(Screen.OnDemand.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = CircleShape,
                                    ambientColor = DeepTeal.copy(alpha = 0.4f),
                                    spotColor = DeepTeal.copy(alpha = 0.4f)
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF00685F),
                                            Color(0xFF0D9488),
                                            Color(0xFF14B8A6)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(2.5.dp, WarmWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_beautypass_emblem_white),
                                contentDescription = "Pedir Agora (Encaixe Imediato)",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Pedir Agora",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DeepTeal
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(animationSpec = tween(140)) },
            exitTransition = { fadeOut(animationSpec = tween(110)) },
            popEnterTransition = { fadeIn(animationSpec = tween(140)) },
            popExitTransition = { fadeOut(animationSpec = tween(110)) }
        ) {
            // Tela 1: Onboarding & Auth
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    initialParticipantCode = participantCode,
                    onOnboardingComplete = { name, _, code ->
                        participantName = name
                        participantCode = code
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            // Tela 2: Home Feed & Hub Social
            composable(Screen.Home.route) {
                HomeScreen(
                    onSalonClick = { salonId ->
                        navController.navigate(Screen.Detail.createRoute(salonId))
                    },
                    onNavigateToOnDemand = {
                        navController.navigate(Screen.OnDemand.route)
                    },
                    onNavigateToMap = {
                        navController.navigate(Screen.Map.route)
                    },
                    participantName = participantName
                )
            }

            // Tela Mapa
            composable(Screen.Map.route) {
                MapScreen(
                    onSalonClick = { salonId ->
                        navController.navigate(Screen.Detail.createRoute(salonId))
                    }
                )
            }

            // Tela 4: Pedir Agora (On-Demand)
            composable(Screen.OnDemand.route) {
                OnDemandScreen(
                    onBackClick = { navController.popBackStack() },
                    onProceedToCheckout = { sId, servId, time ->
                        navController.navigate(Screen.Checkout.createRoute(sId, servId, time, "salon"))
                    }
                )
            }

            // Tela 6A: Meus Agendamentos (Agenda)
            composable(Screen.Appointments.route) {
                AppointmentsScreen(
                    onOpenVoucher = { appointmentId ->
                        navController.navigate(Screen.Confirm.createRoute(appointmentId))
                    },
                    onBookNowClick = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            // Tela 6B: Ajustes / Perfil & Avaliação SUS
            composable(Screen.Profile.route) {
                ProfileScreen(
                    participantCode = participantCode,
                    participantName = participantName.ifBlank { "Carolina Almeida" },
                    onResetToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onOpenPartnerB2B = {
                        navController.navigate(Screen.PartnerB2B.route)
                    }
                )
            }

            // Tela B2B: Painel do Salão Parceiro & Validação H3
            composable(Screen.PartnerB2B.route) {
                PartnerB2BScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Tela 3: Detalhe do Salão & Seletor Radial
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("salonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val salonId = backStackEntry.arguments?.getString("salonId") ?: "s1"
                SalonDetailScreen(
                    salonId = salonId,
                    onBackClick = { navController.popBackStack() },
                    onProceedToCheckout = { sId, servId, time, fulfillmentMode ->
                        navController.navigate(Screen.Checkout.createRoute(sId, servId, time, fulfillmentMode))
                    }
                )
            }

            // Tela 5A: Checkout Seguro
            composable(
                route = Screen.Checkout.route,
                arguments = listOf(
                    navArgument("salonId") { type = NavType.StringType },
                    navArgument("serviceId") { type = NavType.StringType },
                    navArgument("slotTime") { type = NavType.StringType },
                    navArgument("fulfillmentMode") {
                        type = NavType.StringType
                        defaultValue = "salon"
                    }
                )
            ) { backStackEntry ->
                val salonId = backStackEntry.arguments?.getString("salonId") ?: "s1"
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "srv_s1_1"
                val slotTime = backStackEntry.arguments?.getString("slotTime") ?: "14:00"
                val fulfillmentMode = backStackEntry.arguments?.getString("fulfillmentMode") ?: "salon"
                CheckoutScreen(
                    salonId = salonId,
                    serviceId = serviceId,
                    slotTime = slotTime,
                    fulfillmentMode = fulfillmentMode,
                    onBackClick = { navController.popBackStack() },
                    onBookingConfirmed = { appointmentId ->
                        navController.navigate(Screen.Confirm.createRoute(appointmentId)) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // Tela 5B: Confirmação & Voucher Digital
            composable(
                route = Screen.Confirm.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                ConfirmScreen(
                    appointmentId = appointmentId,
                    onGoToAppointments = {
                        navController.navigate(Screen.Appointments.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onGoToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomNavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .bounceClick(scaleDown = 0.93f, onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) DeepTeal else SlateGray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isSelected) DeepTeal else SlateGray
        )
    }
}
