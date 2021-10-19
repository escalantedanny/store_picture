package com.descalante.storepicture

import android.app.Application
import androidx.room.Room
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
        database = Room.databaseBuilder(this, StoreDataBase::class.java, "StoreDatabase").build()
    }
}