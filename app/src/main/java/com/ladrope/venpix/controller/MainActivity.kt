package com.ladrope.venpix.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.FirebaseAuth
import com.ladrope.venpix.R
import com.twitter.sdk.android.core.Twitter
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var textSwitcher: TextSwitcher
    lateinit var dotsLayout: LinearLayout
    private val timer = Timer()

    private var mAuth: FirebaseAuth? = null

    var textList = arrayListOf<String>("Never let great moments slide pass you","Create an album and share your link with friends", "Get all your pictures in one vault and only you and your friends can see it")
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mAuth = FirebaseAuth.getInstance()

        if(mAuth?.currentUser == null){

        }else{
            var homeIntent = Intent(this, home::class.java)
            startActivity(homeIntent)
            finish()
        }

        //facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)

        //Twitter initialization
        Twitter.initialize(this)


        textSwitcher = hintSwitcher
        dotsLayout = layoutDots


        setFactory()
        loadAnimations()
        startTimer()


    }


    protected fun startTimer() {

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(index < textList.size - 1){
                    ++index
                }else{
                    index = 0
                }
                mHandler.obtainMessage(1).sendToTarget()
            }
        }, 0, 3000)
    }

    val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            textSwitcher.setText(textList[index])
            addBottomDots(index)
        }
    }

    fun signInClicked (view: View){
        var signInIntent = Intent(this@MainActivity, signin::class.java)
        startActivity(signInIntent)
    }

    fun signUpClicked (view: View){
        var signInIntent = Intent(this@MainActivity, signup::class.java)
        startActivity(signInIntent)
    }


    fun loadAnimations() {
      var  inAnimate = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
      var  outAnimate = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        textSwitcher.setInAnimation(inAnimate)
        textSwitcher.setOutAnimation(outAnimate)
    }

    fun setFactory() {
        textSwitcher.setFactory(object : ViewSwitcher.ViewFactory {

            override fun makeView(): View {

                // Create run time textView with some attributes like gravity,
                // color, etc.
                val myText = TextView(this@MainActivity)
                myText.gravity = Gravity.CENTER
                myText.textSize = 24f
                myText.setTextColor(Color.rgb(0,187,209))
                return myText
            }
        })

    }

    private fun addBottomDots(index: Int) {
        var dots = arrayOfNulls<TextView>(textList.size)

        dotsLayout.removeAllViews()
        for (i in 0 until textList.size) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(Color.rgb(187,187,187))
            dotsLayout.addView(dots[i])
        }
        dots[index]?.setTextColor(Color.rgb(0,187,209))
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
        timer.purge()
    }

    public override fun onStart() {
        super.onStart()

        Branch.getInstance().initSession(object : Branch.BranchReferralInitListener{
            override fun onInitFinished(referringParams: JSONObject, error: BranchError?) {
                if (error == null) {
                    Log.e("BRANCH SDK", referringParams.toString())
                } else {
                    Log.e("BRANCH SDK", error.message)
                }
            }
        }, this.intent.data, this)
    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
    }

}
