package com.descalante.storepicture

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.recyclerview.widget.GridLayoutManager
import com.descalante.storepicture.adapters.StoreAdapter
import com.descalante.storepicture.entity.Store
import com.descalante.storepicture.databinding.ActivityMainBinding
import com.descalante.storepicture.interfaces.MainAux
import com.descalante.storepicture.interfaces.OnClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding : ActivityMainBinding
    private lateinit var mAdapter : StoreAdapter
    private lateinit var mGridLayout : GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.fab.setOnClickListener { launchFragmentEdit() }

        setupRecyclerView()
    }

    /**
     * OnClickListener
     */
    override fun onClick(storeId: Long) {
        val arg = Bundle()
        arg.putLong(getString(R.string.arg_id), storeId)
        launchFragmentEdit(arg)
    }

    fun launchFragmentEdit(args: Bundle? = null) {
        val fragmentEdit = EditStoreFragment()
        if(args!=null){
            fragmentEdit.arguments = args
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragmentEdit)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
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
                updateStore(store)
            }
        }
    }

    override fun onDeleteStore(store: Store) {
        val items = arrayOf("Delete", "Call", "Website")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options)
            .setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    0 -> {
                        confirmDelete(store)
                    } 1 -> {
                        dial(store.phone)
                    } else -> {
                    Toast.makeText(this, "go to the website...", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            .show()


    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startActivity(callIntent)
    }

    private fun confirmDelete(store: Store){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.button_delete, DialogInterface.OnClickListener { dialogInterface, i ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(store)
                    uiThread {
                        mAdapter.delete(store)
                    }
                }
            })
            .setNegativeButton(R.string.button_cancel, null)
            .show()
    }

    /**
     * Interface MainAux
     */
    override fun hideFab(aux: Boolean) {
        if(aux) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(store: Store) {
        mAdapter.add(store)
    }

    override fun updateStore(store: Store) {
        mAdapter.update(store)
    }
}