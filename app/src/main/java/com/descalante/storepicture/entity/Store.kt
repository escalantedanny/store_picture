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

}
