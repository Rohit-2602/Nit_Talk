package com.example.nittalk.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nittalk.R
import com.example.nittalk.data.User
import com.example.nittalk.databinding.FragmentInfoBinding
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

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
            binding.apply {
                subsectionSpinner.isEnabled = it
                branchSpinner.isEnabled = it
                semesterSpinner.isEnabled = it
                backBtn.isEnabled = it
                nameEditText.isEnabled = it
                profileImageView.isEnabled = it
            }
        }

        binding.apply {
            createBtn.setOnClickListener {
                if (nameEditText.text.toString().isEmpty()) {
                    nameInputEditText.error = "Name Cannot be Empty !!"
                }
                else {
                    createUserProgressbar.visibility = View.VISIBLE
                    authViewModel.imageDownloadUrl(null, currentUserUid).observe(viewLifecycleOwner) { imageUrl ->
                        val user = User(
                            id = currentUserUid,
                            name = nameEditText.text.toString(),
                            lowercaseName = nameEditText.text.toString().lowercase(Locale.ROOT),
                            profileImageUrl = imageUrl,
                            semester = semesterSpinner.selectedItem.toString(),
                            branch = branchSpinner.selectedItem.toString(),
                            section = subsectionSpinner.selectedItem.toString()
                        )
                        /*
                        * To Remove bug from SearchFragment (SearchUserByName) method
                        * which uses firebase .whereIn("", "") method
                        * that requires a not null list
                        * Adding Default value to lists, that are parameter in whereIn method
                        * */
                        user.friends.add("1")
                        user.outGoingRequests.add("1")
                        user.incomingRequests.add("1")
                        authViewModel.createUser(user, this@InfoFragment)
                        authViewModel.saveUserToDB(user)
                        authViewModel.addUserToGroup(user, requireActivity())
                        createUserProgressbar.visibility = View.GONE
                    }
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
        val branchList = arrayListOf(
            "Civil",
            "Computer Science",
            "Electrical",
            "Electronics",
            "Information Technology",
            "Mechanical",
            "Production"
        )
        val branchAdapter = InfoSpinnerAdapter(requireContext(), branchList)
        binding.branchSpinner.adapter = branchAdapter
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
        val subSectionAdapter = InfoSpinnerAdapter(requireContext(), subSectionList)
        binding.subsectionSpinner.adapter = subSectionAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}