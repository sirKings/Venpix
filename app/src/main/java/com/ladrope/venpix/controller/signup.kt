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
import com.google.firebase.auth.UserProfileChangeRequest
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.isValidEmail
import kotlinx.android.synthetic.main.activity_signup.*




class signup : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        progressBar = this.progressBar2
        progressBar?.visibility = View.GONE
//        val drawable = resources.getDrawable(drawableID / mipmapID)
//        var emailDrawable = DrawableCompat.wrap(email)
//        DrawableCompat.setTint(emailDrawable, resources.getColor(R.color.colorAccent))
//        DrawableCompat.setTintMode(emailDrawable, PorterDuff.Mode.SRC_IN)
//        editText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }


    fun signUpWithEmailAndPassword(view: View){

        val email = signupEnterEmail.text.toString()
        val password = signupEnterPassword.text.toString()
        val passwordComfirmation = signupConfirmPassword.text.toString()
        val name = signupEnterName.text.toString()

        if(isValidEmail(email) && password.length > 6 ) {
            if(passwordComfirmation == password){
                progressBar?.visibility = View.VISIBLE
                mAuth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                            override fun onComplete(task: Task<AuthResult>) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    println("signUpWithEmail:success")
                                    var currentUser = mAuth?.currentUser
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build()

                                    currentUser?.updateProfile(profileUpdates)
                                    progressBar?.visibility = View.GONE
                                    goHome()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    println("signInWithEmail:failure")
                                    progressBar?.visibility = View.GONE
                                    Toast.makeText(this@signup, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show()
                                    //updateUI(null)
                                }

                                // ...
                            }
                        }
                )
            } else{
                val passwordMatchResource: Int = resources.getIdentifier("passwordMatch", "string", packageName)
                Toast.makeText(this@signup, passwordMatchResource, Toast.LENGTH_SHORT).show()
            }
        } else {
            val invalidEmailResource: Int = resources.getIdentifier("invalidEmail","string", packageName)
            Toast.makeText(this@signup, invalidEmailResource, Toast.LENGTH_SHORT).show()
        }
    }


    private fun goHome(){
        var homeIntent = Intent(this, home::class.java)
        startActivity(homeIntent)
    }

}
