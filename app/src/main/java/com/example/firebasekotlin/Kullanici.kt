package com.example.firebasekotlin

class Kullanici {
    var isim: String? = null
    var kullanici_adi: String? = null
    var e_mail: String? = null
    var telefon: String? = null
    var adres : String? = null
    var profil_resmi: String? = null
    var ürün_resmi : String? = null
    var seviye: String? = null
    var kullanici_id: String? = null

    constructor(
        isim: String,
        kullanici_adi: String,
        e_mail: String,
        telefon: String,
        adres : String,
        profil_resmi: String,
        ürün_resmi:String,
        seviye: String,
        user_id: String
    ) {
        this.isim = isim
        this.kullanici_adi = kullanici_adi
        this.e_mail = e_mail
        this.telefon = telefon
        this.adres=adres
        this.profil_resmi = profil_resmi
        this.ürün_resmi=ürün_resmi
        this.seviye = seviye
        this.kullanici_id = user_id
    }

    constructor()
}