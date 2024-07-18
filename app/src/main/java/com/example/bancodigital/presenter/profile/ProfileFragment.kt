package com.example.bancodigital.presenter.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.bancodigital.R
import com.example.bancodigital.data.model.User
import com.example.bancodigital.databinding.BottomSheetImageBinding
import com.example.bancodigital.databinding.FragmentProfileBinding
import com.example.bancodigital.util.BaseFragment
import com.example.bancodigital.util.FirebaseHelper
import com.example.bancodigital.util.StateView
import com.example.bancodigital.util.initToolbar
import com.example.bancodigital.util.showBottomSheet
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()
    private var user: User? = null

    private var imageProfile: String? = null
    private var currentPhotoPath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        getProfile()
        initListeners()
    }

    private fun initListeners() {
        binding.imageProfile.setOnClickListener { showBottomSheetImage() }
        binding.btnProfile.setOnClickListener { if (user != null) validateDta() }
    }

    private fun showBottomSheetImage() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding: BottomSheetImageBinding =
            BottomSheetImageBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.btnCamera.setOnClickListener {
            checkPermissionCamera()
            bottomSheetDialog.dismiss()
        }

        bottomSheetBinding.btnGallery.setOnClickListener {
            checkPermissionGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()

    }

    private fun checkPermissionCamera() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    requireContext(),
                    "Permissão Negada", Toast.LENGTH_SHORT
                ).show()
            }
        }
        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = Manifest.permission.CAMERA,
            message = R.string.text_camera_denied
        )
    }

    private fun checkPermissionGallery() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openGallery()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    requireContext(),
                    "Permissão Negada", Toast.LENGTH_SHORT
                ).show()
            }
        }
        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = Manifest.permission.READ_EXTERNAL_STORAGE,
            message = R.string.text_gallery_denied
        )
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(
                requireContext(),
                "Não foi possível abrir a câmera do dispositivo.",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (photoFile != null) {
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.bancodigital.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH:mm:ss",
            Locale("pt", "BR")
        ).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir  /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath!!)
            binding.imageProfile.setImageURI(Uri.fromFile(file))
            imageProfile = file.toURI().toString()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageSelected = result.data!!.data
            imageProfile = imageSelected.toString()

            if (imageSelected != null) {
                binding.imageProfile.setImageBitmap(getBitmap(imageSelected))
            }
        }
    }

    private fun getBitmap(pathUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, pathUri)
            } else {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, pathUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun showDialogPermissionDenied(
        permissionlistener: PermissionListener,
        permission: String,
        message: Int
    ) {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedTitle("Permissão Negada")
            .setDeniedMessage(message)
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(permission)
            .check()

    }

    private fun validateDta() {
        val name = binding.editName.text.toString().trim()
        val phone = binding.editPhone.unMaskedText

        if (name.isNotEmpty()) {
            if (phone?.isNotEmpty() == true) {
                if (phone.length == 11) {
                    hideKeyboard()
                    user?.name = name
                    user?.phone = phone
                    saveProfile()

                } else {
                    showBottomSheet(message = getString(R.string.text_phone_invalid))
                }
            } else {
                showBottomSheet(message = getString(R.string.text_phone_empty))
            }
        } else {
            showBottomSheet(message = getString(R.string.text_name_empty))
        }
    }

    private fun getProfile() {
        profileViewModel.getProfile().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    stateView.data?.let { user = it }
                    configData()
                }

                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(
                            FirebaseHelper.validError(
                                stateView.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        user?.let {
            profileViewModel.saveProfile(it).observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is StateView.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is StateView.Sucess -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.text_message_sucess),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is StateView.Error -> {
                        binding.progressBar.isVisible = false
                        showBottomSheet(
                            message = getString(
                                FirebaseHelper.validError(
                                    stateView.message ?: ""
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun configData() {
        binding.editName.setText(user?.name)
        binding.editPhone.setText(user?.phone)
        binding.editEmail.setText(user?.email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}