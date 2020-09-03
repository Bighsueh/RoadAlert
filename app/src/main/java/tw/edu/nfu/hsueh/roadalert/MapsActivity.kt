package tw.edu.nfu.hsueh.roadalert

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.pow


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    var orilocation : Location? = null
    var tml : Location? = null
    private var cnt = 0

    val TAG = "MapsActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
        else{
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }


    override fun onMapReady(googleMap: GoogleMap) {


        if (ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap = googleMap
        aim = LatLng(23.702713,120.425073)
        googleMap.addMarker(
            MarkerOptions()
                .position(aim).draggable(true)
        )

        locationManager()
        //顯示裝置的位子 小藍點
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(23.702713,120.425073),15f))

        mMap.isMyLocationEnabled = true
        mMap.isTrafficEnabled = true
        mMap.setOnMarkerClickListener { marker ->//雙擊Marker清理畫面
            cnt += 1
            if (cnt >1){
                mMap.clear()
                mMap.addMarker(
                    MarkerOptions()
                        .position(aim).draggable(true)
                )
                drawMarker()
            }
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }
        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(arg0:Marker) {
            }

            override fun onMarkerDragEnd(arg0: Marker) {
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arg0.position, 18f))
                val message = arg0.position.latitude.toString() + "" + arg0.position.longitude.toString()
                aim = LatLng(arg0.position.latitude,arg0.position.longitude)
                cnt = 0
//                var lntLng = LatLng(orilocation!!.latitude, orilocation!!.longitude)
//                Toast.makeText(this@MapsActivity, "距離目的地%.2f".format(dista(aim,lntLng)), Toast.LENGTH_LONG).show()
                locationManager()

                //Toast.makeText(this@MapsActivity, "%s,%s".format(arg0!!.position.latitude.toString(),arg0.position.longitude.toString()), Toast.LENGTH_LONG).show()

            }

            override fun onMarkerDrag(arg0: Marker?) {
                // val message = arg0!!.position.latitude.toString() + "" + arg0.position.longitude.toString()
                //Toast.makeText(this@MapsActivity, "%s,%s".format(arg0!!.position.latitude.toString(),arg0.position.longitude.toString()), Toast.LENGTH_SHORT).show()
            }
        })

    }




    lateinit var aim : LatLng
    //標記位置
    fun  drawMarker()
    {
        var lntLng = LatLng(orilocation!!.latitude, orilocation!!.longitude)
        val rectOptions: PolylineOptions = PolylineOptions()
        Toast.makeText(this, "距離目的地%.2f公里".format(dista(aim,lntLng)), Toast.LENGTH_LONG).show()

//        mMap.addMarker(MarkerOptions().position(lntLng).title(getNowTimeDetail())).showInfoWindow()
        rectOptions.add(lntLng)
        rectOptions.add(aim)

        mMap.addPolyline(rectOptions)


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lntLng,15f))
        //Toast.makeText(this, "改變位置", Toast.LENGTH_LONG).show()
    }
    fun getNowTimeDetail(): String? {
        val sdf = SimpleDateFormat("MM月dd日H時mm分ss秒")
        return sdf.format(Date())
    }
    fun dista(a:LatLng,b:LatLng):Double
    {
        var la = ((a.latitude-b.latitude) * 111).pow(2)
        var lo = ((a.longitude-b.longitude) * 111.320).pow(2)
        var ans = (la+lo).pow(0.5)
        return ans
    }

    private fun locationManager()
    {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var isNETWORKEnable = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!(isNETWORKEnable || isGPSEnable))
        {
            Toast.makeText(this, "並未開啟任何定位服務", Toast.LENGTH_SHORT).show()
        }
        else
        {
            try {
                if(isGPSEnable)
                {
                    //註冊 LocationManager 要向哪個服務取得位置更新資訊
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000L,10f,locationListener)
                    //取得上一次的定位
                    orilocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                else if(isNETWORKEnable)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000L,10f,locationListener)
                    orilocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            } catch (ex:SecurityException)
            {
                Log.d(TAG,ex.message.toString())
            }
            if(orilocation != null) {
                drawMarker()
            }
        }
    }

    //listener locationChange
    private val locationListener: LocationListener = object : LocationListener
    {
        override fun onLocationChanged(location: Location) {
            if(orilocation == null)
            {
                orilocation = location
                drawMarker()
            }
            else
            {
                orilocation = location
                drawMarker()
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0f))
        }
    }
    private  val REQUEST_PERMISSIONS = 1

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isEmpty()) return
        when(requestCode){
            REQUEST_PERMISSIONS -> {
                for (result in grantResults)
                    if(result != PackageManager.PERMISSION_GRANTED)
                        finish()
                    else {
                        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

                        map.getMapAsync(this)
                    }
            }
        }
    }



}




