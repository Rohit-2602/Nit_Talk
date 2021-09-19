package com.example.nittalk.ui.editprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.nittalk.R
import com.example.nittalk.data.User
import com.example.nittalk.databinding.FragmentEditProfileBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val editProfileViewModel by viewModels<EditProfileViewModel>()
    private val navArgs by navArgs<EditProfileFragmentArgs>()
    private var imageUri: Uri? = null
    private lateinit var currentUserUid: String
    private lateinit var updatedUser : User
    private lateinit var oldUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        currentUserUid = editProfileViewModel.currentUser!!.uid
        oldUser = navArgs.user
        updatedUser = oldUser

        setUpSemesterSpinners()
        setUpBranchSpinners()
        setUpSubSectionSpinners()

        binding.apply {
            Glide.with(requireContext()).load(oldUser.profileImageUrl).circleCrop()
                .into(profileImageView)
            nameEditText.setText(oldUser.name)

            profileImageView.setOnClickListener {
                startCropActivity()
            }
        }

        editProfileViewModel.progress.observe(viewLifecycleOwner) {
            binding.uploadImageProgressbar.visibility = it
        }

        editProfileViewModel.enable.observe(viewLifecycleOwner) {
            binding.apply {
                sectionSpinner.isEnabled = it
                branchSpinner.isEnabled = it
                semesterSpinner.isEnabled = it
                backBtn.isEnabled = it
                nameEditText.isEnabled = it
                profileImageView.isEnabled = it
                saveBtn.isEnabled = it
            }
        }

        binding.apply {
            saveBtn.setOnClickListener {
                if (nameEditText.text.toString().isEmpty()) {
                    nameInputEditText.error = "Name Cannot be Empty !!"
                } else {
                    if (editProfileViewModel.isBranchSemesterOrSectionChanged(
                            oldUser,
                            branch = branchSpinner.selectedItem.toString(),
                            semester = semesterSpinner.selectedItem.toString(),
                            section = sectionSpinner.selectedItem.toString()
                        )
                    ) {
                        editProfileViewModel.showAlertDialog(oldUser.branch, oldUser.semester, requireActivity(), requireContext()) { updateUser() }
                    } else {
                        updateUser()
                    }
                }
            }
        }

    }

    private fun updateUser(): User {
        binding.apply {
            createUserProgressbar.visibility = View.VISIBLE

            updatedUser.name = nameEditText.text.toString()
            updatedUser.lowercaseName = nameEditText.text.toString().lowercase()
            updatedUser.semester = semesterSpinner.selectedItem.toString()
            updatedUser.branch = branchSpinner.selectedItem.toString()
            updatedUser.section = sectionSpinner.selectedItem.toString()

            if (imageUri == null) {
                // Nothing
            }
            else {
                editProfileViewModel.imageDownloadUrl(imageUri)
                    .observe(viewLifecycleOwner) { imageUrl ->
                        updatedUser.profileImageUrl = imageUrl
                    }
            }
            editProfileViewModel.updateUser(updatedUser)
            createUserProgressbar.visibility = View.GONE
            navigateToProfileFragment()
        }
        return updatedUser
    }

    private fun navigateToProfileFragment() {
        val action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment()
        findNavController().navigate(action)
    }

    private fun startCropActivity() {
        CropImage.activity()
            .setAspectRatio(16, 16)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                imageUri = resultUri
                binding.profileImageView.setImageURI(resultUri)
                editProfileViewModel.uploadImage(resultUri, editProfileViewModel.currentUser!!.uid, requireActivity())
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpSemesterSpinners() {
        val semesterList = arrayListOf(
            "Semester 1",
            "Semester 2",
            "Semester 3",
            "Semester 4",
            "Semester 5",
            "Semester 6",
            "Semester 7",
            "Semester 8"
        )
        val semesterAdapter = EditProfileSpinnerAdapter(requireContext(), semesterList)
        binding.semesterSpinner.adapter = semesterAdapter
        binding.semesterSpinner.setSelection(semesterList.indexOf(oldUser.semester))
    }

    private fun setUpBranchSpinners() {
        val branchList = arrayListOf(
            "Civil",
            "Computer Science",
            "Electrical",
            "Electronics",
            "Information Technology",
            "Mechanical",
            "Production"
        )
        val branchAdapter = EditProfileSpinnerAdapter(requireContext(), branchList)
        binding.branchSpinner.adapter = branchAdapter
        binding.branchSpinner.setSelection(branchList.indexOf(oldUser.branch))
    }

    private fun setUpSubSectionSpinners() {
        val subSectionList = arrayListOf(
            "Section 1",
            "Section 2",
            "Section 3",
            "Section 4",
            "Section 5",
            "Section 6",
            "Section 7",
            "Section 8"
        )
        val subSectionAdapter = EditProfileSpinnerAdapter(requireContext(), subSectionList)
        binding.sectionSpinner.adapter = subSectionAdapter
        binding.sectionSpinner.setSelection(subSectionList.indexOf(oldUser.section))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}