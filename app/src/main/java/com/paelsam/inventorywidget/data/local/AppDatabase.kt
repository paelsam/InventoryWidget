package com.paelsam.inventorywidget.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.paelsam.inventorywidget.data.model.Product

/**
 * Base de datos principal de la aplicación usando Room
 * Singleton pattern para garantizar una sola instancia
 */
@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        private const val DATABASE_NAME = "inventory_database"
        
        /**
         * Obtiene la instancia única de la base de datos
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // En desarrollo, recrea la DB si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
