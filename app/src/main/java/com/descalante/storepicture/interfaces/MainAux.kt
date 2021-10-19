package com.descalante.storepicture.interfaces

import com.descalante.storepicture.entity.Store

interface MainAux {
    fun hideFab(aux:Boolean = false)
    fun addStore(store: Store)
    fun updateStore(store: Store)
}