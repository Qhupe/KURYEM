package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        kayit_ol.setOnClickListener {

            if (eMail.text.isNotEmpty() && sifre.text.isNotEmpty() && sifreTekrar.text.isNotEmpty()) {

                if (sifre.text.toString().equals(sifreTekrar.text.toString())) {

                    yeniUyeKayit(eMail.text.toString(), sifre.text.toString())
                } else {
                    Toast.makeText(
                        this,
                        "Şifreler Eşleşmiyor, Lütfen Kontrol Edin",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(this, "Boş Alanları Doldurunuz", Toast.LENGTH_SHORT)
            }

        }

        btncncl.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }

    private fun onayMailiGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici != null) {
            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Kayıt işlemini tamamlamak için size gönderilen maili onaylayın",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Onaylama Maili Gönderilemedi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
                )
        }
    }

    private fun yeniUyeKayit(mail: String, usifre: String) {
        progressBarGoster()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, usifre)
            .addOnCompleteListener(
                object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {

                        if (p0.isSuccessful) {


                            var addDatabaseUser = Kullanici()
                            addDatabaseUser.isim = isim.text.toString()
                            addDatabaseUser.kullanici_adi = userNameRegister.text.toString()
                            addDatabaseUser.e_mail = eMail.text.toString()
                            addDatabaseUser.kullanici_id =
                                FirebaseAuth.getInstance().currentUser?.uid
                            addDatabaseUser.profil_resmi = ""
                            addDatabaseUser.telefon = telefon.text.toString()
                            addDatabaseUser.adres=userAdres.text.toString()

                            if (checkBoxCustomer.isChecked){
                                addDatabaseUser.seviye="1"
                                checkBoxCurye.setChecked(false)
                                checkSales.setChecked(false)
                            }else if (checkSales.isChecked){
                                addDatabaseUser.seviye="2"
                                checkBoxCurye.setChecked(false)
                                checkBoxCustomer.setChecked(false)
                            }else if (checkBoxCurye.isChecked){
                                addDatabaseUser.seviye="3"
                                checkBoxCustomer.toggle()
                                checkSales.setChecked(false)
                            }
                            else{
                                Toast.makeText(this@RegisterActivity,"Lütfen Satıcı Veya Müşteri Kısmını Doldurunuz",Toast.LENGTH_SHORT).show()
                            }

                            FirebaseDatabase.getInstance().reference
                                .child("kullanici")
                                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                                .setValue(addDatabaseUser).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Üye Kaydedildi ",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        onayMailiGonder()
                                        FirebaseAuth.getInstance().signOut()
                                        loginSayfasinaGit()
                                    } else {
                                        Toast.makeText(
                                            this@RegisterActivity,
                                            "Üye Kaydedilirken Bir Sorun Oluştu " + p0.exception?.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            eMail.setText("")
                            sifre.setText("")
                            sifreTekrar.setText("")
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Üye Kaydedilirken Bir Sorun Oluştu " + p0.exception?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        progressBarGizle()
    }

    private fun progressBarGoster() {
        yukleme.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        yukleme.visibility = View.INVISIBLE
    }

    private fun loginSayfasinaGit() {
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}