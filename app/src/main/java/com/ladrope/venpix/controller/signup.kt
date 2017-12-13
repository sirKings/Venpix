package com.ladrope.venpix.controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.RC_SIGN_IN
import com.ladrope.venpix.utilities.isValidEmail
import kotlinx.android.synthetic.main.activity_signup.*




class signup : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mAuth = FirebaseAuth.getInstance()
        progressBar = this.progressBar2
        progressBar?.visibility = View.GONE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
                startLogin(false)
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
                                    startLogin(true)
                                    goHome()
                                } else {
                                    // If sign in fails, display a message to the user.
                                    println("signInWithEmail:failure")
                                    startLogin(true)
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

    private fun startLogin(status: Boolean){
        val signinBtn = signupSignupBtn
        val twitterBtn = twitterIcon2
        val googleBtn = googleplusIcon2
        val facebookBtn = facebookIcon2

        if(status == false){
            progressBar?.visibility = View.VISIBLE
        }else {
            progressBar?.visibility = View.GONE
        }

        signinBtn.isClickable = status
        twitterBtn.isClickable = status
        googleBtn.isClickable = status
        facebookBtn.isClickable = status
    }


    // signup with google
    fun signUpWithGoogle(view: View) {

        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                println("Google sign in failed")
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        println("firebaseAuthWithGoogle:" + acct.id!!)
        startLogin(false)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithCredential:success")
                        startLogin(true)
                        val user = mAuth?.getCurrentUser()
                        //updateUI(user)
                        goHome()
                    } else {
                        // If sign in fails, display a message to the user.
                        println("signInWithCredential:failure")
                        startLogin(true)
                        Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }

                    // ...
                }
    }

}
