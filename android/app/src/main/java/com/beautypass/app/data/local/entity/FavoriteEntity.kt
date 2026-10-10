package com.beautypass.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para persistência de salões favoritados no SQLite.
 * Tabela: favorites
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "salon_id")
    val salonId: String,

    @ColumnInfo(name = "added_at_timestamp")
    val addedAtTimestamp: Long = System.currentTimeMillis()
)
