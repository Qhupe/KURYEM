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
import com.google.firebase.auth.FirebaseAuth


class SifremiUnuttumDialogFragment : DialogFragment() {

    lateinit var email: EditText
    lateinit var mContext: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_sifremi_unuttum_dialog, container, false)

        mContext = requireActivity()
        email = view.findViewById(R.id.sifreTekrar)

        var btnIptal = view.findViewById<Button>(R.id.btncncl)
        btnIptal.setOnClickListener {
            dialog?.dismiss()
        }
        var btnSifreIste = view.findViewById<Button>(R.id.btnsifretry)
        btnSifreIste.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            mContext,
                            "Şifre Sıfırlama Maili Gönderildi",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog?.dismiss()
                    } else {
                        Toast.makeText(
                            mContext,
                            "Şifre Sıfırlama Maili Gönderilemedi, Lütfen Mail Adresinizi Kontrol Edin",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

        }

        return view
    }


}