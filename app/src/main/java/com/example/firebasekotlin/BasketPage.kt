package com.example.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_basket_page2.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_with_text.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BasketPage : AppCompatActivity() {


    lateinit var myAutStateListener: FirebaseAuth.AuthStateListener
    var kullanici = FirebaseAuth.getInstance().currentUser
    lateinit var getAllProduct:ArrayList<Ürünler>
    var myAdapter:BasketViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_basket_page2)

        initAuthStateListener()
        setKullaniciBilgileri()
        init()




    }

    private fun setKullaniciBilgileri() {

        var referans = FirebaseDatabase.getInstance().reference

        //metot1
        var sorgu = referans.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)

    }

    private fun initAuthStateListener() {
        myAutStateListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                var kulanici = p0.currentUser

                if (kulanici != null) {

                } else {
                    var intent = Intent(this@BasketPage, LoginActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.anamenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.menuCikisYap -> {
                cikisYap()
                return true
            }
            R.id.hesapAyar -> {
                var intent = Intent(this, kullanici_ayarlari::class.java)
                startActivity(intent)
            }
            R.id.myBasket -> {
                var intent=Intent(this,BasketPage::class.java)
                startActivity(intent)
            }
            R.id.addProduct -> {
                var intent = Intent(this, kullanici_ayarlari::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cikisYap() {
        FirebaseAuth.getInstance().signOut()
    }

    override fun onResume() {
        super.onResume()
        kullaniciyiKontrolEt()
        setKullaniciBilgileri()
    }

    private fun kullaniciyiKontrolEt() {
        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici == null) {
            var intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(myAutStateListener)
    }

    override fun onStop() {
        super.onStop()
        if (myAutStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(myAutStateListener)
        }
    }

    fun init(){
        getAllProductFromActivity()
    }

    private fun getAllProductFromActivity() {


        getAllProduct=ArrayList<Ürünler>()
        var ref=FirebaseDatabase.getInstance().reference

        var sorgu=ref.child("kullanici").child(kullanici?.uid.toString()).child("basket").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (allProductBox in p0?.children){

                   var idRef=allProductBox.key.toString()

                    var sorgu=ref.child("product").addListenerForSingleValueEvent(object:ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {

                            for (allProductBoxref in p0?.children){

                                var nowProduct = Ürünler()

                                var ObjectMap=(allProductBoxref.getValue() as HashMap<String,Object>)
                                nowProduct.product_id=ObjectMap.get("product_id").toString()
                                nowProduct.product_name=ObjectMap.get("product_name").toString()
                                nowProduct.product_price=ObjectMap.get("product_price").toString()
                                nowProduct.product_color=ObjectMap.get("product_color").toString()
                                nowProduct.product_size=ObjectMap.get("product_size").toString()
                                nowProduct.product_pieces=ObjectMap.get("product_pieces").toString()
                                nowProduct.ürün_resmi=ObjectMap.get("ürün_resmi").toString()
                                nowProduct.kullanici_id=ObjectMap.get("kullanici_id").toString()

                                Log.e("TEST",nowProduct.product_id+" "+nowProduct.ürün_resmi)

                                if (idRef==nowProduct.product_id) {
                                    getAllProduct.add(nowProduct)
                                    myAdapter?.notifyDataSetChanged()
                                }
                            }
                            if(myAdapter==null){
                                spetiListele()
                            }

                        }
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
              }
                if(myAdapter==null){
                    spetiListele()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun spetiListele() {

        myAdapter=BasketViewAdapter(this, getAllProduct!!)
        basketRecycler.adapter=myAdapter
        basketRecycler.layoutManager= LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)



    }
}