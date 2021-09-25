package com.example.nittalk.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nittalk.R
import com.example.nittalk.databinding.FragmentProfileBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.profileToolbar)

        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.apply {
                Glide.with(requireContext()).load(user.profileImageUrl).circleCrop().into(profileIV)
                userNameTextView.text = user.name
                userBranchTextView.text = user.branch
                userSemesterTextView.text = user.semester
                userSectionTextView.text = user.section
                Glide.with(requireContext()).load(user.backgroundImageUrl).into(backgroundImage)
            }
            binding.backgroundImageButton.setOnClickListener {
                startCropActivity()
            }

            binding.editProfileBtn.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(user = user)
                findNavController().navigate(action)
            }
        }
        setHasOptionsMenu(true)
    }

    private fun startCropActivity() {
        CropImage.activity()
            .setAspectRatio(16, 8)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                binding.backgroundImage.setImageURI(resultUri)
                profileViewModel.updateBackgroundImage(resultUri.toString())
                profileViewModel.uploadImageAndGetDownloadUrl(resultUri)
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            profileViewModel.signOut(requireActivity())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}