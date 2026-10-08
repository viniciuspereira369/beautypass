package com.beautypass.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.beautypass.app.theme.SereneTeal
import com.beautypass.app.theme.SurfaceWhite
import com.beautypass.app.ui.screens.appointments.AppointmentsScreen
import com.beautypass.app.ui.screens.checkout.CheckoutScreen
import com.beautypass.app.ui.screens.confirm.ConfirmScreen
import com.beautypass.app.ui.screens.detail.SalonDetailScreen
import com.beautypass.app.ui.screens.home.HomeScreen
import com.beautypass.app.ui.screens.map.MapScreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Início", Icons.Default.Home)
    object Map : Screen("map", "Mapa", Icons.Default.LocationOn)
    object OnDemand : Screen("on_demand", "Pedir Agora", Icons.Default.Spa)
    object Appointments : Screen("appointments", "Reservas", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    object Detail : Screen("detail/{salonId}", "Detalhes") {
        fun createRoute(salonId: String) = "detail/$salonId"
    }
    object Checkout : Screen("checkout/{salonId}/{serviceId}/{slotTime}", "Checkout") {
        fun createRoute(salonId: String, serviceId: String, slotTime: String) = "checkout/$salonId/$serviceId/$slotTime"
    }
    object Confirm : Screen("confirm/{appointmentId}", "Confirmação") {
        fun createRoute(appointmentId: String) = "confirm/$appointmentId"
    }
}

@Composable
fun BeautyPassApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = listOf(
        Screen.Home,
        Screen.Map,
        Screen.Appointments
    )

    val showBottomBar = currentRoute in listOf(Screen.Home.route, Screen.Map.route, Screen.Appointments.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = SurfaceWhite) {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
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
                                indicatorColor = SurfaceWhite
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onSalonClick = { salonId ->
                        navController.navigate(Screen.Detail.createRoute(salonId))
                    }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(
                    onSalonClick = { salonId ->
                        navController.navigate(Screen.Detail.createRoute(salonId))
                    }
                )
            }
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
