package io.github.collins993.learnersguide.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.github.collins993.learnersguide.model.SuggestedCourses
import io.github.collins993.learnersguide.model.Users
import io.github.collins993.learnersguide.utils.Resource
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class FirebaseViewModel(app: Application) : AndroidViewModel(app) {
    private var auth: FirebaseAuth? = null
    private var firebaseUserId: String = ""
    private var storage: StorageReference? = null

    //
    private val _registrationStatus = MutableLiveData<Resource<String>>()
    val registrationStatus: LiveData<Resource<String>> = _registrationStatus

    //
    private val _loginStatus = MutableLiveData<Resource<String>>()
    val loginStatus: LiveData<Resource<String>> = _loginStatus

    //
    private val _resetPasswordStatus = MutableLiveData<Resource<String>>()
    val resetPasswordStatus: LiveData<Resource<String>> = _resetPasswordStatus

    //
    private val _downloadUrlStatus = MutableLiveData<Resource<String>>()
    val downloadUrlStatus: LiveData<Resource<String>> = _downloadUrlStatus

    //
    private val _addUserStatus = MutableLiveData<Resource<String>>()
    val addUserStatus: LiveData<Resource<String>> = _addUserStatus

    //
    private val _getUserStatus = MutableLiveData<Resource<Users>>()
    val getUserStatus: LiveData<Resource<Users>> = _getUserStatus

    //
    private val _addSuggestionStatus = MutableLiveData<Resource<String>>()
    val addSuggestionStatus: LiveData<Resource<String>> = _addSuggestionStatus

    //
    private val _getAllSuggestionStatus = MutableLiveData<Resource<List<SuggestedCourses>>>()
    val getAllSuggestionStatus: LiveData<Resource<List<SuggestedCourses>>> = _getAllSuggestionStatus

    //
    private val _getUserSuggestionStatus = MutableLiveData<Resource<List<SuggestedCourses>>>()
    val getUserSuggestionStatus: LiveData<Resource<List<SuggestedCourses>>> =
        _getUserSuggestionStatus

    //
    private val _deleteStatus = MutableLiveData<Resource<String>>()
    val deleteStatus: LiveData<Resource<String>> = _deleteStatus

    init {
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance().reference.child("user_image")
    }

    fun signUp(email: String, password: String) {
        var errorCode = -1
        viewModelScope.launch {

            _registrationStatus.postValue(Resource.Loading())

            try {

                auth?.let { authentication ->
                    authentication.createUserWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _registrationStatus.postValue(
                                Resource.Success(
                                    "Registration failed with ${task.exception}",
                                )
                            )
                        } else {
                            _registrationStatus.postValue(
                                Resource.Success("User created!")
                            )
                        }

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()

                if (errorCode == -1) {
                    _registrationStatus
                        .postValue(
                            Resource.Error(
                                "Failed with Error code $errorCode"
                            )
                        )
                } else {
                    Resource.Error(
                        "Failed with Exception ${e.message} ",
                        e.toString()
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        var errorCode = -1
        viewModelScope.launch {

            _loginStatus.postValue(Resource.Loading())

            try {

                auth?.let { authentication ->
                    authentication.signInWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _loginStatus.postValue(
                                Resource.Success(
                                    "Registration failed with ${task.exception}",
                                )
                            )
                        } else {
                            _loginStatus.postValue(
                                Resource.Success("User Logged In!")
                            )
                        }

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()

                if (errorCode == -1) {
                    _loginStatus
                        .postValue(
                            Resource.Error(
                                "Failed with Error code $errorCode"
                            )
                        )
                } else {
                    Resource.Error(
                        "Failed with Exception ${e.message} ",
                        e.toString()
                    )
                }
            }
        }
    }

    fun signOut() {

        viewModelScope.launch {
            var errorCode = -1
            try {
                auth?.let { authentation ->
                    authentation.signOut()

                }

            } catch (e: Exception) {
                e.printStackTrace()


            }
        }
    }

    fun resetPassword(email: String) {
        var errorCode = -1
        viewModelScope.launch {

            _resetPasswordStatus.postValue(Resource.Loading())

            try {

                auth?.let { authentication ->
                    authentication.sendPasswordResetEmail(
                        email
                    ).addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            _resetPasswordStatus.postValue(
                                Resource.Success(
                                    "Registration failed with ${task.exception}",
                                )
                            )
                        } else {
                            _resetPasswordStatus.postValue(
                                Resource.Success("Password Successfully changed")
                            )
                        }

                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()

                if (errorCode == -1) {
                    _resetPasswordStatus
                        .postValue(
                            Resource.Error(
                                "Failed with Error code $errorCode"
                            )
                        )
                } else {
                    Resource.Error(
                        "Failed with Exception ${e.message} ",
                        e.toString()
                    )
                }
            }
        }
    }

    fun uploadToStorage(image: Uri) {
        var errorCode = -1
        viewModelScope.launch {

            _downloadUrlStatus.postValue(Resource.Loading())
            try {
                val fileReference = storage?.child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask = fileReference?.putFile(image)

                uploadTask?.continueWithTask { task ->
                    if (!task.isSuccessful) {

                        task.exception?.let {
                            throw it
                        }
                    }
                    return@continueWithTask fileReference.downloadUrl

                }?.addOnCompleteListener { task1 ->

                    if (task1.isSuccessful) {
                        val downloadUrl = task1.result
                        _downloadUrlStatus.postValue(Resource.Success(downloadUrl.toString()))
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()

                if (errorCode == -1) {
                    _downloadUrlStatus
                        .postValue(
                            Resource.Error(
                                "Failed with Error code $errorCode"
                            )
                        )
                } else {
                    Resource.Error(
                        "Failed with Exception ${e.message} ",
                        e.toString()
                    )
                }
            }
        }
    }

    fun addUserToFirestore(user: Users) {
        var errorCode = -1
        viewModelScope.launch {
            _addUserStatus.postValue(Resource.Loading())

            try {
                firebaseUserId = auth?.currentUser?.uid.toString()
                Firebase.firestore.collection("users")
                    .document(firebaseUserId).set(user)

                _addUserStatus.postValue(Resource.Success("User added Successfully"))


            } catch (e: Exception) {
                e.printStackTrace()
                if (errorCode != -1) {
                    _addUserStatus.postValue(
                        Resource.Error(
                            "Failed with Error code $errorCode",
                            e.toString()
                        )
                    )
                } else {
                    _addUserStatus.postValue(
                        Resource.Error(
                            "Failed with exception ${e.message}",
                            e.toString()
                        )
                    )
                }
            }
        }
    }

    fun updateUserInfo(user: Users) {

    }

    fun getUserFromFirestore() {
        viewModelScope.launch {
            var errorCode = -1
            try {
                firebaseUserId = auth?.currentUser?.uid.toString()
                Firebase.firestore.collection("users")
                    .document(firebaseUserId).addSnapshotListener { value, error ->
                        val user = value?.toObject<Users>()

                        if (user != null) {
                            _getUserStatus.postValue(Resource.Success(user))
                        }
                    }


            } catch (e: Exception) {

                e.printStackTrace()

                if (errorCode != -1) {
                    _getUserStatus.postValue(
                        Resource.Error(
                            "Failed with Error code $errorCode",
                            null
                        )
                    )
                } else {
                    _getUserStatus.postValue(
                        Resource.Error(
                            "Failed with exception ${e.message}",
                            null
                        )
                    )
                }
            }
        }
    }

    fun deleteSuggestion(suggestedCourses: SuggestedCourses) {
        viewModelScope.launch {
            var errorCode = -1


            try {

                Firebase.firestore.collection("suggested_courses")
                    .whereEqualTo("title", suggestedCourses.title)
                    .whereEqualTo("url", suggestedCourses.url)
                    .whereEqualTo("username", suggestedCourses.username)
                    .whereEqualTo("firstname", suggestedCourses.firstname)
                    .whereEqualTo("lastname", suggestedCourses.lastname)
                    .whereEqualTo("img", suggestedCourses.img)
                    .whereEqualTo("uid", suggestedCourses.uid)
                    .whereEqualTo("date", suggestedCourses.date).get().addOnSuccessListener {


                        for (document in it) {
                            Firebase.firestore.collection("suggested_courses")
                                .document(document.id).delete()
                        }

                        _deleteStatus.postValue(Resource.Success("Course Deleted"))

                    }


            } catch (e: Exception) {
                if (errorCode != -1) {
                    _deleteStatus.postValue(
                        Resource.Error(
                            "Failed with Error Code ${errorCode} ",
                            e.toString()
                        )
                    )
                } else {
                    _deleteStatus.postValue(
                        Resource.Error(
                            "Failed with Exception ${e.message} ",
                            e.toString()
                        )
                    )
                }
            }
        }
    }

    fun addSuggestion(suggestedCourses: SuggestedCourses) {

        viewModelScope.launch {

            var errorCode = -1

            try {


                Firebase.firestore.collection("suggested_courses").add(suggestedCourses)
                    .addOnSuccessListener { documentReference ->
                        _addSuggestionStatus.postValue(Resource.Success("Course Suggested"))

                    }.addOnFailureListener { e ->

                        _addSuggestionStatus.postValue(Resource.Error("Failed", e.toString()))
                    }


            } catch (e: Exception) {
                if (errorCode != -1) {
                    _addSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with Error Code ${errorCode} ",
                            e.toString()
                        )
                    )
                } else {
                    _addSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with Exception ${e.message} ",
                            e.toString()
                        )
                    )
                }
            }
        }
    }

    fun getAllCourseSuggestion() {

        viewModelScope.launch {

            var errorCode = -1

            try {

                Firebase.firestore.collection("suggested_courses").get()
                    .addOnSuccessListener { documents ->

                        val suggestedCourseList = documents.toObjects(SuggestedCourses::class.java)
                        _getAllSuggestionStatus.postValue(Resource.Success(suggestedCourseList))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                if (errorCode != -1) {
                    _getAllSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with Error code $errorCode",
                            null
                        )
                    )
                } else {
                    _getAllSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with exception ${e.message}",
                            null
                        )
                    )
                }
            }
        }
    }

    fun getUsersSuggestions() {
        viewModelScope.launch {

            var errorCode = -1

            try {

                firebaseUserId = auth?.currentUser?.uid.toString()

                Firebase.firestore.collection("suggested_courses")
                    .whereEqualTo("uid", firebaseUserId)
                    .get()
                    .addOnSuccessListener { documents ->

                        val suggestedCourseList = documents.toObjects(SuggestedCourses::class.java)
                        _getUserSuggestionStatus.postValue(Resource.Success(suggestedCourseList))
                    }

            } catch (e: Exception) {
                e.printStackTrace()
                if (errorCode != -1) {
                    _getUserSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with Error code $errorCode",
                            null
                        )
                    )
                } else {
                    _getUserSuggestionStatus.postValue(
                        Resource.Error(
                            "Failed with exception ${e.message}",
                            null
                        )
                    )
                }
            }
        }
    }


}