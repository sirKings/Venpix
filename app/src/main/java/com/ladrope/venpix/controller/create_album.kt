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
import com.ladrope.venpix.util.IabHelper
import com.ladrope.venpix.util.IabResult
import com.ladrope.venpix.util.Inventory
import com.ladrope.venpix.util.Purchase
import com.ladrope.venpix.utilities.PURCHASE_REQUEST
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
import kotlin.collections.ArrayList


class create_album: AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var next: Button
    var index = 0
    private val mFragmentList = arrayListOf<Fragment>()
    lateinit var dotsLayout: LinearLayout

    private var IAPkey = ""

    var plan: Int = 1
    val buo = BranchUniversalObject()
    var ITEM_SKU = ""

    private var mAuth: FirebaseAuth? = null
    private var uid: String? = null
    private var title: String? = null
    private var desc: String? = null
    private var date: Long? = null
    private var albumKey: String? = null

    private var mHelper: com.ladrope.venpix.util.IabHelper? = null


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

        IAPkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0k7jlVCRGBJAelMU0E5FM+bYCq7s/aiVf41JSOhRmJ3QIdfQD8wsDX5HI5ZCAMLYU5C/M80WngOjdN/9C1s/1EFaWM0G5r4vUKDtplpOTNwSSL1N4X43U4qRSfYYXqonhvr8N44m5JDTHMJOxbtUhJUZ/a6zfI6Gkx14lgI+m0Yat6Mbyxs4mZfmR/zPhqo9XNyGVXSc7j7/UJaCQtKW9DysdoNv2VC9UwOC5o/T6+2FXxM2YqkSHdakFHRRqPGbp1ezkna1Rw1eMqn4fDK6b3Oky00DjMgZ5xshVGOyjVZBbsEGnGjBGOz/lfc8Uxd7u3kJzABFAZyEwyNEyVmUowIDAQAB"

        mHelper = IabHelper(this, IAPkey)

        mHelper?.enableDebugLogging(true)

        mHelper?.startSetup(object:IabHelper.OnIabSetupFinishedListener {
            override fun onIabSetupFinished(result: IabResult) {
                Log.d("mHelper", "Setup finished.")
                if (!result.isSuccess())
                {
                    // Oh noes, there was a problem.
                    Toast.makeText(this@create_album,
                            "Problem setting up in-app billing: " + result,
                            Toast.LENGTH_LONG).show()
                    return
                }
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null)
                    return
                // IAP is fully set up. Now, let's get an inventory of stuff we
                // own.
                Log.d("mHelper", "Setup successful. Querying inventory.")
                val list = ArrayList<String>()
                list.add("premuim")
                list.add("plus")
                list.add("pro")
                mHelper?.queryInventoryAsync(true, list, list, mGotInventoryListener )
            }
        })

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
        clearCards()
        val proCard = pro_card
        proCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
        purchasePlan("pro")
        ITEM_SKU = "pro"
    }

    fun plusPlanSelected(view: View){
        clearCards()
        val plusCard = plus_card
        plusCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
        purchasePlan("plus")
        ITEM_SKU = "plus"
    }

    fun premuimPlanSelected(view: View){
        clearCards()
        val premuimCard = premuim_card
        premuimCard.setCardBackgroundColor(getResources().getColor(R.color.colorAccent))
        purchasePlan("premuim")
        ITEM_SKU = "premuim"
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


    var mGotInventoryListener:IabHelper.QueryInventoryFinishedListener = object:IabHelper.QueryInventoryFinishedListener {
        override fun onQueryInventoryFinished(result:IabResult,
                                     inventory: Inventory) {
            Log.d("mHelper", "Query inventory finished.")
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null)
            {
                return
            }
            // Is it a failure?
            if (result.isFailure)
            {
                Toast.makeText(this@create_album,
                        "Failed to query inventory: " + result,
                        Toast.LENGTH_LONG).show()
                return
            }
            Log.d("mHelper", "Query inventory was successful.")
            proPrice.text = inventory.getSkuDetails("pro").price
            plusPrice.text = inventory.getSkuDetails("plus").price
            premuimPrice.text = inventory.getSkuDetails("premuim").price
        }
    }

    fun purchasePlan(SKU: String){
        mHelper?.launchPurchaseFlow(this@create_album, SKU, PURCHASE_REQUEST,
                mPurchaseFinishedListener, "supertokenforonetimeintanlyconsumeditem")

    }

    override fun onActivityResult(requestCode:Int, resultCode:Int,
                                   data:Intent) {
            if (mHelper != null && !mHelper!!.handleActivityResult(requestCode,
                            resultCode, data))
            {
                Log.e("mHelper","User closed the window")
                super.onActivityResult(requestCode, resultCode, data)
            }

    }

    var mPurchaseFinishedListener:IabHelper.OnIabPurchaseFinishedListener = object:IabHelper.OnIabPurchaseFinishedListener {
        override fun onIabPurchaseFinished(result:IabResult,
                                  purchase:Purchase?) {
            if (result.isFailure)
            {
                // Handle error
                Log.e("mHelper", "User bounced")
                return
            }
            else if ( purchase?.getSku().equals(ITEM_SKU))
            {
                consumeItem()

                //buyButton.setEnabled(false)
            }
        }
    }
    fun consumeItem() {
        mHelper?.queryInventoryAsync(mReceivedInventoryListener)
    }
    var mReceivedInventoryListener:IabHelper.QueryInventoryFinishedListener = object:IabHelper.QueryInventoryFinishedListener {
        override fun onQueryInventoryFinished(result:IabResult,
                                     inventory:Inventory) {
            if (result.isFailure)
            {
                // Handle failure
                Log.e("mHelper", "helper dismised")
            }
            else
            {
                mHelper?.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener)
            }
        }
    }
    var mConsumeFinishedListener:IabHelper.OnConsumeFinishedListener = object:IabHelper.OnConsumeFinishedListener {
        override fun onConsumeFinished(purchase: Purchase,
                              result:IabResult) {
            if (result.isSuccess)
            {
                //clickButton.setEnabled(true)
                when (ITEM_SKU){
                    "pro"-> {
                        plan = 2
                    }
                    "plus"-> {
                        plan = 3
                    }
                    "premuim"-> {
                        plan = 4
                    }
                }
                Toast.makeText(this@create_album, getString(R.string.purchaseSuccessful), Toast.LENGTH_SHORT).show()
            }
            else
            {
                // handle error
                Toast.makeText(this@create_album, getString(R.string.purchaseFail), Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (mHelper != null) mHelper?.dispose()
        mHelper = null
    }
}
