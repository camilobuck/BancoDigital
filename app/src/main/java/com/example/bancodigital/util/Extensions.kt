package com.example.bancodigital.util

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.fragment.app.Fragment
import com.example.bancodigital.R
import com.example.bancodigital.databinding.LayoutBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.initToolbar(
    toolbar: Toolbar,
    homesAsUpEnable: Boolean = true,
    light: Boolean = false
) {

    val iconBack = if (light) R.drawable.ic_back_white else R.drawable.ic_back
    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).title = ""
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(homesAsUpEnable)
    (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(iconBack)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
}

fun Fragment.showBottomSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    message: String?,
    onClick: () -> Unit = {}
) {
    val bottomSheetDialog = BottomSheetDialog(requireContext())
    val bottomSheetBinding: LayoutBottomSheetBinding =
        LayoutBottomSheetBinding.inflate(layoutInflater, null, false)

    bottomSheetBinding.textTitle.text = getString(titleDialog ?: R.string.text_title_bottom_sheet)
    bottomSheetBinding.textMessage.text = message ?: getText(R.string.error_generic)
    bottomSheetBinding.btnOk.text = getString(titleDialog ?: R.string.text_button_bottom_sheet)
    bottomSheetBinding.btnOk.setOnClickListener {
        bottomSheetDialog.dismiss()
        onClick()
    }

    bottomSheetDialog.setContentView(bottomSheetBinding.root)
    bottomSheetDialog.show()

}