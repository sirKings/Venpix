package com.ladrope.venpix.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.ladrope.venpix.R
import com.ladrope.venpix.utilities.LetterAvatar
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.change_password_popup.view.*


class Settings : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null
    var mCurrentUser: FirebaseUser? = null
    var GALLERY_ID: Int = 1
    var displayName: String? = null
    var email: String? = null
    var userImage: String? = null
    var coverImage: String? = null
    var mImage: String? = null
    var mProgress: ProgressBar? = null
    var mName: String? = null
    var mEmail: String? = null
    private var dialogBuilder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null

    val colorInt: Int = Color.parseColor("#252231")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mCurrentUser = FirebaseAuth.getInstance().currentUser


        var userId = mCurrentUser!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(userId)
        mProgress = settingsProgressbar

        mDatabase!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                displayName = dataSnapshot!!.child("userName").value.toString()
                userImage = dataSnapshot!!.child("userImage").value.toString()
                coverImage = dataSnapshot!!.child("coverImage").value.toString()
                email = dataSnapshot!!.child("email").value.toString()

                settingsName.setText(displayName)
                mName = displayName
                settingsEmail.setText(email)
                mEmail = email


                if (userImage!!.equals("default")) {
                    val initial = mCurrentUser!!.displayName?.get(0)
                    settingsProfileImage.setImageDrawable(LetterAvatar(this@Settings, colorInt, initial, 15))
                }else{
                    Picasso.with(this@Settings)
                            .load(userImage)
                            .placeholder(R.drawable.profile_img)
                            .into(settingsProfileImage)
                }

                if (coverImage!!.equals("default")) {
                    Picasso.with(this@Settings)
                            .load(R.drawable.garden)
                            .into(settingCoverImage)
                }else{
                    Picasso.with(this@Settings)
                            .load(coverImage)
                            .placeholder(R.drawable.garden)
                            .into(settingCoverImage)
                }


            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError?) {

            }

        })
    }

    fun changeImage(name: String){

        //setup variable
        mImage = name
        //open gallery to get the picture

        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.chooseImage)), GALLERY_ID)

    }

    fun changeUserImage(view: View){
        changeImage("userImage")
    }

    fun changeCoverImage(view: View){
        changeImage("coverImage")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_ID
                && resultCode == Activity.RESULT_OK) {

            var image: Uri = data!!.data

            if(mImage == "userImage"){
                CropImage.activity(image)
                        .setAspectRatio(3, 4)
                        .start(this)
            }else{
                CropImage.activity(image)
                        .setAspectRatio(4, 3)
                        .start(this)
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

                val resultUri = result.uri

                var thumbFile = resultUri.path

                class callback: UploadCallback {
                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {

                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(this@Settings,getString(R.string.uploadFail), Toast.LENGTH_SHORT).show()
                    }

                    override fun onStart(requestId: String?) {
                            settingsSaveBtn.isClickable = false
                            mProgress?.visibility = View.VISIBLE
                    }

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        var updateObj = HashMap<String, Any?>()
                            updateObj.put(mImage!!, resultData?.get("url"))
                            mDatabase!!.updateChildren(updateObj)
                                        .addOnCompleteListener { task: Task<Void> ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this@Settings, getString(R.string.uploadSucc),
                                                        Toast.LENGTH_LONG)
                                                        .show()
                                                        settingsSaveBtn.isClickable = true
                                                        mProgress?.visibility = View.GONE

                                            } else {

                                            }
                                        }

                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    }
                }


                var requestid = MediaManager.get().upload(thumbFile).unsigned("kfmwfbua").option("public_id", System.currentTimeMillis().toString()).callback(callback()).dispatch()



            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d("Error", error.toString())
            }
        }
//        super.onActivityResult(requestCode, resultCode, data)
    }

    fun saveInfo(view: View){
        if(mName == settingsName.text.toString()){

        }else{
            changeName(settingsName.text.toString())
        }

        if (mEmail == settingsEmail.text.toString()){

        }else{
            changeEmail(settingsEmail.text.toString())
        }

        Toast.makeText(this, getString(R.string.uploadSucc), Toast.LENGTH_SHORT).show()
        val homeIntent = Intent(this@Settings, home::class.java)
        startActivity(homeIntent)
        finish()
        
    }

    fun settingsChangePassword(view: View){

        var popup = layoutInflater.inflate(R.layout.change_password_popup, null)


        dialogBuilder = AlertDialog.Builder(this@Settings).setView(popup)
        //dialogBuilder?.setCancelable(false)
        dialog = dialogBuilder!!.create()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
        val popupProgress = popup.popProgress
        popupProgress.visibility = View.GONE


        popup.popupSaveBtn.setOnClickListener {

            val oldPass = popup.popupOldPassword.text.toString()
            val newPass = popup.popupNewPassword.text.toString()
            val confirmNewPass = popup.popupConfirmPassword.text.toString()
            if(oldPass != "" && newPass != "" && confirmNewPass != ""){
                if(newPass == confirmNewPass){
                    popupProgress.visibility = View.VISIBLE
                    popup.popupSaveBtn.isClickable = false
                    popup.popupCancelBtn.isClickable = false
                    val credential = EmailAuthProvider.getCredential(mEmail!!, oldPass)
                    mCurrentUser?.reauthenticate(credential)?.addOnCompleteListener {
                        mCurrentUser?.updatePassword(newPass)?.addOnCompleteListener {
                            Toast.makeText(this, getString(R.string.passUpdated), Toast.LENGTH_SHORT).show()
                            popupProgress.visibility = View.GONE
                            dialog?.dismiss()}
                                ?.addOnFailureListener {
                                    Toast.makeText(this,getString(R.string.passUpdateFail), Toast.LENGTH_SHORT).show()
                                    popupProgress.visibility = View.GONE
                                    dialog?.dismiss()}
                    }
                }else{
                    Toast.makeText(this, getString(R.string.passwordMatch), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,getString(R.string.enterAll), Toast.LENGTH_SHORT).show()
            }

        }
        popup.popupCancelBtn.setOnClickListener { dialog?.dismiss() }

    }

    fun changeName(name: String){
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
        mCurrentUser?.updateProfile(profileUpdates)
        val updateObj = HashMap<String, Any?>()
        updateObj.put("userName", name)
        mDatabase!!.updateChildren(updateObj)
                .addOnCompleteListener { task: Task<Void> ->
                    if (task.isSuccessful) {

                    } else {

                    }
                }
    }

    fun changeEmail(email: String){
        mCurrentUser?.updateEmail(email)
                ?.addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful) {
                            var updateObj = HashMap<String, Any?>()
                            updateObj.put("email", email)
                            mDatabase!!.updateChildren(updateObj)
                                    .addOnCompleteListener { task: Task<Void> ->
                                        if (task.isSuccessful) {

                                        } else {

                                        }
                                    }
                        }
                    }
                })

    }


    fun logout(view: View){
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        val mainIntent = Intent(this@Settings, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }
}
