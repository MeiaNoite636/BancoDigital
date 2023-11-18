package com.example.bancodigital.presenter.profile

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
import com.example.bancodigital.databinding.LayoutBottomSheetBinding
import com.example.bancodigital.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var user: User? = null

    private var imageProfile: String? = null
    private var currentPhotoPath: String? = null

    private val tagPicasso = "tagPicasso"

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

        getProfile()

        initToolbar(binding.toolbar)

        initListener()

    }

    private fun initListener() {
        binding.btnSalvar.setOnClickListener {
            if (user != null) {
                if (imageProfile != null) {//usuario selecionou uma imagem
                    saveImageProfile()
                } else { // usuario nao selecionou uma imagem
                    validateData()
                }
            }

        }

        binding.imgUser.setOnClickListener {
            showBottomSheetImage()
        }
    }

    private fun showBottomSheetImage() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
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

    private fun saveImageProfile() {
        imageProfile?.let { image ->
            viewModel.saveImageProfile(image).observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is StateView.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is StateView.Sucess -> {
                        saveProfile(stateView.data)
                    }
                    is StateView.Error -> {
                        binding.progressBar.isVisible = false
                        showBottomSheet(message = stateView.message)
                    }
                }
            }
        }
    }

    private fun checkPermissionCamera() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(requireContext(), "Permissão Negada", Toast.LENGTH_SHORT).show()
            }
        }

        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = android.Manifest.permission.CAMERA,
            message = R.string.text_message_camera_denied_profile_fragment
        )
    }

    private fun checkPermissionGallery() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openGallery()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(requireContext(), "Permissão Negada", Toast.LENGTH_SHORT).show()
            }
        }

        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = android.Manifest.permission.READ_EXTERNAL_STORAGE,
            message = R.string.text_message_gallery_denied_profile_fragment
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
                "Não foi possível abrir a câmera do dispositivo",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.bancodigital.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath!!)
            binding.imgUser.setImageURI(Uri.fromFile(file))

            imageProfile = file.toURI().toString()
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale("pt", "BR")).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        currentPhotoPath = image.absolutePath
        return image
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
                binding.imgUser.setImageBitmap(getBitmap(imageSelected))
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
            .setDeniedTitle("Permissão negada")
            .setDeniedMessage(message)
            .setDeniedCloseButtonText("Não")
            .setGotoSettingButtonText("Sim")
            .setPermissions(permission)
            .check()
    }

    private fun validateData() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isNotEmpty()) {
            if (phone.isNotEmpty()) {

                hideKeyboard()

                user?.name = name
                user?.phone = phone

                saveProfile()

            } else {
                showBottomSheet(message = getString(R.string.text_phone_empty))
            }
        } else {
            showBottomSheet(message = getString(R.string.text_name_empty))
        }
    }

    private fun getProfile() {
        viewModel.getProfile().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    stateView.data.let {
                        user = it
                    }
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

    private fun saveProfile(urlImage: String? = null) {
        user?.let {
            if (urlImage != null) {
                it.image = urlImage
            }

            viewModel.saveProfile(it).observe(viewLifecycleOwner) { stateView ->
                when (stateView) {
                    is StateView.Loading -> {
                        binding.progressBar.isVisible = true
                    }

                    is StateView.Sucess -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.text_message_save_sucess_profile_fragment),
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

        if(user?.image?.isNotEmpty() == true){
            Picasso.get()
                .load(user?.image)
                .tag(tagPicasso)
                .fit().centerCrop()
                .into(binding.imgUser, object : Callback {
                    override fun onSuccess() {
                        binding.progressImage.isVisible = false
                        binding.imgUser.isVisible = true
                    }

                    override fun onError(e:Exception) {
                        TODO("Not yet implemented")
                    }

                })
        }else{
            binding.progressImage.isVisible = false
            binding.imgUser.isVisible = true
            binding.imgUser.setImageResource(R.drawable.ic_user_place_holder)
        }

        binding.etName.setText(user?.name)
        binding.etPhone.setText(user?.phone)
        binding.etEmail.setText(user?.email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Picasso.get().cancelTag(tagPicasso)
        _binding = null
    }

}