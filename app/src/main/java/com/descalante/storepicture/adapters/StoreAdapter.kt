package com.descalante.storepicture.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.descalante.storepicture.R
import com.descalante.storepicture.entity.Store
import com.descalante.storepicture.databinding.ItemStoreBinding
import com.descalante.storepicture.interfaces.OnClickListener

class StoreAdapter(private var stores: MutableList<Store>, private var listener:OnClickListener) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mBinding = ItemStoreBinding.bind(view)
        fun setListener(store:Store){
            with(mBinding.root){
                setOnClickListener{ listener.onClick(store.id) }
                setOnLongClickListener {
                    listener.onDeleteStore(store)
                    true
                }
            }

            mBinding.cbFavorite.setOnClickListener{ listener.onFavoriteStore(store) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)
        with(holder){
            setListener(store)
            mBinding.tvName.text = store.name
            mBinding.cbFavorite.isChecked = store.isFavorite
            Glide.with(mContext)
                .load(store.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.imgPhoto)
        }
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(store: Store) {
        if(!stores.contains(store)){
            stores.add(store)
            notifyItemInserted(stores.size-1) // avisamos a adaptador que refleje la pantalla para que se vea la tienda
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setStores(stores: MutableList<Store>) {
        this.stores = stores
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(store: Store) {
        val index = stores.indexOf(store)
        if (index != -1){
            stores.set(index, store)
            notifyItemChanged(index)
        }
    }

    fun delete(store: Store) {
        val index = stores.indexOf(store)
        if (index != -1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun findById(store: Store) {
        val index = stores.indexOf(store)
        if (index != -1){
            stores.get(index)
        }
    }
}