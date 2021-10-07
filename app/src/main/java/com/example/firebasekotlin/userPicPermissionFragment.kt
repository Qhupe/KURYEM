package com.example.firebasekotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment


@Suppress("DEPRECATION")
class userPicPermissionFragment : DialogFragment() {

    lateinit var btnpiCamera: Button
    lateinit var btnpicStorage: Button
    lateinit var takeCamera: Button

    interface onProfilePictureListener{

        fun getPictureCode(picResult:Uri?)
        fun getPictureBitmap(bitmap: Bitmap)

    }

    lateinit var mProfilePictureListener: onProfilePictureListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v = inflater.inflate(R.layout.fragment_user_pic_permission, container, false)

        btnpicStorage = v.findViewById(R.id.allowStorage)
        btnpiCamera = v.findViewById(R.id.takeCamera)
        takeCamera = v.findViewById<Button>(R.id.takeCamera)

        btnpicStorage.setOnClickListener {

            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        takeCamera.setOnClickListener {

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)
        }

        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {

            var selectFromGalleryPic = data.data
            mProfilePictureListener.getPictureCode(selectFromGalleryPic)

            dismiss()

        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {

            var takeFromCameraPic : Bitmap
            takeFromCameraPic= data.extras?.get("data") as Bitmap
            mProfilePictureListener.getPictureBitmap(takeFromCameraPic)
            dialog?.dismiss()

        }


    }

    override fun onAttach(context: Context) {

        mProfilePictureListener=activity as onProfilePictureListener

        super.onAttach(context)
    }

}