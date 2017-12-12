package com.ladrope.venpix.controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.ladrope.venpix.R
import kotlinx.android.synthetic.main.activity_signin.*




class signin : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()
    }



    fun signInWithEmailAndPassword(view: View){
        var email = signinEnterEmail.toString()
        var password = signinEnterPassword.toString()
        mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            println("signInWithEmail:success")
                             //currentUser = mAuth?.currentUser
                             goHome()
                        } else {
                            // If sign in fails, display a message to the user.
                            println("signInWithEmail:failure")
                            Toast.makeText(this@signin, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }

                        // ...
                    }
                })
    }

    private fun goHome(){
       var homeIntent = Intent(this, home::class.java)
        startActivity(homeIntent)
    }
}
