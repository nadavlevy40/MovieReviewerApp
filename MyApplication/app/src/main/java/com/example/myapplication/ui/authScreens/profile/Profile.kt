package com.example.myapplication.ui.authScreens.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.ui.views.ImagePicker
import com.example.myapplication.utils.BasicAlert
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File

class Profile : Fragment() {

    companion object {
        fun newInstance() = Profile()
    }

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var logoutButton: View
    private lateinit var saveChangesButton: Button
    lateinit var imagePickerView: ImagePicker
    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        bindViews(binding)
        setupLogoutButton(binding)
        setupImagePicker(binding)
        imagePickerView.requestStoragePermission(requireContext(), requireActivity())
        saveChangesButton = binding.root.findViewById(R.id.save_changes_button)
        saveChangesButton.setOnClickListener {
            viewModel.saveChanges({
                BasicAlert("Success", "Changes saved successfully", requireContext()).show()
            }, {
                BasicAlert("Fail", "Failed to save changes", requireContext()).show()
            })
        }
        return binding.root
    }

    private fun bindViews(binding: FragmentProfileBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun setupLogoutButton(binding: FragmentProfileBinding) {
        logoutButton = binding.root.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            Firebase.auth.signOut()
        }
    }

    private fun setupImagePicker(binding: FragmentProfileBinding) {
        imagePickerView = binding.root.findViewById(R.id.image_view)
        imagePickerView.setOnClickListener {
            imagePicker.launch("image/*")
        }
        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            imagePickerView.setImageURI(uri.toUri())
        }
    }

    private fun getImagePicker() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imagePickerView.getImagePicker(uri, uCropLauncher)
        }

    private fun getUCropLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = UCrop.getOutput(result.data!!)
                viewModel.imageUri.value = uri.toString()
            }
        }
}