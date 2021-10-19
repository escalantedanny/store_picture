package com.descalante.storepicture.interfaces

import com.descalante.storepicture.entity.Store

interface OnClickListener {
    fun onClick(store: Store)
    fun setupRecyclerView()
    fun onFavoriteStore(store: Store)
    fun onDeleteStore(store: Store)
}