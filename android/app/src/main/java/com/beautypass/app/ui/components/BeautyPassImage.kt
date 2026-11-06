package com.beautypass.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.beautypass.app.R

/**
 * BeautyPassImage: Carrega imagens remotas do Unsplash quando online (via Coil AsyncImage)
 * e utiliza automaticamente as fotos locais de alta definição do projeto (res/drawable-nodpi)
 * como fallback instantâneo de carregamento, erro offline ou Android Studio @Preview.
 */
object LocalImageFallbacks {

    @DrawableRes
    fun forStoryCategory(categoryId: String): Int = when (categoryId.lowercase()) {
        "all", "todos" -> R.drawable.salon_hair_boutique
        "hair", "cabelo" -> R.drawable.treatment_hair_salon
        "barber", "barba", "barbearia" -> R.drawable.salon_barber_modern
        "nails", "unhas" -> R.drawable.salon_nail_lounge
        "massage", "massagem", "spa" -> R.drawable.treatment_massage_spa
        "esthetic", "estetica", "facial" -> R.drawable.treatment_facial_spa
        else -> R.drawable.salon_hair_boutique
    }

    @DrawableRes
    fun forSalon(category: String, salonId: String = "", galleryIndex: Int = 0): Int {
        val cat = category.lowercase()
        val idHash = (salonId.hashCode().let { if (it < 0) -it else it } + galleryIndex)
        return when (cat) {
            "hair", "cabelo" -> when (idHash % 3) {
                0 -> R.drawable.salon_hair_boutique
                1 -> R.drawable.treatment_hair_salon
                else -> R.drawable.treatment_eyebrows_beauty
            }
            "nails", "unhas" -> when (idHash % 2) {
                0 -> R.drawable.salon_nail_lounge
                else -> R.drawable.treatment_eyebrows_beauty
            }
            "barber", "barba", "barbearia" -> R.drawable.salon_barber_modern
            "massage", "massagem", "spa" -> when (idHash % 2) {
                0 -> R.drawable.treatment_massage_spa
                else -> R.drawable.salon_spa_oasis
            }
            "facial", "esthetic", "estetica" -> when (idHash % 3) {
                0 -> R.drawable.salon_clinic_aesthetic
                1 -> R.drawable.treatment_facial_spa
                else -> R.drawable.treatment_skincare_cosmetics
            }
            else -> R.drawable.salon_hair_boutique
        }
    }

    @DrawableRes
    fun forOwnerAvatar(name: String = ""): Int {
        val lower = name.lowercase()
        return when {
            lower.contains("marcos") || lower.contains("rodrigo") || lower.contains("gabriel") || lower.contains("ricardo") || lower.contains("lucas") ->
                R.drawable.profile_owner_marcos
            lower.contains("renata") || lower.contains("fernanda") || lower.contains("carla") ->
                R.drawable.profile_owner_renata
            else -> R.drawable.profile_owner_juliana
        }
    }

    @DrawableRes
    fun forFriendAvatar(index: Int): Int = when (index % 3) {
        0 -> R.drawable.friend_camila
        1 -> R.drawable.friend_beatriz
        else -> R.drawable.friend_larissa
    }

    @DrawableRes
    fun forUrl(url: String): Int {
        val u = url.lowercase()
        return when {
            u.contains("1503951914875") || u.contains("1585747860715") || u.contains("barber") -> R.drawable.salon_barber_modern
            u.contains("1633681926022") || u.contains("1604654894610") || u.contains("nail") -> R.drawable.salon_nail_lounge
            u.contains("1540555700478") || u.contains("1600334129128") || u.contains("spa") -> R.drawable.salon_spa_oasis
            u.contains("1570172619644") || u.contains("1512290900672") -> R.drawable.salon_clinic_aesthetic
            u.contains("1522337360788") -> R.drawable.treatment_hair_salon
            u.contains("1527799820374") -> R.drawable.treatment_eyebrows_beauty
            u.contains("1534528741775") || u.contains("1580489944761") -> R.drawable.profile_owner_juliana
            u.contains("1507003211169") || u.contains("1500648767791") -> R.drawable.profile_owner_marcos
            u.contains("1573496359142") -> R.drawable.profile_owner_renata
            else -> {
                val hash = u.hashCode().let { if (it < 0) -it else it }
                val pool = listOf(
                    R.drawable.salon_hair_boutique,
                    R.drawable.treatment_hair_salon,
                    R.drawable.salon_nail_lounge,
                    R.drawable.salon_spa_oasis,
                    R.drawable.salon_clinic_aesthetic,
                    R.drawable.salon_barber_modern
                )
                pool[hash % pool.size]
            }
        }
    }
}

@Composable
fun BeautyPassImage(
    imageUrl: String,
    @DrawableRes fallbackResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val isPreview = LocalInspectionMode.current
    val fallbackPainter = painterResource(id = fallbackResId)

    if (isPreview || imageUrl.isBlank() || !imageUrl.startsWith("http")) {
        Image(
            painter = fallbackPainter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            placeholder = fallbackPainter,
            error = fallbackPainter,
            fallback = fallbackPainter,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

@Composable
fun BeautyPassImage(
    model: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    BeautyPassImage(
        imageUrl = model,
        fallbackResId = LocalImageFallbacks.forUrl(model),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}
