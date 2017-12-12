package com.ladrope.venpix.controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.ladrope.venpix.R
import kotlinx.android.synthetic.main.activity_signin.*






class signin : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()

        progressBar = this.progressBar1
        progressBar?.visibility = View.GONE
    }



    fun signInWithEmailAndPassword(view: View){
        progressBar?.visibility = View.VISIBLE
        var email = signinEnterEmail.text.toString()
        var password = signinEnterPassword.text.toString()
        mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            println("signInWithEmail:success")
                             //currentUser = mAuth?.currentUser
                            progressBar?.visibility = View.GONE
                             goHome()
                        } else {
                            // If sign in fails, display a message to the user.
                            println("signInWithEmail:failure")
                            progressBar?.visibility = View.GONE
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
