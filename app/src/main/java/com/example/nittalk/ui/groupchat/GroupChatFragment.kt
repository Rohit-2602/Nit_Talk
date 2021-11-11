package com.example.nittalk.ui.groupchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nittalk.R
import com.example.nittalk.data.*
import com.example.nittalk.databinding.FragmentGroupChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

@AndroidEntryPoint
class GroupChatFragment : Fragment(R.layout.fragment_group_chat), OnGroupItemSelected, OnMessageLongPress,
    OnTextChannelSelected, OnVoiceChannelClicked {

    private var _binding: FragmentGroupChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    private val groupChatViewModel by viewModels<GroupChatViewModel>()
    private lateinit var groupAdapter : GroupAdapter
    private lateinit var textChannelAdapter: TextChannelAdapter
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var onlineAdapter: OnlineStatusAdapter
    private lateinit var offlineAdapter: OfflineStatusAdapter
    private lateinit var currentGroup : Group
    private lateinit var currentUser: User

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private var imageUri :Uri?= null
    private var imageUrl :String?= null
    private var repliedMessage: Message? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGroupChatBinding.bind(view)
        setUpNavDrawer()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.messageBottomSheet)

        val currentUserToken = groupChatViewModel.currentUserToken

        groupChatViewModel.currentGroup.asLiveData().observe(viewLifecycleOwner) {
            currentGroup = it
        }

        groupChatViewModel.groupName.observe(viewLifecycleOwner) {
            binding.groupNameTextView.text = it
        }

        groupChatViewModel.currentUserFromDB.asLiveData().observe(viewLifecycleOwner) {
            currentUser = it
        }

        binding.apply {

            removeImageBtn.setOnClickListener {
                imageUri = null
                imageUrl = null
                imageContainer.visibility = View.GONE
            }

            blankView.setOnClickListener {
                hideMessageOptions()
            }

            textChannelsTextView.setOnClickListener {
                if (binding.textChannelsRecyclerview.isVisible) {
                    binding.textChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_down,
                        0,
                        0,
                        0
                    )
                    binding.textChannelsRecyclerview.visibility = View.GONE
                } else {
                    binding.textChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                    binding.textChannelsRecyclerview.visibility = View.VISIBLE
                }
            }

            voiceChannelsTextView.setOnClickListener {
                if (voiceChannelsRecyclerview.isVisible) {
                    voiceChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_down,
                        0,
                        0,
                        0
                    )
                    voiceChannelsRecyclerview.visibility = View.GONE
                } else {
                    voiceChannelsTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_arrow_up,
                        0,
                        0,
                        0
                    )
                    voiceChannelsRecyclerview.visibility = View.VISIBLE
                }
            }

            messageImageBtn.setOnClickListener {
                startCropActivity()
            }

        }

        setUpGroupRecyclerView()
        setUpTextChannelRecyclerView()
        setUpVoiceChannelsRecyclerView()
        setUpMessageRecyclerView()
        setUpOnlineUserRecyclerView()
        setUpOfflineUserRecyclerView()

        binding.apply {
            messageEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }
                override fun afterTextChanged(p0: Editable?) {
                    val isEmpty = p0.toString().trim().isEmpty()
                    if (isEmpty) {
                        messageSendBtn.visibility = View.GONE
                    } else {
                        messageSendBtn.visibility = View.VISIBLE
                    }
                }
            })

            messageSendBtn.setOnClickListener {
                val message = messageEditText.text.toString().trim()
                if (message != "") {
                    hideEditMessage()
                    messageEditText.setText("")
                    groupChatViewModel.sendMessage(messageText = message, imageUrl = imageUrl ?: "", repliedTo = repliedMessage)

                    imageUri = null
                    imageUrl = null
                    imageContainer.visibility = View.GONE
                    val membersUidList = currentGroup.members

                    for (member in membersUidList) {
                        if(member != groupChatViewModel.currentUserUid) {
                            groupChatViewModel.sendNotification(
                                context = requireContext(),
                                title = currentGroup.groupName,
                                message = currentUser.name + ": " + message,
                                userId = member,
                                currentUserToken = currentUserToken
                            )
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(), "Write Something", Toast.LENGTH_SHORT).show()
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
                imageUri = result.uri

                binding.apply {
                    msgImage.setImageURI(imageUri)
                    imageContainer.visibility = View.VISIBLE
                    messageSendBtn.isEnabled = false
                }
                FirebaseStorage.getInstance().reference.child("images/$imageUri").putFile(imageUri!!)
                    .addOnProgressListener {
                        val progress = 100.0 * it.bytesTransferred / it.totalByteCount
                        val currentProgress = progress.toInt()
                        binding.imageUploadProgressBar.progress = currentProgress
                    }
                    .addOnSuccessListener {
                        FirebaseStorage.getInstance().reference.child("images/$imageUri").downloadUrl
                            .addOnSuccessListener { uri ->
                                imageUrl = uri.toString()
                                binding.messageSendBtn.isEnabled = true
                            }
                    }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpGroupRecyclerView() {
        groupAdapter = GroupAdapter(this, groupChatViewModel.selectedGroupId)
        groupChatViewModel.currentUserGroups.observe(viewLifecycleOwner) {
            groupAdapter.submitList(it)
        }
        binding.groupRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun checkOutGroup(groupId: String) {
        groupChatViewModel.updateGroupSelected(groupId)
        groupChatViewModel.update(groupId)
    }

    private fun setUpTextChannelRecyclerView() {
        textChannelAdapter = TextChannelAdapter(this, groupChatViewModel.channelSelected, this)
        groupChatViewModel.textChannels.asLiveData().observe(viewLifecycleOwner) { textChannels ->
            val list = textChannels.filter { currentUser.section == it.channelName || it.channelName == "General" }
            textChannelAdapter.submitList(list)
        }
        binding.textChannelsRecyclerview.apply {
            adapter = textChannelAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setUpVoiceChannelsRecyclerView() {
        val voiceChannelAdapter = VoiceChannelAdapter(this)
        groupChatViewModel.voiceChannels.asLiveData().observe(viewLifecycleOwner) {
            voiceChannelAdapter.submitList(it)
        }
        binding.voiceChannelsRecyclerview.apply {
            adapter = voiceChannelAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun joinVoiceCall(voiceChannel: VoiceChannel) {
        val option = JitsiMeetConferenceOptions.Builder()
            .setRoom(voiceChannel.groupId + voiceChannel.channelName)
            .setWelcomePageEnabled(false)
            .build()
        voiceChannel.members.add(currentUser)
        JitsiMeetActivity.launch(context, option)
    }

    @SuppressLint("SetTextI18n")
    override fun showTextChannelMessages(textChannel: TextChannel, channelId: String) {
        groupChatViewModel.updateChannelSelected(textChannel.groupId, channelId)
        val channelName = textChannel.channelName
        binding.apply {
            groupChatToolbar.title = channelName
            onlineChannelTitle.text = "# ${channelName.lowercase()}"
//            welcomeTextView.text = "Welcome to #$channelName!"
//            channelStartTextView.text = "This is the start of the #$channelName channel."
            drawerLayout.closeDrawer(GravityCompat.START)
            messageEditText.setText("")
        }
        Toast.makeText(requireContext(), "Clicked On $channelName", Toast.LENGTH_SHORT)
            .show()
    }

    private fun setUpMessageRecyclerView() {
        messageAdapter = MessageAdapter(this)
        val mLayoutManager = LinearLayoutManager(requireContext())
        groupChatViewModel.messages.asLiveData().observe(viewLifecycleOwner) {
            messageAdapter.submitList(it)
            mLayoutManager.smoothScrollToPosition(binding.messageRV, null, it.size)
        }
        mLayoutManager.stackFromEnd = true
        binding.messageRV.apply {
            adapter = messageAdapter
            layoutManager = mLayoutManager
        }
    }

    override fun showMessageOptions(message: Message, lastMessage: Message, nextLastMessage: Message?) {
        showOptions(message)
        binding.apply {
            editButton.setOnClickListener {
                binding.apply {
                    editMessageContainer.visibility = View.VISIBLE
                    messageEditBtn.visibility = View.VISIBLE
                    messageSendBtn.visibility = View.GONE
                    messageImageBtn.visibility = View.GONE
                    messageEditText.setText(message.message)
                    cancelEditingMessage.setOnClickListener {
                        messageEditText.setText("")
                        editMessageContainer.visibility = View.GONE
                        messageImageBtn.visibility = View.VISIBLE
                        messageEditBtn.visibility = View.GONE
                        messageSendBtn.visibility = View.GONE
                    }
                    messageEditBtn.setOnClickListener {
                        if (messageEditText.text.trim().isEmpty()) {
                            Toast.makeText(requireContext(), "Message Can't be Empty", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            groupChatViewModel.editMessage(messageText = messageEditText.text.trim().toString(), message = message)
                            messageEditText.setText("")
                            Toast.makeText(requireContext(), "Message Edited", Toast.LENGTH_SHORT).show()
                            editMessageContainer.visibility = View.GONE
                            messageImageBtn.visibility = View.VISIBLE
                            messageEditBtn.visibility = View.GONE
                        }
                    }
                }
                hideMessageOptions()
            }

            replyButton.setOnClickListener {
                repliedMessage = message
                hideMessageOptions()
                showEditMessage(message)
            }

            cancelReplyingMessage.setOnClickListener {
                hideEditMessage()
                repliedMessage = null
            }

            deleteButton.setOnClickListener {
                groupChatViewModel.deleteMessage(message)
                hideMessageOptions()
            }
        }
    }

    private fun showEditMessage(message: Message) {
        binding.apply {
            replyingToText.text = message.senderName
            repliedMessageContainer.visibility = View.VISIBLE
        }
    }

    private fun hideEditMessage() {
        binding.apply {
            repliedMessageContainer.visibility = View.GONE
        }
    }

    private fun showOptions(message: Message) {
        binding.apply {
            if(groupChatViewModel.currentUserUid != message.senderId) {
                editButton.visibility = View.GONE
                deleteButton.visibility = View.GONE
            }
            else {
                editButton.visibility = View.VISIBLE
                deleteButton.visibility = View.VISIBLE
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            blankView.visibility = View.VISIBLE
        }
    }

    private fun hideMessageOptions() {
        binding.apply {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            blankView.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOnlineUserRecyclerView() {
        onlineAdapter = OnlineStatusAdapter()
        groupChatViewModel.onlineGroupMembers.observe(viewLifecycleOwner) {
            onlineAdapter.submitList(it)
            binding.onlineTextView.text = "Online - ${it.size}"
        }
        binding.onlineRecyclerView.apply {
            adapter = onlineAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpOfflineUserRecyclerView() {
        offlineAdapter = OfflineStatusAdapter()
        groupChatViewModel.offlineGroupMembers.observe(viewLifecycleOwner) {
            offlineAdapter.submitList(it)
            binding.offlineTextView.text = "Offline - ${it.size}"
        }
        binding.offlineRecyclerView.apply {
            adapter = offlineAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpNavDrawer() {
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.groupChatToolbar)

//        CoroutineScope(Dispatchers.Main).launch {
            groupChatViewModel.channelName.observe(viewLifecycleOwner) { channelName ->
                binding.apply {
                    groupChatToolbar.title = channelName
                    onlineChannelTitle.text = "# ${channelName.lowercase()}"
//                    welcomeTextView.text = "Welcome to #$channelName!"
//                    channelStartTextView.text = "This is the start of the #$channelName channel."
                }
            }
//        }

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        toggle = object : ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        ) {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                onlineAdapter.notifyDataSetChanged()
                offlineAdapter.notifyDataSetChanged()
                textChannelAdapter.notifyDataSetChanged()
            }
            override fun onDrawerOpened(drawerView: View) {
                bottomNav.visibility = View.VISIBLE
            }
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                bottomNav.visibility = View.GONE
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("RtlHardcoded")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        if (item.itemId == R.id.onlineStatus) {
            if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            } else {
                binding.drawerLayout.openDrawer(Gravity.RIGHT)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}