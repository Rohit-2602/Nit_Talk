package com.example.nittalk.firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.nittalk.data.*
import com.example.nittalk.db.GroupPreferencesDao
import com.example.nittalk.db.UserDao
import com.example.nittalk.ui.auth.AuthActivity
import com.example.nittalk.ui.auth.InfoFragment
import com.example.nittalk.ui.auth.LoginFragment
import com.example.nittalk.ui.auth.LoginFragmentDirections
import com.example.nittalk.util.Constant.DEFAULT_USER_DP
import com.example.nittalk.util.Constant.GROUP_SELECTED
import com.example.nittalk.util.Constant.LOGIN_STATE_KEY
import com.example.nittalk.util.Constant.branchIdHashMap
import com.example.nittalk.util.Constant.semesterIdHashMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class FirebaseSource @Inject constructor(private val preferencesManager: PreferencesManager, private val userDao: UserDao, private val groupPreferencesDao: GroupPreferencesDao) {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fireStore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val storageReference: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val userCollection = fireStore.collection("users")
    private val groupCollection = fireStore.collection("groups")
    private val statusCollection = fireStore.collection("status")
    private val inboxCollection = fireStore.collection("inbox")
    private val messageCollection = fireStore.collection("messages")

    val progress = MutableLiveData(View.GONE)
    val enable = MutableLiveData(true)
    val loginState = preferencesManager.loginStateFlow

    @ExperimentalCoroutinesApi
    suspend fun makeCurrentUserOnline(userId: String) {
        val currentUser = getUserById(userId)
        userCollection.document(userId).update("isOnline", true)
        statusCollection.document("online").collection("onlineMembers").document(userId).set(currentUser)
        statusCollection.document("offline").collection("offlineMembers").document(userId).delete()
    }

    @ExperimentalCoroutinesApi
    suspend fun makeCurrentUserOffline(userId: String) {
        val currentUser = getUserById(userId)
        userCollection.document(userId).update("isOnline", false)
        statusCollection.document("online").collection("onlineMembers").document(userId).delete()
        statusCollection.document("offline").collection("offlineMembers").document(userId).set(currentUser)
    }

    @ExperimentalCoroutinesApi
    fun onlineGroupMembers(): Flow<List<User>> = preferencesManager.groupSelected.flatMapLatest { groupId ->
        callbackFlow {
            val group = getGroupById(groupId)
            val members = group.first().members
            val users = statusCollection.document("online").collection("onlineMembers")
                .whereIn("id", members)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Online Group Members")
                        return@addSnapshotListener
                    }
                    val map = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(map)
                }
            awaitClose {
                users.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun offlineGroupMembers(): Flow<List<User>> = preferencesManager.groupSelected.flatMapLatest { groupId ->
        callbackFlow {
            val group = getGroupById(groupId)
            val members = group.first().members
            val users = statusCollection.document("offline").collection("offlineMembers")
                .whereIn("id", members)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Online Group Members")
                        return@addSnapshotListener
                    }
                    val map = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(map)
                }
            awaitClose {
                users.remove()
            }
        }
    }

    suspend fun saveUserToDB(user: User) =
        userDao.insertUser(user)

    fun getCurrentUserFromDB(currentUserId: String) =
        userDao.getCurrentUser(currentUserId)

    suspend fun getUserById(userId: String): User {
        return userCollection.document(userId).get().await().toObject(User::class.java)!!
    }

    fun createUserWithEmailAndPassword(email: String, password: String, activity: Activity) {
        progress.value = View.VISIBLE
        enable.value = false
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { createUser ->
                if (createUser.isSuccessful) {
                    firebaseAuth.currentUser!!.sendEmailVerification()
                        .addOnCompleteListener { emailVerification ->
                            if (emailVerification.isSuccessful) {
                                enable.value = true
                                progress.value = View.GONE
                                showToast(activity, "Email Verification Sent")
                            } else {
                                enable.value = true
                                progress.value = View.GONE
                                showToast(activity, emailVerification.exception?.message)
                            }
                        }
                } else {
                    enable.value = true
                    progress.value = View.GONE
                    showToast(activity, createUser.exception?.message)
                }
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String, loginFragment: LoginFragment) {
        progress.value = View.VISIBLE
        enable.value = false
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseAuth.currentUser!!.reload().addOnCompleteListener { reload ->
                        if (reload.isSuccessful) {
                            userCollection.document(firebaseAuth.currentUser!!.uid).get()
                                .addOnSuccessListener {
                                    if (it.exists()) {
                                        enable.value = true
                                        progress.value = View.GONE
                                        CoroutineScope(Dispatchers.IO).launch {
                                            preferencesManager.updateLoginState(
                                                LOGIN_STATE_KEY,
                                                true
                                            )
                                            val user = getUserById(firebaseAuth.currentUser!!.uid)
                                            userDao.insertUser(user)
                                        }
                                    } else {
                                        if (firebaseAuth.currentUser?.isEmailVerified == true) {
                                            enable.value = true
                                            progress.value = View.GONE
                                            startInfoFragment(loginFragment)
                                            showToast(loginFragment.requireActivity(), "Info Fragment")
                                        } else {
                                            enable.value = true
                                            progress.value = View.GONE
                                            showToast(
                                                loginFragment.requireActivity(),
                                                "Please verify your Email !!"
                                            )
                                        }
                                    }
                                }
                        } else {
                            enable.value = true
                            progress.value = View.GONE
                            showToast(loginFragment.requireActivity(), reload.exception?.message)
                        }
                    }
                } else {
                    enable.value = true
                    progress.value = View.GONE
                    showToast(loginFragment.requireActivity(), task.exception?.message)
                }
            }
    }

    suspend fun createUser(user: User, infoFragment: InfoFragment) {
        progress.value = View.VISIBLE
        enable.value = false
        val initialGroupSelectedId = branchIdHashMap[user.branch] + semesterIdHashMap[user.semester]
        preferencesManager.updateGroupSelected(GROUP_SELECTED, initialGroupSelectedId)

        userCollection.document(user.id).set(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                enable.value = true
                progress.value = View.GONE
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesManager.updateLoginState(LOGIN_STATE_KEY, true)
                }

            } else {
                enable.value = true
                progress.value = View.GONE
                showToast(infoFragment.requireActivity(), task.exception?.message)
            }
        }
    }

    suspend fun updateFirebaseUser(user: User) {
        CoroutineScope(Dispatchers.Main).launch {
            progress.value = View.VISIBLE
            enable.value = false
        }
        val map : Map<String, String> = hashMapOf(
            "name" to user.name,
            "lowercaseName" to user.name.lowercase(),
            "profileImageUrl" to user.profileImageUrl,
            "branch" to user.branch,
            "section" to user.section,
            "semester" to user.semester,
            "backgroundImageUrl" to user.backgroundImageUrl
        )
        userCollection.document(user.id).update(map).await()
        CoroutineScope(Dispatchers.Main).launch {
            progress.value = View.GONE
            enable.value = true
        }
    }

    suspend fun updateUserBackgroundImage(userId: String, backgroundImage: String) =
        userDao.updateUserBackgroundImage(userId, backgroundImage)

    suspend fun uploadImage(imageUri: Uri, userId: String, activity: Activity) {
        progress.value = View.VISIBLE
        enable.value = false
        storageReference.reference.child("${userId}/uploads/DP").putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progress.value = View.GONE
                    enable.value = true
                    showToast(activity, "Upload Successful")
                } else {
                    progress.value = View.GONE
                    enable.value = true
                    showToast(activity, task.exception?.message)
                }
            }.await()
    }

    fun uploadImageAndGetDownloadUrl(imageUri: Uri, userId: String) {
        val path = imageUri.toString()
        storageReference.reference.child("${userId}/images/${path}").putFile(imageUri)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageReference.reference.child("${userId}/images/${path}").downloadUrl.addOnCompleteListener {
                        if(task.isSuccessful) {
                            val downloadUrl = it.result.toString()
                            userCollection.document(userId).update("backgroundImageUrl", downloadUrl)
                        }
                    }
                }
            }
    }

    suspend fun getProfileImageDownloadUrl(userId: String): String {
        var url = DEFAULT_USER_DP
        storageReference.reference.child("${userId}/uploads/DP").downloadUrl.addOnCompleteListener {
            url = it.result.toString()
        }.await()
        return url
    }

    fun addUserToGroup(user: User, activity: Activity) {
        progress.value = View.VISIBLE
        enable.value = false
        val id = branchIdHashMap[user.branch] + semesterIdHashMap[user.semester]
        groupCollection.document(id).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val group = dataSnapshot.toObject(Group::class.java)!!
                group.members.add(user.id)

                val userGroups = user.groups
                userGroups.add(id)
                userCollection.document(user.id).update("groups", userGroups)

                val serverSelected1 = GroupPreferences(id, id + "General")

                CoroutineScope(Dispatchers.IO).launch {
                    groupPreferencesDao.insertServer(serverSelected1)
                }

                groupCollection.document(id).set(group)
                progress.value = View.GONE
                enable.value = true
            } else {
                val link =
                    "https://firebasestorage.googleapis.com/v0/b/whatsapp-clone-bcfa9.appspot.com/o/spiderman.jpg?alt=media&token=22bbb815-26b7-4da1-87c7-29961f510d90"
                val group = Group(
                    groupId = id,
                    groupName = "${user.branch} ${user.semester}",
                    groupDp = link
                )

                group.members.add(user.id)
                val userGroups = user.groups
                userGroups.add(id)
                userCollection.document(user.id).update("groups", userGroups)

                createGeneralTextChannel(group = group, activity = activity)
                CoroutineScope(Dispatchers.IO).launch {
                    createTextChannel(channelName = user.section, group = group, activity = activity)
                    createVoiceChannel(group = group)
                }

                val serverSelected1 = GroupPreferences(id, id + "General")

                CoroutineScope(Dispatchers.IO).launch {
                    groupPreferencesDao.insertServer(serverSelected1)
                }

                groupCollection.document(id).set(group).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast(activity, "New Group Created")
                    } else {
                        showToast(activity, task.exception?.message)
                    }
                }
                progress.value = View.GONE
                enable.value = true
            }
        }
    }

    suspend fun changeUserGroup(oldUserBranch: String, oldUserSemester: String, updatedUser: User, activity: Activity) {

        val newGroupId = branchIdHashMap[updatedUser.branch] + semesterIdHashMap[updatedUser.semester]
        val oldGroupId = branchIdHashMap[oldUserBranch] + semesterIdHashMap[oldUserSemester]
        preferencesManager.updateGroupSelected(GROUP_SELECTED, newGroupId)

        val oldServer = GroupPreferences(oldGroupId, oldGroupId + "General")
        groupPreferencesDao.removeServer(oldServer)

        groupCollection.document(oldGroupId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val group = dataSnapshot.toObject(Group::class.java)!!
                group.members.remove(updatedUser.id)
                groupCollection.document(oldGroupId).set(group)
            }
        }.await()

        addUserToGroup(updatedUser, activity)
    }

    @ExperimentalCoroutinesApi
    fun getGroupById(groupId: String) : Flow<Group> {
        return callbackFlow {
            val groups = groupCollection.document(groupId)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Group Detail")
                        return@addSnapshotListener
                    }
                    val map = documentSnapshot!!.toObject(Group::class.java)!!
                    offer(map)
                }
            awaitClose {
                groups.remove()
            }
        }
    }

    fun getGroupPref() =
        groupPreferencesDao.getSelectedGroupChannel()

    fun update(groupSelectedId: String) = groupPreferencesDao.update(groupSelectedId)

    @ExperimentalCoroutinesApi
    fun getChannelName(groupId: String, channelId: String) : Flow<String> {
        return callbackFlow {
            val channelName = groupCollection.document(groupId).collection("textChannels").document(channelId)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Channel Name")
                        return@addSnapshotListener
                    }
                    val map = documentSnapshot!!.toObject(TextChannel::class.java)!!.channelName
                    offer(map)
                }
            awaitClose {
                channelName.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getGroupName(groupId: String) : Flow<String> {
        return callbackFlow {
            val groupName = groupCollection.document(groupId)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Channel Name")
                        return@addSnapshotListener
                    }
                    val map = documentSnapshot!!.toObject(Group::class.java)!!.groupName
                    offer(map)
                }
            awaitClose {
                groupName.remove()
            }
        }
    }

    fun updateChannelSelected(groupId: String, channelId: String) =
        groupPreferencesDao.updateChannelSelected(groupId, channelId)

    private fun createGeneralTextChannel(group: Group, activity: Activity) {
        val textChannelCollection = groupCollection.document(group.groupId).collection("textChannels")
        val id = group.groupId + "General"
        val sectionTextChannel = TextChannel(
            channelId = id,
            channelName = "General",
            createdAt = System.currentTimeMillis(),
            groupId = group.groupId
        )
        textChannelCollection.document(id).set(sectionTextChannel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast(activity, "Text Channel Created")
            }
            else {
                showToast(activity, task.exception?.message)
            }
        }
        group.channelsId.add(id)
        group.textChannels.add(id)
        groupCollection.document(group.groupId).set(group)
    }

    private suspend fun createTextChannel(channelName: String, group: Group, activity: Activity) {
        val textChannelCollection = groupCollection.document(group.groupId).collection("textChannels")
        for (i in 1..8) {
            val id = textChannelCollection.document().id
            val sectionTextChannel = TextChannel(
                channelId = id,
                channelName = "Section $i",
                createdAt = System.currentTimeMillis(),
                groupId = group.groupId
            )
            textChannelCollection.document(id).set(sectionTextChannel).await()
            group.channelsId.add(id)
            group.textChannels.add(id)
        }
        groupCollection.document(group.groupId).set(group)
    }

    private suspend fun createVoiceChannel(group: Group) {
        val voiceChannelCollection = groupCollection.document(group.groupId).collection("voiceChannels")
        val id = voiceChannelCollection.document().id
        val voiceChannel = VoiceChannel(
            channelId = id,
            channelName = "General",
            createdAt = System.currentTimeMillis(),
            groupId = group.groupId
        )
        voiceChannelCollection.document(id).set(voiceChannel).await()
        group.channelsId.add(id)
        group.voiceChannels.add(id)

        groupCollection.document(group.groupId).set(group)
    }

    @ExperimentalCoroutinesApi
    suspend fun getUserGroup(userId: String) : Flow<List<Group>> {
        val currentUser = getUserById(userId)
        return callbackFlow {
            val groups = groupCollection.whereIn("groupId", currentUser.groups)
                .addSnapshotListener { querySnapshot : QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Groups")
                        return@addSnapshotListener
                    }
                    val groupList = querySnapshot!!.documents.mapNotNull { it.toObject(Group::class.java) }
                    offer(groupList)
                }
            awaitClose {
                groups.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getGroupTextChannels(groupId: String) : Flow<List<TextChannel>> {
        return callbackFlow {
            val channels = groupCollection.document(groupId).collection("textChannels").orderBy("createdAt")
                .addSnapshotListener { querySnapShot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Fetching Channels")
                        return@addSnapshotListener
                    }
                    val mapChannels = querySnapShot!!.documents.mapNotNull { it.toObject(TextChannel::class.java) }
                    offer(mapChannels)
                }
            awaitClose {
                channels.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getGroupVoiceChannels(groupId: String) : Flow<List<VoiceChannel>> {
        return callbackFlow {
            val channels = groupCollection.document(groupId).collection("voiceChannels").orderBy("createdAt")
                .addSnapshotListener { querySnapShot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Fetching Channels")
                        return@addSnapshotListener
                    }
                    val mapChannels = querySnapShot!!.documents.mapNotNull { it.toObject(VoiceChannel::class.java) }
                    offer(mapChannels)
                }
            awaitClose {
                channels.remove()
            }
        }
    }

    fun sendMessage(groupSelectedId: String, channelSelectedId: String, messageText: String, imageUrl: String, currentUser: User) {
        val messageCollection = groupCollection.document(groupSelectedId).collection("textChannels").document(channelSelectedId).collection("messages")
        val id = messageCollection.document().id
        val message = Message(
            senderId = firebaseAuth.currentUser!!.uid,
            message = messageText,
            messageId = id,
            imageUrl = imageUrl,
            senderDp = currentUser.profileImageUrl,
            senderName = currentUser.name,
            sendAt = System.currentTimeMillis()
        )
        messageCollection.document(id).set(message)
    }

    @ExperimentalCoroutinesApi
    fun getChannelMessages(groupId: String, channelId: String) : Flow<List<Message>> {
        return callbackFlow {
            val messages = groupCollection.document(groupId).collection("textChannels").document(channelId).collection("messages")
                .orderBy("sendAt")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Fetching Messages")
                        return@addSnapshotListener
                    }
                    val map = querySnapshot!!.documents.mapNotNull { it.toObject(Message::class.java) }
                    offer(map)
                }
            awaitClose {
                messages.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun searchUserByName(query: String): Flow<List<User>> {
        return callbackFlow {
            val users = userCollection
                .orderBy("lowercaseName")
                .startAt(query.lowercase(Locale.ROOT))
                .endAt(query.lowercase(Locale.ROOT) + "\uf8ff")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(message = "Error Fetching Posts", cause = firebaseFirestoreException)
                        return@addSnapshotListener
                    }
                    val map = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(map)
                }
            awaitClose {
                users.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun sendFriendRequest(friendId: String, currentUserId: String) {
        val currentUser = getUserById(currentUserId)
        val friend = getUserById(friendId)

        currentUser.outGoingRequests.add(friendId)
        friend.incomingRequests.add(currentUserId)

        userCollection.document(currentUserId).set(currentUser)
        userCollection.document(friendId).set(friend)
    }

    @ExperimentalCoroutinesApi
    suspend fun cancelFriendRequest(friendId: String, currentUserId: String) {
        val currentUser = getUserById(currentUserId)
        val friend = getUserById(friendId)

        currentUser.outGoingRequests.remove(friendId)
        friend.incomingRequests.remove(currentUserId)

        userCollection.document(currentUserId).set(currentUser)
        userCollection.document(friendId).set(friend)
    }

    @ExperimentalCoroutinesApi
    suspend fun acceptFriendRequest(friendId: String, currentUserId: String) {
        val currentUser = getUserById(currentUserId)
        val friend = getUserById(friendId)

        currentUser.incomingRequests.remove(friendId)
        friend.outGoingRequests.remove(currentUserId)

        currentUser.friends.add(friendId)
        friend.friends.add(currentUserId)

        userCollection.document(currentUserId).set(currentUser)
        userCollection.document(friendId).set(friend)

        val userInbox = Inbox(friendId = friendId, lastMessage = "", friendName = friend.name, lastMessageTime = System.currentTimeMillis())
        val friendInbox = Inbox(friendId = currentUserId, lastMessage = "", friendName = currentUser.name, lastMessageTime = System.currentTimeMillis())
        inboxCollection.document(currentUserId).collection("userInbox").document(friendId).set(userInbox)
        inboxCollection.document(friendId).collection("userInbox").document(currentUserId).set(friendInbox)

    }

    @ExperimentalCoroutinesApi
    suspend fun declineFriendRequest(friendId: String, currentUserId: String) {
        val currentUser = getUserById(currentUserId)
        val friend = getUserById(friendId)

        currentUser.incomingRequests.remove(friendId)
        friend.outGoingRequests.remove(currentUserId)

        userCollection.document(currentUserId).set(currentUser)
        userCollection.document(friendId).set(friend)
    }

    @ExperimentalCoroutinesApi
    fun getUserFlowById(userId: String): Flow<User> {
        return callbackFlow {
            val user = userCollection.document(userId)
                .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting User")
                        return@addSnapshotListener
                    }
                    val map = documentSnapshot!!.toObject(User::class.java)!!
                    offer(map)
                }
            awaitClose {
                user.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getIncomingRequests(currentUserId: String): Flow<List<User>> = getUserFlowById(currentUserId).flatMapLatest { currentUser ->
        callbackFlow {
            val userRequests = currentUser.incomingRequests
            val incomingRequest = userCollection.whereIn("id", userRequests)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Incoming Request")
                        return@addSnapshotListener
                    }
                    val incomingMap = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(incomingMap)
                }
            awaitClose {
                incomingRequest.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getOutGoingRequests(currentUserId: String): Flow<List<User>> = getUserFlowById(currentUserId).flatMapLatest { currentUser ->
        callbackFlow {
            val userOutgoingRequest = currentUser.outGoingRequests
            val outgoingRequest = userCollection.whereIn("id", userOutgoingRequest)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Incoming Requests")
                        return@addSnapshotListener
                    }
                    val outgoingMap = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(outgoingMap)
                }
            awaitClose {
                outgoingRequest.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserOnlineFriends(currentUserId: String): Flow<List<User>> = getUserFlowById(currentUserId).flatMapLatest { currentUser ->
        callbackFlow {
            val userFriends = currentUser.friends
            val onlineFriends = statusCollection.document("online")
                .collection("onlineMembers").whereIn("id", userFriends)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Incoming Requests")
                        return@addSnapshotListener
                    }
                    val onlineFriendsMap = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(onlineFriendsMap)
                }
            awaitClose {
                onlineFriends.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserOfflineFriends(currentUserId: String): Flow<List<User>> = getUserFlowById(currentUserId).flatMapLatest { currentUser ->
        callbackFlow {
            val userFriends = currentUser.friends
            val offlineFriends = statusCollection.document("offline")
                .collection("offlineMembers").whereIn("id", userFriends)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Incoming Requests")
                        return@addSnapshotListener
                    }
                    val offlineFriendsMap = querySnapshot!!.documents.mapNotNull { it.toObject(User::class.java) }
                    offer(offlineFriendsMap)
                }
            awaitClose {
                offlineFriends.remove()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getUserInbox(currentUserId: String): Flow<List<Inbox>> {
        return callbackFlow {
            val inbox = inboxCollection.document(currentUserId).collection("userInbox")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting User Inbox")
                        return@addSnapshotListener
                    }
                    val inboxMap = querySnapshot!!.documents.mapNotNull { it.toObject(Inbox::class.java) }
                    offer(inboxMap)
                }
            awaitClose {
                inbox.remove()
            }
        }
    }

    fun sendPersonalMessage(currentUser: User, friendId: String, imageUrl: String, messageText: String) {
        val combinedId = if (currentUser.id > friendId) currentUser.id + friendId
                         else friendId + currentUser.id

        val id = messageCollection.document().id
        val sendTime = System.currentTimeMillis()
        val message = Message(
            senderId = firebaseAuth.currentUser!!.uid,
            message = messageText,
            messageId = id,
            imageUrl = imageUrl,
            senderDp = currentUser.profileImageUrl,
            senderName = currentUser.name,
            sendAt = sendTime
        )

        messageCollection.document(combinedId).collection("messages").document(id).set(message)
        inboxCollection.document(currentUser.id).collection("userInbox").document(friendId).update("lastMessage", messageText)
        inboxCollection.document(friendId).collection("userInbox").document(currentUser.id).update("lastMessage", messageText)
        inboxCollection.document(currentUser.id).collection("userInbox").document(friendId).update("lastMessageTime", sendTime)
        inboxCollection.document(friendId).collection("userInbox").document(currentUser.id).update("lastMessageTime", sendTime)

    }

    @ExperimentalCoroutinesApi
    fun getFriendMessages(currentUserId: String, friendId: String): Flow<List<Message>> {
        val combinedId = if (currentUserId > friendId) currentUserId + friendId
                            else friendId + currentUserId
        return callbackFlow {
            val messages = messageCollection.document(combinedId).collection("messages")
                .orderBy("sendAt")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    if (firebaseFirestoreException != null) {
                        cancel(cause = firebaseFirestoreException, message = "Error Getting Messages")
                        return@addSnapshotListener
                    }
                    val messagesMap = querySnapshot!!.documents.mapNotNull { it.toObject(Message::class.java) }
                    offer(messagesMap)
                }
            awaitClose {
                messages.remove()
            }
        }
    }

    private fun startInfoFragment(loginFragment: LoginFragment) {
        val action = LoginFragmentDirections.actionLoginFragmentToInfoFragment()
        loginFragment.findNavController().navigate(action)
    }

    suspend fun logout(activity: Activity) {
        firebaseAuth.signOut()
        preferencesManager.updateLoginState(LOGIN_STATE_KEY, false)
        startAuthActivity(activity)
    }

    private fun startAuthActivity(activity: Activity) {
        val intent = Intent(activity, AuthActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
    }

    fun currentUser() = firebaseAuth.currentUser

    private fun showToast(activity: Activity, text: String?) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

}