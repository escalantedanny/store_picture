package com.descalante.storepicture

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.UiThread
import androidx.recyclerview.widget.GridLayoutManager
import com.descalante.storepicture.adapters.StoreAdapter
import com.descalante.storepicture.entity.Store
import com.descalante.storepicture.databinding.ActivityMainBinding
import com.descalante.storepicture.interfaces.OnClickListener
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding : ActivityMainBinding
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private lateinit var mAdapter : StoreAdapter
    private lateinit var mGridLayout : GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSave.setOnClickListener{
            val currentDate = sdf.format(Date())
            var store = Store(name = mBinding.etName.text.toString().trim(), date = currentDate)

            //insert database
            Thread {
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
            mBinding.etName.text = null
        }

        setupRecyclerView()
    }

    /**
     * OnClickListener
     */
    override fun onClick(store: Store) {
        TODO("Not yet implemented")
    }

    override fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStore()
        mBinding.recyclerView.apply {
            setHasFixedSize(true) // no cambiara de tamaño y puede optimizar los recursos
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStore(){
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    override fun onFavoriteStore(store: Store) {
        store.isFavorite = !store.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(store)
            uiThread {
                mAdapter.update(store)
            }
        }
    }

    override fun onDeleteStore(store: Store) {
        doAsync {
            StoreApplication.database.storeDao().deleteStore(store)
            uiThread {
                mAdapter.delete(store)
            }
        }
    }
}