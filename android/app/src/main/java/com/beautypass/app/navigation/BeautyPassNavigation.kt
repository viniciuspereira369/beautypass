package com.beautypass.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beautypass.app.theme.*
import com.beautypass.app.ui.screens.appointments.AppointmentsScreen
import com.beautypass.app.ui.screens.checkout.CheckoutScreen
import com.beautypass.app.ui.screens.confirm.ConfirmScreen
import com.beautypass.app.ui.screens.detail.SalonDetailScreen
import com.beautypass.app.ui.screens.home.HomeScreen
import com.beautypass.app.ui.screens.map.MapScreen
import com.beautypass.app.ui.screens.onboarding.OnboardingScreen
import com.beautypass.app.ui.screens.ondemand.OnDemandScreen
import com.beautypass.app.ui.screens.profile.ProfileScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Boas-vindas")
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Map : Screen("map", "Mapa", Icons.Default.LocationOn)
    object OnDemand : Screen("on_demand", "Pedir Agora", Icons.Default.Bolt)
    object Appointments : Screen("appointments", "Reservas", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    object Detail : Screen("detail/{salonId}", "Detalhes") {
        fun createRoute(salonId: String) = "detail/$salonId"
    }

    object Checkout : Screen("checkout/{salonId}/{serviceId}/{slotTime}", "Checkout") {
        fun createRoute(salonId: String, serviceId: String, slotTime: String) =
            "checkout/$salonId/$serviceId/$slotTime"
    }

    object Confirm : Screen("confirm/{appointmentId}", "Confirmação") {
        fun createRoute(appointmentId: String) = "confirm/$appointmentId"
    }
}

/**
 * BeautyPassApp: Orquestrador Geral de Navegação Nativa em Jetpack Compose (MVI / UDF).
 */
@Composable
fun BeautyPassApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var participantCode by remember { mutableStateOf("P01") }
    var participantName by remember { mutableStateOf("Participante do Teste") }

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.Map,
        Screen.OnDemand,
        Screen.Appointments,
        Screen.Profile
    )

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
                NavigationBar(
                    containerColor = SurfaceWhite,
                    tonalElevation = 8.dp
                ) {
                    bottomNavScreens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon!!,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SereneTeal,
                                selectedTextColor = SereneTeal,
                                unselectedIconColor = NeutralMuted,
                                unselectedTextColor = NeutralMuted,
                                indicatorColor = MintSurface
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(padding)
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
                    }
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
                        navController.navigate(Screen.Checkout.createRoute(sId, servId, time))
                    }
                )
            }

            // Tela 6A: Meus Agendamentos
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

            // Tela 6B: Perfil & Avaliação SUS
            composable(Screen.Profile.route) {
                ProfileScreen(
                    participantCode = participantCode,
                    participantName = participantName,
                    onResetToOnboarding = {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
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
                    onProceedToCheckout = { sId, servId, time ->
                        navController.navigate(Screen.Checkout.createRoute(sId, servId, time))
                    }
                )
            }

            // Tela 5A: Checkout Seguro
            composable(
                route = Screen.Checkout.route,
                arguments = listOf(
                    navArgument("salonId") { type = NavType.StringType },
                    navArgument("serviceId") { type = NavType.StringType },
                    navArgument("slotTime") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val salonId = backStackEntry.arguments?.getString("salonId") ?: "s1"
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "srv_s1_1"
                val slotTime = backStackEntry.arguments?.getString("slotTime") ?: "14:00"
                CheckoutScreen(
                    salonId = salonId,
                    serviceId = serviceId,
                    slotTime = slotTime,
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
