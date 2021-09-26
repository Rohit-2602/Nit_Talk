package com.example.nittalk.firebase

import android.content.Context
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.nittalk.util.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class FirebaseUtil {

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fireStore: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val tokenCollection = fireStore.collection("tokens")

    fun updateToken(token: String) {
        val currentUser = firebaseAuth.currentUser

        val map = hashMapOf("token" to token)

        if (currentUser != null) {
            tokenCollection.document(currentUser.uid).set(map).addOnCompleteListener {
                if (it.isSuccessful) {
//                    Toast.makeText(context, "Token Updated", Toast.LENGTH_SHORT).show()
                } else {
//                    Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getCurrentUserToken(currentUserId: String): String {
        var token = ""
        tokenCollection.document(currentUserId).get().addOnCompleteListener {
            token = it.result.getString("token")!!.toString()
        }
        return token
    }

    fun sendNotification(context: Context, title: String, message: String, userId: String, currentUserToken: String) {
        val userTokenRef = tokenCollection.document(userId)

        userTokenRef
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                if (firebaseFirestoreException != null) {
                    Toast.makeText(context, firebaseFirestoreException.message, Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val deviceToken = documentSnapshot!!.getString("token")

                if (deviceToken == currentUserToken) {
                    return@addSnapshotListener
                }

                val notification = JSONObject()
                val notificationData = JSONObject()

                notificationData.put("title", title)
                notificationData.put("message", message)

                notification.put("to", deviceToken)
                notification.put("data", notificationData)

                val fcmApiUrl = "https://fcm.googleapis.com/fcm/send"
                val contentType = "application/json"

                val request = object : JsonObjectRequest(fcmApiUrl, notification,
                    {
                        Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show()
                    },
                    {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val params = HashMap<String, String>()
                        params["Authorization"] = "key=" + Constant.FIREBASE_SERVER_KEY
                        params["Sender"] = "id=" + Constant.SENDER_ID
                        params["Content-Type"] = contentType
                        return params
                    }
                }

                val queue = Volley.newRequestQueue(context)
                queue.add(request)

            }

    }

}