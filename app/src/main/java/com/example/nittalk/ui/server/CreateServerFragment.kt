package com.example.nittalk.ui.server

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
import com.example.nittalk.databinding.FragmentCreateServerBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateServerFragment : Fragment(R.layout.fragment_create_server) {

    private var _binding: FragmentCreateServerBinding? = null
    private val binding get() = _binding!!
    private val createServerViewModel by viewModels<CreateServerViewModel>()
    private val navArgs by navArgs<CreateServerFragmentArgs>()

    private var imageUri: Uri? = null
    private var imageUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateServerBinding.bind(view)

        binding.apply {
            createServerBtn.setOnClickListener {
                if (serverNameEdittext.text.toString().trim().isNotEmpty()) {
                    serverDpProgressbar.visibility = View.VISIBLE
                    disableViews()
                    createServerViewModel.createNewServer(
                        navArgs.userId,
                        serverNameEdittext.text.toString(),
                        imageUrl,
                        requireActivity()
                    )
                        .invokeOnCompletion {
                            enableViews()
                            serverDpProgressbar.visibility = View.GONE
                            findNavController().navigateUp()
                        }
                } else {
                    Snackbar.make(view, "Server Name Can't be Empty", Snackbar.LENGTH_SHORT).show()
                }
            }

            removeImageBtn.setOnClickListener {
                removeImageBtn.visibility = View.GONE
                imageUrl = null
                serverDp.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
            }
            serverDp.setOnClickListener {
                startCropActivity()
            }
            cancelBtn.setOnClickListener {
                findNavController().navigateUp()
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
                imageUri = result.uri
                Glide.with(requireContext()).load(imageUri).circleCrop().into(binding.serverDp)

                FirebaseStorage.getInstance().reference.child("groupDps/$imageUri").putFile(imageUri!!)
                    .addOnProgressListener {
                        binding.serverDpProgressbar.visibility = View.VISIBLE
                        disableViews()
                    }
                    .addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("groupDps/$imageUri").downloadUrl
                            .addOnSuccessListener { uri ->
                                binding.serverDpProgressbar.visibility = View.GONE
                                binding.removeImageBtn.visibility = View.VISIBLE
                                imageUrl = uri.toString()
                                enableViews()
                            }
                    }
                    .addOnFailureListener {
                        binding.serverDpProgressbar.visibility = View.GONE
                        binding.serverDp.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
                        enableViews()
                        Snackbar.make(binding.root, it.message!!.toString(), Snackbar.LENGTH_SHORT).show()
                    }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun disableViews() {
        binding.apply {
            serverDp.isEnabled = false
            serverDpProgressbar.isEnabled = false
            serverNameEdittext.isEnabled = false
            createServerBtn.isEnabled = false
            cancelBtn.isEnabled = false
        }
    }

    private fun enableViews() {
        binding.apply {
            serverDp.isEnabled = true
            serverDpProgressbar.isEnabled = true
            serverNameEdittext.isEnabled = true
            createServerBtn.isEnabled = true
            cancelBtn.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}