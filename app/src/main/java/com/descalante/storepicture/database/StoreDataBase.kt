package com.descalante.storepicture.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.descalante.storepicture.dao.StoreDao
import com.descalante.storepicture.entity.Store

@Database(entities = arrayOf(Store::class), version = 2)
abstract class StoreDataBase : RoomDatabase(){

    abstract fun storeDao() : StoreDao

}