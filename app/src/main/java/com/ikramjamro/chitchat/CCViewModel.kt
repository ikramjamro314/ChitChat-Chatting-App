package com.ikramjamro.chitchat

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.ikramjamro.chitchat.Data.CHATS
import com.ikramjamro.chitchat.Data.ChatData
import com.ikramjamro.chitchat.Data.ChatUser
import com.ikramjamro.chitchat.Data.Events
import com.ikramjamro.chitchat.Data.MESSAGE
import com.ikramjamro.chitchat.Data.Message
import com.ikramjamro.chitchat.Data.STATUS
import com.ikramjamro.chitchat.Data.Status
import com.ikramjamro.chitchat.Data.USER_NODE
import com.ikramjamro.chitchat.Data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {


    var inProgress = mutableStateOf(false)
    var eventMutableState = mutableStateOf<Events<String>?>(null)
    var signup = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)

    var inProgressChats = mutableStateOf(false)
    var chats = mutableStateOf<List<ChatData>>(listOf())

    val chatMessage = mutableStateOf<List<Message>>(listOf())
    val inProgressMessage = mutableStateOf(false)
    var currentChatMessageListener: ListenerRegistration? = null

    val status = mutableStateOf<List<Status>>(listOf())
    val inProgressStatus = mutableStateOf(false)

    init {
        val currentUser = auth.currentUser
        signup.value = currentUser!= null
        currentUser?.uid?.let {
            getUserData(it)
        }

    }

    fun populateStatuses() {
        val timeDelta=24L*60*60*1000
        // gives the time of last 24 hours in milliSeconds
        val cutOff = System.currentTimeMillis()-timeDelta

        inProgressStatus.value = true

        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userID", userData.value?.userID),
                Filter.equalTo("user2.userID", userData.value?.userID)
            )
        ).addSnapshotListener { value, error ->
            if(error!=null){
                handleException(error)
                inProgressStatus.value=false
            }
            if (value != null) {
                val currentConnection = arrayListOf(userData.value?.userID)
                val chats = value.toObjects<ChatData>()
                chats.forEach { chat ->

                    if (chat.user1.userID == userData.value?.userID) {
                        currentConnection.add(chat.user2.userID)
                    } else {
                        currentConnection.add(chat.user1.userID)
                    }

                }

                db.collection(STATUS).whereGreaterThan("time",cutOff).whereIn("user.userID", currentConnection)
                    .addSnapshotListener { value, error ->
                     if(error!=null){
                         handleException(error)
                     }
                        if (value!=null){
                            status.value=value.toObjects()
                            inProgressStatus.value= false
                        }
                    }

            }
            }

        inProgressStatus.value = false
        status.value = emptyList()

    }

    fun populateMessages(chatID: String) {
        inProgressMessage.value = true
        currentChatMessageListener = db.collection(CHATS).document(chatID).collection(MESSAGE)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    chatMessage.value = value.documents.mapNotNull {
                        it.toObject<Message>()
                    }.sortedBy { it.time }
                    inProgressMessage.value = false
                }
            }
    }

    fun dePopulate() {
        chatMessage.value = listOf()
        currentChatMessageListener = null
    }

    fun onSendReply(chatID: String, message: String) {

        val time = Calendar.getInstance().time
        val msg = Message(userData.value?.userID, message, time.toString())
        db.collection(CHATS).document(chatID).collection(MESSAGE).document().set(msg)

    }

    fun populateChats() {
        inProgressChats.value = true
        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userID", userData.value?.userID),
                Filter.equalTo("user2.userID", userData.value?.userID)
            )
        ).addSnapshotListener { value, error ->
            if (value != null) {
                chats.value = value.documents.mapNotNull {
                    it.toObject<ChatData>()
                }
                inProgressChats.value = false
            }
        }
        inProgressChats.value = false
    }


    fun signUp(name: String, number: String, email: String, password: String,context: Context) {
        inProgress.value = true
        if (name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill All The Fields!")
            Toast.makeText(context,"Please Fill all the Fields!",Toast.LENGTH_SHORT).show()
            inProgress.value=false
            return
        }

        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {        // check that if the result from above query is empty
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        CreateOrUpdateUserProfile(name=name, number = number)
                    } else {
                        handleException(it.exception, "Sign Up Failed!")
                        inProgress.value=false
                        Toast.makeText(context,"Sign Up Failed!",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                handleException(customMessage = "Number already exist")
                Toast.makeText(context,"Number already exist!",Toast.LENGTH_SHORT).show()
                inProgress.value = false
            }

        }

    }

    fun logIn(email: String, password: String,context: Context) {
        if (email.isEmpty() or password.isEmpty()) {
            handleException(customMessage = "Please Fill all the Fields!")
            Toast.makeText(context,"Please Fill all the Fields!",Toast.LENGTH_SHORT).show()
            return
        } else {
            inProgress.value = true
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    signup.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }

                } else {
                    inProgress.value = false
                    handleException(exception = it.exception, customMessage = "Log in Failed!")
                    Toast.makeText(context,"Log in Failed!",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    fun CreateOrUpdateUserProfile(
        name: String? = null,
        number: String? = null,
        ImageUrl: String? = null,
    ) {
        var uid = auth.currentUser?.uid
        // created new user data
        val userData = UserData(
            userID = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = ImageUrl ?: userData.value?.imageUrl
        )

        // if the uid exists after the authentication
        uid?.let {
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    val updates = mutableMapOf<String, Any>()

                    // Check each field and add to the map if not null
                    name?.let { updates["name"] = it }
                    number?.let { updates["number"] = it }
                    ImageUrl?.let { updates["imageUrl"] = it }

                    if (updates.isNotEmpty()) {
                        // If there are fields to update, perform the update
                        db.collection(USER_NODE).document(uid)
                            .update(updates)
                            .addOnSuccessListener {
                                inProgress.value = false
                                getUserData(uid)     // updates current user data
                            }
                            .addOnFailureListener {
                                inProgress.value = false
                                handleException(it, "Failed to update user data")
                            }
                    } else {
                        // No changes, just complete the process
                        inProgress.value = false
                    }
                } else {
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
                }
            }

                .addOnFailureListener {
                    inProgress.value=false
                    handleException(it, "cannot retrieve User")
                }

        }

    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { value, error ->
            if (error != null) {
                handleException(error)
                inProgress.value=false
            }
            if (value != null) {
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
                populateChats()
                populateStatuses()
            }
        }

    }


    fun handleException(exception: Exception? = null, customMessage: String = "") {
        Log.e("CHITCHATAPP", "Chit Chat Exception ", exception)
        var errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value = Events(message)
        inProgress.value = false
    }

    fun uploadProfileImage(uri: Uri) {
        uploadImage(uri) {
            CreateOrUpdateUserProfile(ImageUrl = it.toString())
        }

    }

    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit) {

        inProgress.value = true
        inProgressStatus.value=true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("image/$uuid")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri)
                inProgress.value = false
                inProgressStatus.value=false
            }

        }.addOnFailureListener { exception ->
            Log.e("UploadImage", "Image upload failed", exception)
            inProgress.value = false
            inProgressStatus.value=false
            handleException(exception)
        }

    }

    fun logout() {
        auth.signOut()
        signup.value = false
        userData.value = null
        dePopulate()
        currentChatMessageListener = null
        eventMutableState.value = Events("Logged Out")
    }


    fun addChat(context: Context,number: String) {
        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number must contains only digits ")
            Toast.makeText(context,"Number must contains only digits ",Toast.LENGTH_SHORT).show()
        } else {

            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            ).get().addOnSuccessListener { querySnapShot ->
                if (querySnapShot.isEmpty) {
                    db.collection(USER_NODE).where(Filter.and(
                        Filter.equalTo("number", number),
                        Filter.notEqualTo("number", userData.value?.number))
                    )
                        .get()
                        .addOnSuccessListener { chatUser ->
                            if (chatUser.isEmpty) {
                                handleException(customMessage = "Number not found")
                                Toast.makeText(context,"Number not found",Toast.LENGTH_SHORT).show()
                            } else {
                                val chatPartners = chatUser.toObjects<UserData>()[0]
                                val id = db.collection(CHATS).document().id
                                val chat = ChatData(
                                    chatId = id,
                                    user1 = ChatUser(
                                        userID = userData.value?.userID,
                                        name = userData.value?.name,
                                        number = userData.value?.number,
                                        imageUrl = userData.value?.imageUrl
                                    ),
                                    user2 = ChatUser(
                                        userID = chatPartners.userID,
                                        name = chatPartners.name,
                                        number = chatPartners.number,
                                        imageUrl = chatPartners.imageUrl
                                    )
                                )
                                db.collection(CHATS).document(id).set(chat)
                            }
                        }
                        .addOnFailureListener {
                            handleException(it)
                        }

                } else {
                    handleException(customMessage = "Chat already exists")
                    Toast.makeText(context,"Chat already exists",Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    fun uploadStatus(context: Context, uri: Uri) {
        uploadImage(uri) {
            createStatus(it.toString())
            populateStatuses()
        }
        Toast.makeText(context,"Status Uploaded!" , Toast.LENGTH_SHORT).show()
    }

    fun createStatus(imageUrl: String) {
        val userStatus = Status(
            ChatUser(
                userData.value?.userID,
                userData.value?.name,
                userData.value?.number,
                userData.value?.imageUrl
            ),
            imageUrl,
            System.currentTimeMillis()
        )
        db.collection(STATUS).document().set(userStatus)
    }
}