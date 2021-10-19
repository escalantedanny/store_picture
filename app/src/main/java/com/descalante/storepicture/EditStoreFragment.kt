package com.descalante.storepicture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.descalante.storepicture.adapters.StoreAdapter
import com.descalante.storepicture.databinding.FragmentEditStoreBinding
import com.descalante.storepicture.entity.Store
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    private var mIsEditMode :Boolean = false
    private var mStore: Store? = null

    private var mActivity: MainActivity? = null

    /**
     * vicula la vista
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    /**
     * cuando se a creado la vista por completo
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id!=null && id != 0L){
            mIsEditMode = true
            getStore(id)
        } else {
            Toast.makeText(activity, id?.toString(), Toast.LENGTH_SHORT).show()
        }

        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }

    }

    private fun getStore(id: Long) {
        doAsync {
            mStore = StoreApplication.database.storeDao().findById(id)
            uiThread{
                if(mStore!=null){
                    setUiStore(mStore!!)
                }
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setUiStore(mStore: Store) {
        with(mBinding){

            /* otras maneras de asignar la variable al objeto
                    etName.setText(mStore.name)
                    etPhone.text = Editable.Factory.getInstance().newEditable(mStore.phone)
            */
            etName.text = mStore.name.editable()
            etPhone.text = mStore.phone.editable()
            etWebsite.text = mStore.website.editable()
            etPhotoUrl.text = mStore.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentDate = sdf.format(Date())
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            } R.id.action_save -> {
                var store = Store(
                    name = mBinding.etName.text.toString().trim(),
                    phone = mBinding.etPhone.text.toString().trim(),
                    website = mBinding.etWebsite.text.toString().trim(),
                    date = currentDate,
                    photoUrl = mBinding.etPhotoUrl.text.toString().trim(),
                    createBy = "danny.ezequiel@gmail.com")
                doAsync {
                    store.id = StoreApplication.database.storeDao().addStore(store)
                    uiThread {
                        mActivity?.addStore(store)
                        hideKeyboard()
                        Toast.makeText(mActivity, R.string.message_add_store, Toast.LENGTH_SHORT).show()
                        mActivity?.onBackPressed()
                    }
                }
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if( view != null){
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        setHasOptionsMenu(false)
        super.onDestroy()
    }

}