package com.beautypass.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beautypass.app.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para operações de favoritos no SQLite.
 */
@Dao
interface FavoriteDao {

    @Query("SELECT salon_id FROM favorites ORDER BY added_at_timestamp DESC")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT * FROM favorites ORDER BY added_at_timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE salon_id = :salonId")
    suspend fun deleteFavorite(salonId: String)

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE salon_id = :salonId")
    suspend fun isFavorite(salonId: String): Boolean

    @Query("DELETE FROM favorites")
    suspend fun deleteAllFavorites()
}
