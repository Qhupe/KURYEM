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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyAuthStateListener()


        kayitOl.setOnClickListener {
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        newSifre.setOnClickListener {
            var dialogSifreyYenile = SifremiUnuttumDialogFragment()
            dialogSifreyYenile.show(supportFragmentManager, "sifretrydialog")
        }

        onayMailGonder.setOnClickListener {
            var dialogGoster = OnayMailTryFragment()
            dialogGoster.show(supportFragmentManager, "gosterdialog")
        }

        giris.setOnClickListener {

            progressBarGoster()

            if (eMailgiris.text.isNotEmpty() && sifreGiris.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    eMailgiris.text.toString(),
                    sifreGiris.text.toString()
                ).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {

                        if (p0.isSuccessful) {
                            progressBarGizle()
                            Toast.makeText(
                                this@LoginActivity,
                                "Giriş Yapılıyor... : " + FirebaseAuth.getInstance().currentUser?.email,
                                Toast.LENGTH_SHORT
                            ).show()
                            if (!p0.result?.user?.isEmailVerified!!) {
                                FirebaseAuth.getInstance().signOut()
                            }
                        } else {
                            progressBarGizle()
                            Toast.makeText(
                                this@LoginActivity,
                                "Giriş Yapılamadı, Email veya Şifre Yanlış ",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
                })

            } else {
                Toast.makeText(this@LoginActivity, "Boş Alanları Doldurunuz", Toast.LENGTH_SHORT)
                    .show()
                progressBarGizle()
            }

        }

    }

    private fun progressBarGoster() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initMyAuthStateListener() {

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if (kullanici != null) {

                    if (kullanici.isEmailVerified) {

                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Lütfen Giriş Yapmak İçin Size Gönderilen Maili Onaylayınız",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

        }

    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }


}