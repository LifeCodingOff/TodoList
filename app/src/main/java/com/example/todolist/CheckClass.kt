package com.example.todolist

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CheckClass : MultiDexApplication() {
    companion object {
        lateinit var auth : FirebaseAuth
        var db : FirebaseFirestore? = null
        var email : String? = null

        fun checkAuth() : Boolean {


            val currentUser = auth.currentUser

            return currentUser?.let {
                email = currentUser.email
                if(currentUser.isEmailVerified) {
                    true
                }
                else{
                    false
                }

            } ?: let {
                false
            }
        }
    }

    override fun onCreate(){
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        auth = Firebase.auth
    }
}