package com.ladrope.venpix.controller

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ladrope.venpix.R
import com.ladrope.venpix.model.Album
import com.ladrope.venpix.services.addAlbum
import com.ladrope.venpix.services.addAlbumLink
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import kotlinx.android.synthetic.main.activity_create_album.*
import kotlinx.android.synthetic.main.ca_add_choose_plan.*
import kotlinx.android.synthetic.main.ca_add_description.*
import kotlinx.android.synthetic.main.ca_add_title.*
import kotlinx.android.synthetic.main.fragment_fragment_four.*
import java.util.*


class create_album: AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var next: Button
    var index = 0
    private val mFragmentList = arrayListOf<Fragment>()
    lateinit var dotsLayout: LinearLayout

    var plan: Int = 1
    val buo = BranchUniversalObject()

    private var mAuth: FirebaseAuth? = null
    private var uid: String? = null
    private var title: String? = null
    private var desc: String? = null
    private var date: Long? = null
    private var albumKey: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        viewPager = view_pager
        val adapter = ViewPagerAdapter(getSupportFragmentManager())
        adapter.addFragment(FragmentOne())
        adapter.addFragment(FragmentTwo())
        adapter.addFragment(FragmentFour())
        adapter.addFragment(FragmentThree())

        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter

        dotsLayout = layoutDots
        addBottomDots(index)
        next = btn_next

        mAuth = FirebaseAuth.getInstance()
        uid = mAuth?.uid

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

        }else if(index == 3) {
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
        if (index == 3){
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

    fun pickDate(view: View){

        // Get Current Date
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this,
                object: DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view:DatePicker, year:Int,
                                  monthOfYear:Int, dayOfMonth:Int) {
                        dateText.text = dayOfMonth.toString() + "-" + (monthOfYear + 1).toString() + "-" + year.toString()
                        val calendar = GregorianCalendar(year, monthOfYear+1, dayOfMonth)
                        date = calendar.timeInMillis
                    }
                }, mYear, mMonth, mDay)
        datePickerDialog.show()

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
            dots[currentPage]?.setTextColor(resources.getColor(R.color.colorAccent))
    }

    fun createOrder() {
        title = album_name.text.toString()
        desc = albumDescription.text.toString()

        if(title == ""){

            Toast.makeText(this@create_album, getString(R.string.titleValidation), Toast.LENGTH_SHORT).show()
            index = 0
            viewPager.currentItem = index
            addBottomDots(index)
            next.text = getString(R.string.next)
        }else{
            if (desc == ""){

                Toast.makeText(this@create_album, getString(R.string.descValidation), Toast.LENGTH_SHORT).show()
                index = 1
                viewPager.currentItem = index
                addBottomDots(index)
                next.text = getString(R.string.next)
            }else {

                if (date == null){
                    Toast.makeText(this@create_album, getString(R.string.dateValidation), Toast.LENGTH_SHORT).show()
                    index = 2
                    viewPager.currentItem = index
                    addBottomDots(index)
                    next.text = getString(R.string.next)

                }else{
                    //purchase a store value

                    //create an album
                    createAlbum()
                }
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

    fun createLink(key: String) {
        val title: String = title.toString()
        val desc: String = desc.toString()
        val date: String = date.toString()
        val plan: String = plan.toString()
        val creatorName: String = mAuth!!.currentUser!!.displayName!!
        val creatorId: String = mAuth!!.uid!!

        buo.setCanonicalIdentifier(key)
                .setTitle(title)
                .setContentDescription(desc)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .setContentMetadata(ContentMetadata().addCustomMetadata("creatorName", creatorName))

        val lp = LinkProperties()
        lp.setChannel("facebook")
                .setFeature("sharing")
                .setCampaign("content 123 launch")
                .setStage("new user")
                .addControlParameter("plan",plan)
                .addControlParameter("date", date)
                .addControlParameter("creatorId", creatorId)

        buo.generateShortUrl(this, lp, Branch.BranchLinkCreateListener { url, error ->
            if (error == null) {
                addAlbumLink(albumKey, uid, url)
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/*"
                share.putExtra(Intent.EXTRA_TEXT, url)
                startActivity(Intent.createChooser(share, "Share Link"))
            }
        })
    }

    fun createAlbum(){

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Creating your Link")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()

        val userDisplayName = mAuth?.currentUser?.displayName

        val album = Album()
        album.creatorName = userDisplayName
        album.albumTitle = title!!
        album.albumDesc = desc!!
        album.creatorId = uid!!
        album.created_at = Calendar.getInstance().timeInMillis
        album.event_date = date!!
        album.plan = plan

        val albumRef = FirebaseDatabase.getInstance().reference.child("albums")

        val key = albumRef.push().key

        albumKey = key

        albumRef.child(key).setValue(album).addOnCompleteListener {
            task: Task<Void> ->
            if(task.isSuccessful){
                createLink(key)
                album.albumKey = key
                addAlbum(uid!!, album)
                progressDialog.dismiss()
            }
        }
    }
}
