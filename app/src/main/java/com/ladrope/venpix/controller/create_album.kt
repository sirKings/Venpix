package com.ladrope.venpix.controller

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.ladrope.venpix.R
import com.ladrope.venpix.services.Album
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.ca_add_choose_plan.*
import kotlinx.android.synthetic.main.ca_add_description.*
import kotlinx.android.synthetic.main.ca_add_title.*


class create_album: AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var next: Button
    var index = 0
    private val mFragmentList = arrayListOf<Fragment>()
    lateinit var dotsLayout: LinearLayout

    var plan: Int = 1
    val buo = BranchUniversalObject()

    private var mAuth: FirebaseAuth? = null

    private var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        viewPager = view_pager
        val adapter = ViewPagerAdapter(getSupportFragmentManager())
        adapter.addFragment(FragmentOne())
        adapter.addFragment(FragmentTwo())
        adapter.addFragment(FragmentThree())
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter

        dotsLayout = layoutDots
        addBottomDots(index)
        next = btn_next

        mAuth = FirebaseAuth.getInstance()

        progressBar = progressBar3
        progressBar?.visibility = View.GONE
    }

    internal inner class ViewPagerAdapter(manager:FragmentManager): FragmentPagerAdapter(manager) {

        override fun getCount(): Int {
            return mFragmentList.size
        }
        override fun getItem(position:Int):Fragment {
            return mFragmentList.get(position)
        }
        fun addFragment(fragment:Fragment) {
            mFragmentList.add(fragment)
        }

    }

    fun prev (view: View){
        Log.e("index", index.toString())
        if (index <= 0){

        }else if(index == 2) {
            next.text = getString(R.string.next)
            viewPager.currentItem = --index
            addBottomDots(index)
        }else {
            viewPager.currentItem = --index
            addBottomDots(index)
        }

    }

    fun next (view: View) {
        Log.e("index", index.toString())
        if (index == 2){
            createOrder()
        }else if(index > mFragmentList.size - 2){
        }
        else if(index == mFragmentList.size -2){
            next.text = getString(R.string.create)
            viewPager.currentItem = ++index
            addBottomDots(index)
        }
        else{
            viewPager.currentItem = ++index
            addBottomDots(index)
        }

    }

    private fun addBottomDots(currentPage:Int) {
        val dots = arrayOfNulls<TextView>(mFragmentList.size)

        dotsLayout.removeAllViews()
        for (i in 0 until mFragmentList.size)
        {
            dots[i] = TextView(this)
            dots[i]?.text =Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(Color.DKGRAY)
            dotsLayout.addView(dots[i])
        }
        if (mFragmentList.size > 0)
            dots[currentPage]?.setTextColor(Color.parseColor("#00bbd1"))
    }

    fun createOrder() {
        val title = album_name.text.toString()
        val desc = albumDescription.text.toString()

        if(title == ""){
            val titleValidation: Int = resources.getIdentifier("titleValidation", "string", packageName)
            Toast.makeText(this@create_album, titleValidation, Toast.LENGTH_SHORT).show()
            index = 0
            viewPager.currentItem = index
            addBottomDots(index)
            next.text = getString(R.string.next)
        }else{
            if (desc == ""){
                val descValidation: Int = resources.getIdentifier("descValidation", "string", packageName)
                Toast.makeText(this@create_album, descValidation, Toast.LENGTH_SHORT).show()
                index = 1
                viewPager.currentItem = index
                addBottomDots(index)
                next.text = getString(R.string.next)
            }else {
                //purchase a store value

                //create an album
                startLogin(false)
                val uid = mAuth?.uid
                val albumKey = createAlbum(title, desc, uid)

                //create Link
                createLink(title, desc, plan, albumKey)
            }
        }
    }

    fun freePlanSelected(view: View){
        plan = 1
        clearCards()
        val freeCard = free_card
        freeCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
    }

    fun proPlanSelected(view: View){
        plan = 2
        clearCards()
        val proCard = pro_card
        proCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
    }

    fun plusPlanSelected(view: View){
        plan = 3
        clearCards()
        val plusCard = plus_card
        plusCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
    }

    fun premuimPlanSelected(view: View){
        plan = 4
        clearCards()
        val premuimCard = premuim_card
        premuimCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
    }

    fun clearCards(){
        val freeCard = free_card
        val proCard = pro_card
        val plusCard = plus_card
        val premuimCard = premuim_card
        freeCard.setCardBackgroundColor(Color.WHITE)
        proCard.setCardBackgroundColor(Color.WHITE)
        plusCard.setCardBackgroundColor(Color.WHITE)
        premuimCard.setCardBackgroundColor(Color.WHITE)
    }

    fun createLink(title: String, desc: String, plan: Int?, key: String){
        buo.setCanonicalIdentifier(key)
                .setTitle(title)
                .setContentDescription(desc)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata().addCustomMetadata("plan", plan?.toString()))

        val lp = LinkProperties()
                lp.setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("custom_random", "hello")


        var ss =  ShareSheetStyle(this@create_album, "Check this out!", "This stuff is awesome: ")
            ss.setCopyUrlStyle(resources.getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
                .setMoreOptionStyle(resources.getDrawable(android.R.drawable.ic_menu_search), "Show more")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
                    .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                    .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK_MESSENGER)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With")

        buo.showShareSheet(this, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(sharedLink: String, sharedChannel: String, error: BranchError) {}
            override fun onChannelSelected(channelName: String) {}
        })
    }

    fun createAlbum(title: String, desc: String, uid: String?): String{
        val album = Album(title, desc, uid)
        val albumKey = com.ladrope.venpix.services.createAlbum(album)
        startLogin(true)
        return albumKey
    }

    private fun startLogin(status: Boolean){

        if(status == false){
            progressBar?.visibility = View.VISIBLE
        }else {
            progressBar?.visibility = View.GONE
        }

        next.isClickable = status
        val prev = btn_skip
        prev.isClickable = status
    }
}
