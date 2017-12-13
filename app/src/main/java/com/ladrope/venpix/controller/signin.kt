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
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.RC_SIGN_IN
import kotlinx.android.synthetic.main.activity_signin.*




class signin : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        mAuth = FirebaseAuth.getInstance()

        progressBar = this.progressBar1
        progressBar?.visibility = View.GONE

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    fun signInWithEmailAndPassword(view: View){
        startLogin(false)
        var email = signinEnterEmail.text.toString()
        var password = signinEnterPassword.text.toString()
        mAuth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            println("signInWithEmail:success")
                             //currentUser = mAuth?.currentUser
                            startLogin(true)
                             goHome()
                        } else {
                            // If sign in fails, display a message to the user.
                            println("signInWithEmail:failure")
                            startLogin(true)
                            Toast.makeText(this@signin, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                            //updateUI(null)
                        }

                        // ...
                    }
                })
    }

    private fun startLogin(status: Boolean){
        val signinBtn = signinSigninBtn
        val twitterBtn = twitterIcon
        val googleBtn = googleplusIcon
        val facebookBtn = facebookIcon

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

    private fun goHome(){
       var homeIntent = Intent(this, home::class.java)
        startActivity(homeIntent)
    }


    //google signin code
    fun signInWithGoogle(view: View) {

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
