package com.example.firebasekotlin

class Ürünler {
    var product_name:String?=null
    var product_price:String?=null
    var product_color:String?=null
    var product_size:String?=null
    var product_pieces:String?=null
    var ürün_resmi : String? = null
    var ürün_information : String?=null
    var product_id: String? = null
    var kullanici_id: String? = null

    constructor(
        product_name:String,
        product_price:String,
        product_color:String,
        product_size:String,
        product_pieces:String,
        ürün_information:String,
        ürün_resmi:String,
        product_id:String,
        user_id: String
    ) {
        this.product_name=product_name
        this.product_price=product_price
        this.product_color=product_color
        this.product_size=product_size
        this.product_pieces=product_pieces
        this.ürün_information=ürün_information
        this.ürün_resmi=ürün_resmi
        this.product_id=product_id
        this.kullanici_id = user_id
    }

    constructor()
}