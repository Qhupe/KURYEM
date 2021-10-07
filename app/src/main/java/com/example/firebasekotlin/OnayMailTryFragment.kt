package com.example.firebasekotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class OnayMailTryFragment : DialogFragment() {

    lateinit var emailEditText: EditText
    lateinit var sifreEditText: EditText
    lateinit var mContext: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dialog, container, false)
        emailEditText = view.findViewById(R.id.eMailFrag)
        sifreEditText = view.findViewById(R.id.sifreFrag)
        mContext = requireActivity()

        var btnIptal = view.findViewById<Button>(R.id.btnIptalFrag)
        btnIptal.setOnClickListener {

            dialog?.dismiss()

        }
        var btnGonder = view.findViewById<Button>(R.id.btnGonderFrag)
        btnGonder.setOnClickListener {

            if (emailEditText.text.toString().isNotEmpty() && sifreEditText.text.toString()
                    .isNotEmpty()
            ) {
                onayMailiTekrarGonder(emailEditText.text.toString(), sifreEditText.text.toString())
            } else {
                Toast.makeText(mContext, "Lütfen Boş Alanları Doldurunuz", Toast.LENGTH_SHORT)
                    .show()
            }


        }


        return view
    }

    private fun onayMailiTekrarGonder(email: String, sifre: String) {

        var credential = EmailAuthProvider.getCredential(email, sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onayMailiGonder()
                    dialog?.dismiss()
                } else {

                    Toast.makeText(mContext, "Email veya Şifre Hatalı", Toast.LENGTH_SHORT).show()

                }

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
                                mContext,
                                "Kayıt işlemini tamamlamak için size gönderilen maili onaylayın",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                mContext,
                                "Onay Maili Gönderilemedi",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })

        }
    }


}