package com.example.nittalk.firebase

import android.content.Context
import android.util.Log
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
                    Log.i("Token", "Save Token $token")
                } else {
                    Log.i("Token", "Failed To Save Token")
                }
            }
        }

    }

    fun sendNotification(context: Context, title: String, message: String, userId: String) {
        val userTokenRef = tokenCollection.document(userId)

        userTokenRef
            .addSnapshotListener { documentSnapshot: DocumentSnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                if (firebaseFirestoreException != null) {
                    Log.i("Error Getting UserToken", firebaseFirestoreException.message.toString())
                    return@addSnapshotListener
                }
                val deviceToken = documentSnapshot!!.getString("token")

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
                        Log.i("Rohit JSON", it.toString())
                        Toast.makeText(context, "Notification Sent", Toast.LENGTH_SHORT).show()
                    },
                    {
                        Log.i("Rohit JSON", it.message.toString())
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