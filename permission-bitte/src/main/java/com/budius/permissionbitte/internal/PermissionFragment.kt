package com.budius.permissionbitte.internal

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

internal class PermissionFragment : Fragment() {

    private val viewModel: PermissionViewModel by viewModels {
        PermissionViewModel.Factory.instance
    }

    private val contract = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.onResult(it, ::shouldShowRequestPermissionRationale)
        parentFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.shouldRequest {
            contract.launch(requireArguments().getStringArray(KEY_PERMISSIONS)!!)
        }
    }

    companion object {

        private const val KEY_PERMISSIONS = "permissions"

        const val TAG = "tag-permission-fragment"

        fun create(permissions: Array<String>): Fragment {
            val bundle = Bundle()
            bundle.putStringArray(KEY_PERMISSIONS, permissions)
            val frag = PermissionFragment()
            frag.arguments = bundle
            return frag
        }
    }
}
