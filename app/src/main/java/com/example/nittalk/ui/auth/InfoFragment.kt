package com.example.nittalk.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nittalk.R
import com.example.nittalk.data.User
import com.example.nittalk.databinding.FragmentInfoBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info) {

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private var imageUri : Uri?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentInfoBinding.bind(view)

        val currentUserUid = authViewModel.currentUser!!.uid

        setUpSemesterSpinners()
        setUpBranchSpinners()
        setUpSubSectionSpinners()

        binding.profileImageView.setOnClickListener {
            startCropActivity()
        }

        binding.backBtn.setOnClickListener {

        }

        authViewModel.progress.observe(viewLifecycleOwner) {
            binding.uploadImageProgressbar.visibility = it
        }

        authViewModel.enable.observe(viewLifecycleOwner) {
            binding.subsectionSpinner.isEnabled = it
            binding.branchSpinner.isEnabled = it
            binding.semesterSpinner.isEnabled = it
            binding.backBtn.isEnabled = it
            binding.nameEditText.isEnabled = it
            binding.profileImageView.isEnabled = it
        }

        binding.createBtn.setOnClickListener {
            if (binding.nameEditText.text.toString().isEmpty()) {
                binding.nameInputEditText.error = "Name Cannot be Empty !!"
            }
            else {
                binding.createUserProgressbar.visibility = View.VISIBLE
                authViewModel.imageDownloadUrl(
                    imageUri,
                    currentUserUid
                ).observe(viewLifecycleOwner) { imageUrl ->
                    val user = User(
                        id = currentUserUid,
                        name = binding.nameEditText.text.toString(),
                        profileImageUrl = imageUrl,
                        semester = binding.semesterSpinner.selectedItem.toString(),
                        branch = binding.branchSpinner.selectedItem.toString(),
                        section = binding.subsectionSpinner.selectedItem.toString()
                    )
                    authViewModel.createUser(user, this)
                    authViewModel.addUserToGroup(user, requireActivity())
                    binding.createUserProgressbar.visibility = View.GONE
                    Log.i("Rohit", user.toString())
                }
            }
        }

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
                authViewModel.uploadImage(resultUri, authViewModel.currentUser!!.uid, requireActivity())
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
        val semesterAdapter = InfoSpinnerAdapter(requireContext(), semesterList)
        binding.semesterSpinner.adapter = semesterAdapter
    }

    private fun setUpBranchSpinners() {
        val semesterList = arrayListOf(
            "Civil",
            "Computer Science",
            "Electrical",
            "Electronics",
            "Information Technology",
            "Mechanical",
            "Production"
        )
        val branchAdapter = InfoSpinnerAdapter(requireContext(), semesterList)
        binding.branchSpinner.adapter = branchAdapter
    }

    private fun setUpSubSectionSpinners() {
        val semesterList = arrayListOf(
            "Section 1",
            "Section 2",
            "Section 3",
            "Section 4",
            "Section 5",
            "Section 6",
            "Section 7",
            "Section 8"
        )
        val subSectionAdapter = InfoSpinnerAdapter(requireContext(), semesterList)
        binding.subsectionSpinner.adapter = subSectionAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}