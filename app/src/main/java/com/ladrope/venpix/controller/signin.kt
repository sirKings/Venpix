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
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_signin.*


class signin : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null

    //google sign in client
    private var mGoogleSignInClient: GoogleSignInClient? = null

    //facebook sign in manager
    private var mCallbackManager: CallbackManager? = null
    private var mlogin: LoginButton? = null

    //twitter login
    private var mLoginButton: TwitterLoginButton? = null

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

        mCallbackManager = CallbackManager.Factory.create()

        mlogin = login_button2
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
                        Toast.makeText(this@signin, loginCancel, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        val error: Int = resources.getIdentifier("error", "string", packageName)
                        Toast.makeText(this@signin, error, Toast.LENGTH_SHORT).show()
                    }
                })

        //twitter login manager
        mLoginButton = login_button_twitter

        mLoginButton?.callback = (object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {

                firebaseAuthWithTwitter(result.data)
            }

            override fun failure(exception: TwitterException) {
                // Do something on failure
                val error: Int = resources.getIdentifier("error", "string", packageName)
                Toast.makeText(this@signin, error, Toast.LENGTH_SHORT).show()
            }
        })
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

        // facebook callbackmanager
        mCallbackManager?.onActivityResult(requestCode, resultCode, data)

        //twitter callbackmanager
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

    // facebook signin
    fun signInWithFacebook(view: View){
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


    fun signInWithTwitter(view: View){
        mLoginButton?.performClick()
    }

    private fun firebaseAuthWithTwitter(session: TwitterSession){

        startLogin(false)

        println("token"+ session.authToken.token)
        println("secret"+ session.authToken.secret)

        val credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret)

        println(credential)
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
