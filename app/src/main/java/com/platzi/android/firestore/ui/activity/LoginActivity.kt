package com.platzi.android.firestore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RemoteViewsService
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.platzi.android.firestore.R
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.USERS_COLLECTION_NAME
import java.lang.Exception

/**
 * @author Santiago Carrillo
 * github sancarbar
 * 1/29/19.
 */


const val USERNAME_KEY = "username_key"

class LoginActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore


    private lateinit var auth: FirebaseAuth

    lateinit var etLoginUsername: EditText

    lateinit var firestoreService: FirestoreService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etLoginUsername = findViewById(R.id.etLoginUsername)
        firestoreService = FirestoreService(Firebase.firestore)
        auth = Firebase.auth

    }


    fun onStartClicked(view: View) {
        view.isEnabled = false
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("signIn", "signInAnonymously:success")
                    val firebaseUser = auth.currentUser
                    val user = User(name = etLoginUsername.text.toString())
                    saveUserAndStartMainActivity(firebaseUser, user, view)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("signIn", "signInAnonymously:failure", task.exception)
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            }

    }

    private fun showErrorMessage(view: View) {
        Snackbar.make(view, "Authentication failed.", Snackbar.LENGTH_LONG)
            .setAction("Info", null).show()
    }

    private fun saveUserAndStartMainActivity(firebaseUser: FirebaseUser?, user: User, view: View) {
        user.name?.let {
            firestoreService.setDocument(user, USERS_COLLECTION_NAME, it, object : Callback<Void>{
                override fun onSuccess(result: Void?) {
                    Log.i("saveUser", "User saved")
                    startMainActivity(firebaseUser, user.name ?: "unnamed")
                }

                override fun onFailed(exception: Exception) {
                    exception.message?.let { error -> Log.e("saveUserError", error) }
                    showErrorMessage(view)
                    view.isEnabled = true
                }
            })
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        startMainActivity(currentUser, etLoginUsername.text.toString())
    }

    private fun startMainActivity(currentUser: FirebaseUser?, username: String) {
        if (currentUser != null){
            val intent = Intent(this@LoginActivity, TraderActivity::class.java)
            intent.putExtra(currentUser.uid, username)
            startActivity(intent)
            finish()
        }
    }

}
