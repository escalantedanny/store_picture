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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    @SuppressLint("SimpleDateFormat")
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())
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
            mIsEditMode = false
            mStore = Store(name = "", phone = "", website = "", photoUrl = "", date = currentDate, createBy = "Danny.ezequiel@gmail.com")
        }

        setupActionBar()

        setupTextFields()

    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_title_edit) else getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(mBinding.etPhotoUrl.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.imgPhoto)
        }

        mBinding.etName.addTextChangedListener{ validateFields(mBinding.tilName) }
        mBinding.etPhone.addTextChangedListener{ validateFields(mBinding.tilPhone) }
        mBinding.etWebsite.addTextChangedListener{ validateFields(mBinding.tilWebsite) }
        mBinding.etPhotoUrl.addTextChangedListener{ validateFields(mBinding.tilPhoto) }
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
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            } R.id.action_save -> {

                if (mStore != null && validateFields(mBinding.tilName, mBinding.tilPhone, mBinding.tilWebsite, mBinding.tilPhoto) ){
                    with(mStore!!){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        date = currentDate
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                        createBy = "danny.ezequiel@gmail.com"
                    }
                    doAsync {
                        if(mIsEditMode) {
                            StoreApplication.database.storeDao().updateStore(mStore!!)
                        } else {
                            mStore!!.id = StoreApplication.database.storeDao().addStore(mStore!!)
                        }

                        uiThread {
                            hideKeyboard()
                            if (mIsEditMode){
                                mActivity?.updateStore(mStore!!)
                                Snackbar.make(mBinding.root, R.string.message_update_store, Snackbar.LENGTH_SHORT).show()
                            }else{
                                mActivity?.addStore(mStore!!)
                            }
                            Toast.makeText(mActivity, R.string.message_add_store, Toast.LENGTH_SHORT).show()
                            mActivity?.onBackPressed()
                        }
                    }
                }
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else textField.error = null
        }
        if (!isValid) Snackbar.make(mBinding.root, getString(R.string.edit_store_message_valid), Snackbar.LENGTH_LONG).show()
        return isValid
    }

    private fun validateFields() : Boolean {
        var isValid = true

        if(mBinding.etWebsite.text.toString().isEmpty() || mBinding.etPhone.text.toString().isEmpty() || mBinding.etName.text.toString().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.tilName.requestFocus()
            isValid = false
        } else if (mBinding.etPhotoUrl.text.toString().isEmpty()){
            mBinding.etPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        } else if (mBinding.etWebsite.text.toString().isEmpty()){
            mBinding.tilWebsite.error = getString(R.string.helper_required)
            mBinding.tilWebsite.requestFocus()
            isValid = false
        } else if (mBinding.etPhone.text.toString().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.tilPhone.requestFocus()
            isValid = false
        } else if (mBinding.etName.text.toString().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.tilName.requestFocus()
            isValid = false
        }
        
        if (!isValid) Snackbar.make(mBinding.root, getString(R.string.edit_store_message_valid), Snackbar.LENGTH_LONG).show()
        return isValid
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