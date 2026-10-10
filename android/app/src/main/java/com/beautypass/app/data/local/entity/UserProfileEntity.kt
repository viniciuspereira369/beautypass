package com.beautypass.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para persistência do perfil e termo LGPD do usuário.
 * Tabela: user_profile
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "participant_id")
    val participantId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "lgpd_accepted")
    val lgpdAccepted: Boolean,

    @ColumnInfo(name = "accepted_at_timestamp")
    val acceptedAtTimestamp: Long
)
