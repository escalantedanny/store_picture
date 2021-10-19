package com.descalante.storepicture.interfaces

import com.descalante.storepicture.entity.Store

interface OnClickListener {
    fun onClick(storeId: Long)
    fun setupRecyclerView()
    fun onFavoriteStore(store: Store)
    fun onDeleteStore(store: Store)
}