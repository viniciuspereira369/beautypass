package com.beautypass.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beautypass.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para perfil de participante e termo LGPD no SQLite.
 */
@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}
