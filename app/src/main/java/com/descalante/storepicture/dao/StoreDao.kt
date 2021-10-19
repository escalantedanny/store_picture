package com.descalante.storepicture.dao

import androidx.room.*
import com.descalante.storepicture.entity.Store

@Dao
interface StoreDao {

    @Query("SELECT * FROM StoreEntity")
    fun getAllStores() : MutableList<Store>

    @Insert
    fun addStore(storeEntity: Store)

    @Update
    fun updateStore(storeEntity: Store)

    @Delete
    fun deleteStore(storeEntity: Store)

    @Query("SELECT * FROM StoreEntity WHERE name LIKE '%'|| :query ||'%'")
    fun searchStore(query: String) : MutableList<Store>

    @Query("SELECT * FROM StoreEntity WHERE id = :id")
    fun findById(id: Long) : Store

}