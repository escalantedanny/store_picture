package com.descalante.storepicture.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoreEntity")
data class Store(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                var name:String = "",
                var phone:String = "",
                var website:String = "",
                var isFavorite:Boolean = false,
                var photoUrl:String,
                var date:String = "",
                var createBy:String = ""
        ){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Store

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
