package com.descalante.storepicture

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.descalante.storepicture.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding

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


        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)

    }

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
                Snackbar.make(mBinding.root, getString(R.string.message_add_store), Snackbar.LENGTH_LONG).show()
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        setHasOptionsMenu(false)
        super.onDestroy()
    }

}