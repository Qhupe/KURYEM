package com.example.firebasekotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MapsActivityUser : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var kuryeUserName=""
    var latituderef=0.0
    var longituderef=0.0
    var guncelKonum: LatLng? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_user)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

         kuryeUserName= intent.getStringExtra("kuryeUserName").toString()
        println("-----Aktivitiden Gelen Veri-------->"+kuryeUserName)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        println("-----Aktivitiden Gelen Veri-------->"+kuryeUserName)

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                //lokasyon veya konum değişince yapılacak işlemler
                println("----------------->Locationn"+location.latitude.toString())
                println(location.longitude.toString())

                mMap.clear()

                var referans = FirebaseDatabase.getInstance().reference

                var sorgu = referans.child("location")
                    .orderByKey()
                    .equalTo(kuryeUserName)

                sorgu.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        for (singleSnapshot in p0.children) {

                            var readuser = singleSnapshot.getValue(Locations::class.java)

                            longituderef= readuser?.longitude!!
                            latituderef=readuser?.latitude!!
                             guncelKonum= LatLng(latituderef,longituderef)

                            mMap.addMarker(MarkerOptions().position(guncelKonum).title(kuryeUserName+" Konumu"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
                // Add a marker in Sydney and move the camera
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