package com.example.firebasekotlin

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add__product.*
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.image_with_text.*
import java.io.ByteArrayOutputStream
import java.util.*


class add_Product : AppCompatActivity(), userPicPermissionFragment.onProfilePictureListener {

    var permitStatus = false
    var comeFromGalerryURI: Uri? = null
    var comeFromCameraBitmap: Bitmap? = null
    var ref=FirebaseDatabase.getInstance().reference
    var productId=ref.child("product").push().key




    var kullanici = FirebaseAuth.getInstance().currentUser
    var context: Context = this

    val MBresult = 1000000.toDouble()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add__product)



        imageProduct.setOnClickListener {

            if (permitStatus) {
                var dialog = userPicPermissionFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                askPermission()
            }
        }

        saveProduct.setOnClickListener {

            if (productName.text.toString()!=""&&productColor.text.toString()!=""&&productPieces.text.toString()!=""&&productPrice.text.toString()!=""&&productSize.text.toString()!=""&&productInformation.text.toString()!=""&&comeFromGalerryURI!=null) {

                if (comeFromGalerryURI != null) {

                    pictureCompressed(comeFromGalerryURI!!)

                } else if (comeFromCameraBitmap != null) {

                    pictureCompressed(comeFromCameraBitmap!!)

                }
                yeniUrunKayit()
                productName.setText("")
                productColor.setText("")
                productPieces.setText("")
                productPrice.setText("")
                productSize.setText("")
                productInformation.setText("")
                imageProduct.invalidate();
                imageProduct.setImageBitmap(null);
            }else{
                Toast.makeText(this,"Lütfen Tüm Alanları Doldurun",Toast.LENGTH_SHORT).show()
            }


        }
    }


    inner class BackgroundPictureCompress : AsyncTask<Uri, Double, ByteArray?> {

        var myBitmap: Bitmap? = null

        constructor() {
        }
        constructor(bm: Bitmap) {

            if (bm != null) {
                myBitmap = bm
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        //Resmi Alıp Sıkıştırıp ByteArray e Çevirme Metodu
        override fun doInBackground(vararg params: Uri?): ByteArray? {

            //Galeriden resim seçilme durumu
            if (myBitmap == null) {

                myBitmap = MediaStore.Images.Media.getBitmap(
                    this@add_Product.contentResolver,
                    params[0]
                )
                Log.e("TEST", "Resim Gerçek Boyut: " + (myBitmap!!.byteCount).toDouble() / MBresult)
            }

            var picBytes: ByteArray? = null

            for (i in 1..3) {
                picBytes = convertBitmaptoByte(myBitmap, 100 / i)
                publishProgress(picBytes?.size?.toDouble())
            }

            return picBytes
        }

        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)
            Log.e("Sıkıştırma", "Sıkıştırılan Boyut:" + values[0]!! / MBresult + " Mb")
            Toast.makeText(this@add_Product, "Fotoğraf Yükleniyor", Toast.LENGTH_SHORT).show()
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadPicturetoFirebase(result)
        }

    }



    private fun uploadPicturetoFirebase(result: ByteArray?) {


        var storageReferance = FirebaseStorage.getInstance().reference.child("/productimages/"+kullanici?.uid+"/products/"+productId)
        var resimEkle = storageReferance.putBytes(result!!)
        resimEkle.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri>? {
                if (!p0.isSuccessful()) {
                    throw p0.getException()!!
                }
                return storageReferance.downloadUrl
                println("ÜRÜN LİNKİ ------------------------>"+storageReferance.downloadUrl)
            }

        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>) {
                if (p0.isSuccessful) {
                    Log.e("Firebasestorage", p0.result.toString())
                    val downloadUri = p0.result
                    FirebaseDatabase.getInstance().reference
                        .child("product")
                        .child(productId.toString())
                        .child("ürün_resmi")
                        .setValue(downloadUri!!.toString())

                    Toast.makeText(
                        this@add_Product,
                        "Fotoğraf Yüklendi: " + downloadUri.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // Handle failures
                    // ...
                }
            }
        }).addOnFailureListener(object : OnFailureListener {
            override fun onFailure(p0: Exception) {
                Toast.makeText(
                    this@add_Product,
                    "Resim yüklenirken hata oluştu",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun convertBitmaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {

        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.PNG, i, stream)
        return stream.toByteArray()

    }

    private fun askPermission() {

        var permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        if (ContextCompat.checkSelfPermission(
                this,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                permissions[1]
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                permissions[2]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            permitStatus = true
        } else {
            ActivityCompat.requestPermissions(this, permissions, 150)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 150) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                var dialog = userPicPermissionFragment()
                dialog.show(supportFragmentManager, "fotosec")

            } else {
                Toast.makeText(
                    this,
                    "Gerekli İzinler Verilmeden İşlem Gerçekleştirilemez",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }


    override fun getPictureCode(picResult: Uri?) {

        comeFromGalerryURI = picResult
        Picasso.with(this).load(comeFromGalerryURI).into(imageProduct)

    }

    override fun getPictureBitmap(bitmap: Bitmap) {

        comeFromCameraBitmap = bitmap
        imageProduct.setImageBitmap(bitmap)

    }

    private fun pictureCompressed(comeFromGalerryURI: Uri) {

        var compressed = BackgroundPictureCompress()
        compressed.execute(comeFromGalerryURI)
    }

    private fun pictureCompressed(comeFromCameraBitmap: Bitmap) {

        var compressed = BackgroundPictureCompress(comeFromCameraBitmap)
        var uri: Uri? = null
        compressed.execute(uri)

    }



    private fun yeniUrunKayit() {

        var addDatabaseUser = Ürünler()
        addDatabaseUser.product_name = productName.text.toString()
        addDatabaseUser.product_price = productPrice.text.toString()
        addDatabaseUser.product_color = productColor.text.toString()
        addDatabaseUser.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
        addDatabaseUser.ürün_resmi =""
        addDatabaseUser.product_size = productSize.text.toString()
        addDatabaseUser.product_id=productId
        addDatabaseUser.ürün_information=productInformation.text.toString()
        addDatabaseUser.product_pieces = productPieces.text.toString()

        FirebaseDatabase.getInstance().reference
            .child("product")
            .child(productId.toString())
            .setValue(addDatabaseUser).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Üye Kaydedildi ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Üye Kaydedilirken Bir Sorun Oluştu ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        // Create a new user with a first and last name
    }



}




