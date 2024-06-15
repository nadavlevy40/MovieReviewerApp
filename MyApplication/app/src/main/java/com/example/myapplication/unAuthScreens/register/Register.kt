package com.example.myapplication.unAuthScreens.register

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.utils.BasicAlert
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File

class Register : Fragment() {

    companion object {
        fun newInstance() = Register()
    }

    private val viewModel: RegisterViewModel by viewModels()
    lateinit var loginLink: View
    lateinit var registerButton: Button
    lateinit var imageView: ImageView
    lateinit var progressBar: ProgressBar
    private val REQUEST_STORAGE_PERMISSION = 1
    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        bindViews(binding)

        setupUploadButton(binding)
        setupLoginLink(binding)
        setupRegisterButton(binding)
        requestStoragePermission()

        return binding.root
    }

    private fun bindViews(binding: FragmentRegisterBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupUploadButton(binding: FragmentRegisterBinding) {
        imageView = binding.root.findViewById(R.id.image_view)
        imageView.setOnClickListener {
            imagePicker.launch("image/*")
        }
        addOnIsImageUriValidChangedCallback()
    }

    private fun addOnIsImageUriValidChangedCallback() {
        viewModel.isImageUriValid.addOnPropertyChangedCallback(object :
            OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (!viewModel.isImageUriValid.get()!!) {
                    BasicAlert("Invalid Input", "Please upload an image", requireContext()).show()
                }
            }
        })
    }

    private fun setupLoginLink(binding: FragmentRegisterBinding) {
        loginLink = binding.root.findViewById(R.id.login_link)
        loginLink.setOnClickListener {
            findNavController().navigate(R.id.register_to_login)
        }
    }

    private fun setupRegisterButton(binding: FragmentRegisterBinding) {
        registerButton = binding.root.findViewById(R.id.register_button)
        progressBar = binding.root.findViewById(R.id.progress_bar)
        registerButton.setOnClickListener {
            showProgressBar()
            viewModel.register({ onRegisterSuccess() }, { error -> onRegisterFailure(error) })
        }
    }

    private fun onRegisterSuccess() {
        BasicAlert("Success", "You have successfully registered.", requireContext()).show()
    }

    private fun onRegisterFailure(error: Exception?) {
        if (error == null) {
            registerButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            return
        }

        Log.e("Register", "Error Registering", error)
        handleRegisterError(error)
        showRegisterButton()
    }

    private fun handleRegisterError(error: Exception) {
        when (error) {
            is FirebaseAuthUserCollisionException -> {
                BasicAlert(
                    "Register Error",
                    "User with this email already exists",
                    requireContext()
                ).show()
            }

            else -> {
                BasicAlert("Register Error", "An error occurred", requireContext()).show()
            }
        }
    }

    private fun showRegisterButton() {
        registerButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        registerButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_STORAGE_PERMISSION
            )
        }
    }

    private fun getImagePicker() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped"))
                val options = UCrop.Options()
                options.setCompressionQuality(80)
                options.setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f))

                val cropIntent = UCrop.of(uri, destinationUri)
                    .withOptions(options)
                    .getIntent(requireContext())
                uCropLauncher.launch(cropIntent)
            }
        }

    private fun getUCropLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = UCrop.getOutput(result.data!!)
                imageView.setImageURI(uri)
                viewModel.imageUri.set(uri.toString())
            }
        }
}