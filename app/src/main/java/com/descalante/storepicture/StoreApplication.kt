package com.descalante.storepicture

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.descalante.storepicture.database.StoreDataBase

class StoreApplication : Application() {

    /**
     * companion : para acceder desde cualquier parte de nuestra aplicacion
     * object : nos va a configurar el patron singlentoon
     */
    companion object{
        lateinit var database : StoreDataBase
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object:Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
            }
        }
        database = Room.databaseBuilder(this,
            StoreDataBase::class.java, "StoreDatabase")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}