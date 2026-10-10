package com.beautypass.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.beautypass.app.data.local.dao.AppointmentDao
import com.beautypass.app.data.local.dao.FavoriteDao
import com.beautypass.app.data.local.dao.SusEvaluationDao
import com.beautypass.app.data.local.dao.UserProfileDao
import com.beautypass.app.data.local.entity.AppointmentEntity
import com.beautypass.app.data.local.entity.FavoriteEntity
import com.beautypass.app.data.local.entity.SusEvaluationEntity
import com.beautypass.app.data.local.entity.UserProfileEntity

/**
 * Banco de dados Room oficial do aplicativo BeautyPass Android.
 * Persiste entidades mutáveis do usuário no SQLite mantendo o catálogo mestre determinístico.
 */
@Database(
    entities = [
        AppointmentEntity::class,
        FavoriteEntity::class,
        UserProfileEntity::class,
        SusEvaluationEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BeautyPassDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun susEvaluationDao(): SusEvaluationDao

    companion object {
        private const val DATABASE_NAME = "beautypass_database.db"

        @Volatile
        private var INSTANCE: BeautyPassDatabase? = null

        /**
         * Obtém o singleton do banco de dados Room.
         */
        fun getDatabase(context: Context): BeautyPassDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BeautyPassDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Cria uma instância de banco in-memory para testes unitários ou instrumentados.
         */
        fun createInMemoryDatabase(context: Context): BeautyPassDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                BeautyPassDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }

        /**
         * Permite injetar uma instância pré-configurada em ambientes de teste.
         */
        fun setInstanceForTesting(db: BeautyPassDatabase?) {
            synchronized(this) {
                INSTANCE = db
            }
        }
    }
}
