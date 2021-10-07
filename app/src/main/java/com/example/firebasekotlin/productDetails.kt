package com.example.firebasekotlin

import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add__product.*
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.image_with_text.view.*

open class productDetails : AppCompatActivity() {

    var checkProductId: String = ""
    var imageLink=""
    var productlink : String = ""
    var ref=FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)


        if(intent.hasExtra("product_id")){
            checkProductId = intent.getStringExtra("product_id").toString()
        }

        getProductInformation()



    }

    private fun getProductInformation() {

        var referans = FirebaseDatabase.getInstance().reference

        //metot1
        var sorgu = referans.child("product")
            .orderByKey()
            .equalTo(checkProductId)

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {



                for (singleSnapshot in p0.children) {

                    var readuser = singleSnapshot.getValue(Ürünler::class.java)

                    productNameDetail.setText("Ürün Adı : "+readuser?.product_name)
                    productPriceDetail.setText("Ürün Fiyatı : "+readuser?.product_price)
                    productPiecesDetail.setText("Adet : "+readuser?.product_pieces)
                    ProductColorsDetail.setText("Ürün Özelli : "+readuser?.product_color)
                    productSizeDetail.setText("Ürün Boyut/Numara : "+readuser?.product_size)
                    Productexplanation.setText("Ürün Açıklaması : "+readuser?.ürün_information)
                    productlink=readuser?.product_id.toString()
                    imageLink= readuser?.ürün_resmi!!



                    if (readuser?.ürün_resmi!!.isEmpty()) {

                    } else {
                        Picasso.with(this@productDetails).load(readuser.ürün_resmi).into(productDetailsPic)

                    }
                }

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        addBasket.setOnClickListener {
            FirebaseDatabase.getInstance().reference
                .child("kullanici")
                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .child("basket")
                .child(productlink.toString())
                .setValue(imageLink)



            Toast.makeText(this,"Ürün Sepete Eklendi",Toast.LENGTH_SHORT).show()

        }
    }

}