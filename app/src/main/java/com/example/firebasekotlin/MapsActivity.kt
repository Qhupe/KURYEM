package com.example.firebasekotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add__product.*
import kotlinx.android.synthetic.main.activity_kullanici_ayarlari.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.userAdres
import kotlinx.android.synthetic.main.activity_register.yukleme
import kotlinx.android.synthetic.main.image_with_text.view.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var kullanici = FirebaseAuth.getInstance().currentUser
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var kullaniciAdi=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        var referans1 = FirebaseDatabase.getInstance().reference
        var sorgu = referans1.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (singleSnapshot in p0.children) {

                    var readuser = singleSnapshot.getValue(Kullanici::class.java)
                    kullaniciAdi=readuser?.kullanici_adi.toString()


                }

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                //lokasyon veya konum değişince yapılacak işlemler

                mMap.clear()
                val guncelKonum = LatLng(location.latitude,location.longitude)

                FirebaseDatabase.getInstance().reference
                    .child("location")
                    .child(kullaniciAdi)
                    .child("latitude")
                    .setValue(location.latitude)

                FirebaseDatabase.getInstance().reference
                    .child("location")
                    .child(kullaniciAdi)
                    .child("longitude")
                    .setValue(location.longitude)

                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Buradasınız"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))



            }
        }



        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            //izin verilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{
            //izin zaten verilmiş
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

        }



    }




}