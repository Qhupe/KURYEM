package com.example.firebasekotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.image_with_text.view.*


class BasketViewAdapter(mActivity: AppCompatActivity, allProduct: ArrayList<Ürünler>) : RecyclerView.Adapter<BasketViewAdapter.BasketHolder>() {

    var idRef=""
    var products=allProduct
    var kullanici=FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketHolder {
        var inflater=LayoutInflater.from(parent?.context)
        var productBox = inflater.inflate(R.layout.image_with_text, parent, false)



        return BasketHolder(productBox)
    }

    override fun getItemCount(): Int {
        return products.size
    }




    override fun onBindViewHolder(holder: BasketHolder, position: Int) {

        var NowCreateBasket = products.get(position)
        holder?.setData(NowCreateBasket, position)

    }




    inner class BasketHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var productActivity=itemView as LinearLayout
        var productImageAdp = productActivity.product_image
        var productColorAdp=productActivity.product_color
        var productPriceAdp=productActivity.product_price
        var productPiecesAdp=productActivity.product_pieces
        var productSizeAdp=productActivity.product_size
        var ProductNameAdp = productActivity.product_Name

        @SuppressLint("RestrictedApi")
        fun setData(nowCeateProduct1: Ürünler, position: Int) {


            productActivity.deleteBasket.visibility=View.VISIBLE

            var ref=FirebaseDatabase.getInstance().reference

            var sorgu1=ref.child("kullanici").child(FirebaseAuth.getInstance()?.uid.toString()).child(
                "basket"
            ).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        println("BASKETADAPTERRRRR ID----------------" + idRef)
                        println("BASKETADAPTERRRRR Product ID----------------" + nowCeateProduct1.product_id)
                        productColorAdp.text =
                            "Renk: " + (nowCeateProduct1.product_color).toString()
                        ProductNameAdp.text =
                            "Ürün Adı: " + (nowCeateProduct1.product_name).toString()
                        productSizeAdp.text = "Boyut: " + (nowCeateProduct1.product_size).toString()
                        productPiecesAdp.text =
                            "Adet: " + (nowCeateProduct1.product_pieces).toString()
                        productPriceAdp.text =
                            "Fiyat: " + (nowCeateProduct1.product_price).toString()
                        println("------------------>" + nowCeateProduct1.ürün_resmi.toString())
                        if (nowCeateProduct1.ürün_resmi.toString()!!.isEmpty()) {

                        } else {
                            Picasso.with(itemView.context)
                                .load(nowCeateProduct1.ürün_resmi.toString()).into(
                                productImageAdp
                            )

                        }
                        // }

                        productActivity.deleteBasket.setOnClickListener {

                            val builder = AlertDialog.Builder(itemView.context)
                            builder.setTitle("Emin misiniz?")
                            builder.setMessage("Ürünü Sepetinizden Silmek İstiyor Musunuz?")
                            builder.setPositiveButton("Evet") { dialogInterface: DialogInterface, i: Int ->

                                FirebaseDatabase.getInstance().reference.child("kullanici").child(
                                    kullanici!!.uid
                                ).child("basket").child(nowCeateProduct1.product_id.toString())
                                    .removeValue()
                                var intent = Intent(itemView.context, BasketPage::class.java)
                                (itemView.context as Activity).finish()
                                itemView.context.startActivity(intent)


                            }
                            builder.setNegativeButton("Hayır") { dialogInterface: DialogInterface, i: Int ->

                            }
                            builder.show()

                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


        }






    }
}