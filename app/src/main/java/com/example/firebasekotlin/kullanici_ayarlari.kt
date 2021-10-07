package com.example.firebasekotlin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.*
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream

class kullanici_ayarlari : AppCompatActivity(), userPicPermissionFragment.onProfilePictureListener {

    var permitStatus = false
    var comeFromGalerryURI: Uri? = null
    var comeFromCameraBitmap: Bitmap? = null

    var kullanici = FirebaseAuth.getInstance().currentUser
    var context: Context = this

    val MBresult = 1000000.toDouble()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_ayarlari)
        nowTimeEmail.setText(kullanici?.email.toString())

        kullaniciBilgileriGetir()
        saveButton.visibility=View.GONE

        restartPasswordUS.setOnClickListener {

            restartPasswordUS.visibility = View.GONE
            newPassword.visibility = View.VISIBLE
            newPasswordTry.visibility = View.VISIBLE
            inputNewPassword.visibility = View.VISIBLE
            inputnewPasswordtry.visibility = View.VISIBLE
            newPasswordimg.visibility = View.VISIBLE
            newPasswordtryimg.visibility = View.VISIBLE
            userNameText.visibility = View.GONE
            nowTimeUserName.visibility = View.VISIBLE
            userTelText.visibility = View.GONE
            inputPhone.visibility = View.VISIBLE
            nowTimePassİmg.visibility = View.VISIBLE
            inputOldPassword.visibility = View.VISIBLE
            oldPassword.visibility = View.VISIBLE
            inputAdres.visibility = View.VISIBLE
            userAdres.visibility = View.GONE
            saveButton.visibility=View.VISIBLE
            prfoilePicSave.visibility=View.GONE
            addProduct.visibility=View.GONE
            addLocation.visibility=View.GONE

        }


        addProduct.setOnClickListener {
            var addProductintent =Intent(this,add_Product::class.java)
            startActivity(addProductintent)
        }

        addLocation.setOnClickListener {
            var intent=Intent(this,MapsActivity::class.java)
            startActivity(intent)
        }


        saveButton.setOnClickListener {
            if (inputOldPassword.text.toString().isNotEmpty()) {


                //Veri değişikliği
                if (inputOldPassword.text.toString().isNotEmpty() && nowTimeEmail.text.toString()
                        .isNotEmpty()
                ) {

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Emin misiniz?")
                    builder.setMessage("Yapılan Değişiklikleri Kaydetmek İstiyor musunuz?")
                    builder.setPositiveButton("Evet") { dialogInterface: DialogInterface, i: Int ->

                        var credential = EmailAuthProvider.getCredential(
                            kullanici?.email.toString(),
                            inputOldPassword.text.toString()
                        )
                        kullanici?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    userNameSave()


                                    if (inputNewPassword.text.toString()
                                            .isNotEmpty() or inputnewPasswordtry.text.toString()
                                            .isNotEmpty()
                                    ) {
                                        sifreBilgisiGuncelle()
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Şimdiki Parola Hatalı",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }


                    }
                    builder.setNegativeButton("Hayır") { dialogInterface: DialogInterface, i: Int ->
                        Toast.makeText(this, "Değişikliklerden Vazgeçildi", Toast.LENGTH_SHORT)
                            .show()
                    }
                    builder.show()


                } else {
                    Toast.makeText(this, "Boş Alanları Doldurunuuz", Toast.LENGTH_SHORT).show()
                }


            } else {
                Toast.makeText(
                    this,
                    "Değişiklikleri Kaydetmeniz İçin Var Olan Parolanızı Giriniz",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        userPicture.setOnClickListener {

            if (permitStatus) {
                var dialog = userPicPermissionFragment()
                dialog.show(supportFragmentManager, "fotosec")
            } else {
                askPermission()
            }
        }

        prfoilePicSave.setOnClickListener {

            progressBarGoster(yukleme)
            if (comeFromGalerryURI != null) {

                pictureCompressed(comeFromGalerryURI!!)

            } else if (comeFromCameraBitmap != null) {

                pictureCompressed(comeFromCameraBitmap!!)


            }
            progressBarGizle(yukleme)
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
                    this@kullanici_ayarlari.contentResolver,
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
            Toast.makeText(this@kullanici_ayarlari,"Fotoğraf Yükleniyor",Toast.LENGTH_SHORT).show()
        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadPicturetoFirebase(result)
        }

    }



    private fun uploadPicturetoFirebase(result: ByteArray?) {

        var storageReferance = FirebaseStorage.getInstance().reference.child("images/users/" + FirebaseAuth.getInstance().currentUser?.uid + "/profil_resmi")
        var resimEkle = storageReferance.putBytes(result!!)
        resimEkle.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri>? {
                if (!p0.isSuccessful()) {
                    throw p0.getException()!!
                }
                return storageReferance.downloadUrl
            }

        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(p0: Task<Uri>) {
                if (p0.isSuccessful) {
                    Log.e("Firebasestorage", p0.result.toString())
                    val downloadUri = p0.result
                    FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("profil_resmi")
                        .setValue(downloadUri!!.toString())

                    Toast.makeText(this@kullanici_ayarlari, "Fotoğraf Yüklendi: " + downloadUri.toString(), Toast.LENGTH_SHORT).show()

                } else {
                    // Handle failures
                    // ...
                }
            }
        }).addOnFailureListener(object : OnFailureListener{
            override fun onFailure(p0: Exception) {
                Toast.makeText(this@kullanici_ayarlari, "Resim yüklenirken hata oluştu", Toast.LENGTH_SHORT).show()

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

    private fun kullaniciBilgileriGetir() {

        progressBarGoster(yukleme)
        progressBarGoster(Ppyukleme)

        var referans = FirebaseDatabase.getInstance().reference

        nowTimeEmail.setText(kullanici?.email.toString())

        //metot1
        var sorgu = referans.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (singleSnapshot in p0.children) {
                    var readuser = singleSnapshot.getValue(Kullanici::class.java)
                    Log.e(
                        "Firebase",
                        "Adı:" + readuser?.isim + " Kullanıcı Adı:" + readuser?.kullanici_adi
                                + " Telefon:" + readuser?.telefon + " Profil Resim:" + readuser?.profil_resmi + " Seviye:" + readuser?.seviye
                    )
                    if (readuser?.seviye == "2") {
                        addProduct.visibility = View.VISIBLE
                    }else if (readuser?.seviye=="3"){
                        addLocation.visibility=View.VISIBLE
                    }
                    else {
                        addProduct.visibility = View.GONE
                    }



                    nowTimeUserName.setText(readuser?.kullanici_adi)
                    userNameText.setText(readuser?.kullanici_adi)
                    nowTimeName.setText(readuser?.isim)
                    userTelText.setText(readuser?.telefon)
                    inputPhone.setText(readuser?.telefon)
                    userAdres.setText(readuser?.adres)


                    progressBarGoster(Ppyukleme)

                    if (readuser?.profil_resmi!!.isEmpty()) {

                    } else {
                        Picasso.with(this@kullanici_ayarlari).load(readuser.profil_resmi).into(userPicture)
                        progressBarGizle(Ppyukleme)

                    }
                    progressBarGizle(yukleme)

                }

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun sifreBilgisiGuncelle() {

        if (inputNewPassword.text.toString().isNotEmpty() && inputnewPasswordtry.text.toString()
                .isNotEmpty()
        ) {
            if (kullanici != null && inputnewPasswordtry.text.toString()
                    .equals(inputNewPassword.text.toString())
            ) {
                kullanici?.updatePassword(inputNewPassword.text.toString())
                Toast.makeText(this, "Şifre Güncellendi, Tekrar Giriş Yapın", Toast.LENGTH_SHORT)
                    .show()
                FirebaseAuth.getInstance().signOut()
                finish()
            } else {
                Toast.makeText(this, "Yeni Şifreler Uyuşmuyor", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "Yeni Parola Alanları Boş Bırakılamaz", Toast.LENGTH_SHORT).show()
        }
    }

    fun userNameSave() {

        var bilgileriGuncelle = UserProfileChangeRequest.Builder()
            .setDisplayName(nowTimeUserName.text.toString())
            .build()
        kullanici?.updateProfile(bilgileriGuncelle)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Kullanıcı Adı Güncellendi", Toast.LENGTH_SHORT)
                        .show()

                    FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .child("kullanici_adi")
                        .setValue(nowTimeUserName.text.toString())

                    FirebaseDatabase.getInstance().reference
                        .child("kullanici")
                        .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                        .child("adres")
                        .setValue(inputAdres.text.toString())

                    if (inputPhone.text.toString().isNotEmpty()) {

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .child("telefon")
                            .setValue(inputPhone.text.toString())

                    }

                }
            }
    }


    override fun getPictureCode(picResult: Uri?) {

        comeFromGalerryURI = picResult
        Picasso.with(this).load(comeFromGalerryURI).into(userPicture)

    }

    override fun getPictureBitmap(bitmap: Bitmap) {

        comeFromCameraBitmap = bitmap
        userPicture.setImageBitmap(bitmap)

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

    private fun progressBarGoster(progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGizle(progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
    }




}

