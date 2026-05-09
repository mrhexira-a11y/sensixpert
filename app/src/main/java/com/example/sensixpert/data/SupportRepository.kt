package com.example.sensixpert.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class SupportMessage(
    val id: String = "",
    val text: String = "",
    val sender: String = "", // "user" or "admin"
    val timestamp: Long = 0L,
    val read: Boolean = false
)

class SupportRepository {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Send a message from the user.
     * Also updates the chat metadata for admin panel listing.
     */
    suspend fun sendMessage(userId: String, userName: String, userEmail: String, text: String) {
        val chatRef = db.collection("support_chats").document(userId)
        val messageData = hashMapOf(
            "text" to text,
            "sender" to "user",
            "timestamp" to System.currentTimeMillis(),
            "read" to false
        )

        // Add message to subcollection
        chatRef.collection("messages").add(messageData)

        // Update chat metadata
        chatRef.set(
            hashMapOf(
                "userName" to userName,
                "userEmail" to userEmail,
                "userId" to userId,
                "lastMessage" to text,
                "lastMessageTime" to System.currentTimeMillis(),
                "lastSender" to "user",
                "unreadByAdmin" to FieldValue.increment(1),
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )
    }

    /**
     * Observe messages for a specific user's chat in real-time.
     */
    fun observeMessages(userId: String): Flow<List<SupportMessage>> = callbackFlow {
        val listener = db.collection("support_chats")
            .document(userId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.map { doc ->
                    SupportMessage(
                        id = doc.id,
                        text = doc.getString("text") ?: "",
                        sender = doc.getString("sender") ?: "user",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        read = doc.getBoolean("read") ?: false
                    )
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Mark all admin messages as read (when user opens chat).
     */
    fun markAdminMessagesRead(userId: String) {
        db.collection("support_chats")
            .document(userId)
            .collection("messages")
            .whereEqualTo("sender", "admin")
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.update(doc.reference, "read", true)
                }
                batch.commit()
            }

        // Reset unread count for user
        db.collection("support_chats").document(userId)
            .update("unreadByUser", 0)
    }
}
