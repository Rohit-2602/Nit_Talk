package com.example.nittalk.util

import java.text.DateFormat
import java.util.*

class MessageTimeUtil {

    companion object {

        fun getTimeAgoGroupChat(time: Long) : String {
            val messageTime = Calendar.getInstance()
            messageTime.timeInMillis = time
            val now = Calendar.getInstance()

            return if (now.get(Calendar.DATE) == messageTime.get(Calendar.DATE)) {
                "Today at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(time)
            } else if (now.get(Calendar.DATE) - messageTime.get(Calendar.DATE) == 1) {
                "Yesterday at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(time)
            }  else {
                DateFormat.getDateInstance().format(time) + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(time)
            }
        }

        fun getTimeAgoFriendChat(time: Long) : String {
            val messageTime = Calendar.getInstance()
            messageTime.timeInMillis = time
            val now = Calendar.getInstance()

            return if (now.get(Calendar.DATE) == messageTime.get(Calendar.DATE)) {
                DateFormat.getTimeInstance(DateFormat.SHORT).format(time)
            } else if (now.get(Calendar.DATE) - messageTime.get(Calendar.DATE) == 1) {
                "Yesterday"
            }  else {
                DateFormat.getDateInstance().format(time)
            }
        }

    }

}