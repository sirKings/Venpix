package com.ladrope.venpix.controller

import android.app.ProgressDialog
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
import com.ladrope.venpix.model.Album
import com.ladrope.venpix.utilities.NewAlbum
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
    var progressDialog: ProgressDialog? = null

    private var albumKey: String? = null
    private val uid = FirebaseAuth.getInstance().uid

    var textList = arrayListOf<String>("Never let great moments slide pass you","Create an album and share your link with friends", "Get all your pictures in one vault and only you and your friends can see it")
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Setting up your albums")
        progressDialog?.setCanceledOnTouchOutside(false)
        progressDialog?.show()


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

    override fun onStart() {
        super.onStart()


        Branch.getInstance().initSession(object : Branch.BranchReferralInitListener{

            override fun onInitFinished(referringParams: JSONObject, error: BranchError?) {
                if (error == null) {
                    Log.e("BRANCH SDK", referringParams.toString())
                    val branchStatus = referringParams.getBoolean("+clicked_branch_link")
                    if(branchStatus){
                        albumKey = referringParams.getString("\$canonical_identifier")
                        val userDisplayName = referringParams.getString("creatorName")
                        val title = referringParams.getString("\$og_title")
                        val desc = referringParams.getString("\$og_description")
                        val date = referringParams.getString("date")
                        val plan = referringParams.getString("plan")
                        val creatorId = referringParams.getString("creatorId")

                        val album = Album()
                        album.creatorName = userDisplayName
                        album.albumTitle = title
                        album.albumDesc = desc
                        album.event_date = date.toLong()
                        album.plan = plan.toInt()
                        album.albumKey = albumKey
                        album.creatorId = creatorId

                        NewAlbum = album
                        login()
                    }else{
                        login()
                    }

                } else {
                    Log.e("BRANCH SDK", error.message)
                    login()
                }
            }
        }, this.intent.data, this)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        this.intent = intent
    }

    fun login(){

        if(uid == null){

        }else{

            val homeIntent = Intent(applicationContext, home::class.java)
            startActivity(homeIntent)
            finish()
        }
        progressDialog?.dismiss()
    }

}
