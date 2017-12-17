package com.ladrope.venpix.controller

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.RC_SIGN_IN
import com.ladrope.venpix.utilities.isValidEmail
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_signup.*


class signup : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null

    // google signin
    private var mGoogleSignInClient: GoogleSignInClient? = null

    // facebook signin
    private var mCallbackManager: CallbackManager? = null
    private var mlogin: LoginButton? = null

    //twitter login
    private var mLoginButton: TwitterLoginButton? = null

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

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


        //facebook login manager
        mCallbackManager = CallbackManager.Factory.create()
        mlogin = login_button
        mlogin?.setReadPermissions("email", "public_profile")
        LoginManager.getInstance().registerCallback(mCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code
                        firebaseAuthWithFacebook(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        // App code
                        val loginCancel: Int = resources.getIdentifier("LoginCancel", "string", packageName)
                        Toast.makeText(this@signup, loginCancel, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        val error: Int = resources.getIdentifier("error", "string", packageName)
                        Toast.makeText(this@signup, error, Toast.LENGTH_SHORT).show()
                    }
                })

        //twitter login manager
        mLoginButton = login_button_twitter2

        mLoginButton?.callback = (object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {

                firebaseAuthWithTwitter(result.data)
            }

            override fun failure(exception: TwitterException) {
                // Do something on failure
            }
        })

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
                val error: Int = resources.getIdentifier("error", "string", packageName)
                Toast.makeText(this@signup, error, Toast.LENGTH_SHORT).show()

            }

        }
        // facebook callback manager
        mCallbackManager?.onActivityResult(requestCode, resultCode, data)

        //twitter calback manager
        mLoginButton?.onActivityResult(requestCode, resultCode, data)
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
                        val error: Int = resources.getIdentifier("error", "string", packageName)
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }

                    // ...
                }
    }

    // facebook Login

    fun signUpWithFacebook(view: View){
        mlogin?.performClick()
    }


    private fun firebaseAuthWithFacebook(token: AccessToken) {
        println("firebaseAuthWithFacebook:" + token)
        startLogin(false)
        val credential = FacebookAuthProvider.getCredential(token.token)
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


    // twitter login
    fun signUpWithTwitter(view: View){
        mLoginButton?.performClick()
    }

    private fun firebaseAuthWithTwitter(session: TwitterSession){
        val credential = TwitterAuthProvider.getCredential(session.authToken.token, session.authToken.secret)
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
