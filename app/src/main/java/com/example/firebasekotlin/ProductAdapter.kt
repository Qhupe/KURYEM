package com.example.firebasekotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_with_text.view.*


class SohbetOdasiRecyclerViewAdapter(mActivity: AppCompatActivity, allProduct:ArrayList<Ürünler>) : RecyclerView.Adapter<SohbetOdasiRecyclerViewAdapter.ProductBoxHolder>() {


    var products=allProduct
    var myActivity=mActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductBoxHolder {
        var inflater=LayoutInflater.from(parent?.context)
        var productBox = inflater.inflate(R.layout.image_with_text,parent,false)

        return ProductBoxHolder(productBox)
    }

    override fun getItemCount(): Int {
        return products.size
    }


    override fun onBindViewHolder(holder: ProductBoxHolder, position: Int) {

        var oAnOlusturulanSohbetOdasi = products.get(position)
        holder?.setData(oAnOlusturulanSohbetOdasi, position)

    }

    inner class ProductBoxHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {




        var productActivity=itemView as LinearLayout
        var productImageAdp = productActivity.product_image
        var productColorAdp=productActivity.product_color
        var productPriceAdp=productActivity.product_price
        var productPiecesAdp=productActivity.product_pieces
        var productSizeAdp=productActivity.product_size
        var ProductNameAdp = productActivity.product_Name

        @SuppressLint("RestrictedApi")
        fun setData(nowCeateProduct: Ürünler, position: Int) {
            ProductNameAdp.text=nowCeateProduct.product_name

            productActivity.setOnClickListener {

                var intent=Intent(myActivity,productDetails::class.java)
                intent.putExtra("product_id",(nowCeateProduct.product_id).toString())
                myActivity.startActivity(intent)

            }

            var ref=FirebaseDatabase.getInstance().reference
            var sorgu=ref.child("product")
                .orderByKey()
                .equalTo(nowCeateProduct.product_id).addListenerForSingleValueEvent(object:ValueEventListener{

                    override fun onDataChange(p0: DataSnapshot) {
                        for(kullanici in p0!!.children){
                            var profilResmiPath=kullanici.getValue(Kullanici::class.java)?.profil_resmi.toString()
                            if(profilResmiPath.isNullOrEmpty() or profilResmiPath.isNullOrBlank())
                            {
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            productColorAdp.text="Renk: "+ (nowCeateProduct.product_color).toString()
            ProductNameAdp.text="Ürün Adı: "+ (nowCeateProduct.product_name).toString()
            productSizeAdp.text="Boyut: "+ (nowCeateProduct.product_size).toString()
            productPiecesAdp.text="Adet: "+ (nowCeateProduct.product_pieces).toString()
            productPriceAdp.text="Fiyat: "+ (nowCeateProduct.product_price).toString()
            println("------------------>"+nowCeateProduct.ürün_resmi.toString())
            if (nowCeateProduct.ürün_resmi.toString()!!.isEmpty()) {

            } else {
                Picasso.with(itemView.context).load(nowCeateProduct.ürün_resmi.toString()).into(productImageAdp)

            }
        }
    }
}