package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_get_location.*

class getLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_location)


        konumAlButon.setOnClickListener {
            if (kuryeUserName.text.isNotEmpty()){
                val intent = Intent(this, MapsActivityUser::class.java)
                intent.putExtra("kuryeUserName",kuryeUserName.text.toString())
                startActivity(intent)
                finish()
            }
        }

    }
}