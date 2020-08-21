    package com.application.shopassist

    import android.Manifest
    import android.annotation.SuppressLint
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.location.Geocoder
    import android.location.Location
    import android.location.LocationManager
    import android.net.ConnectivityManager
    import android.net.NetworkCapabilities
    import android.net.wifi.WifiManager
    import android.os.Build
    import android.os.Bundle
    import android.os.Looper
    import android.provider.Settings
    import android.view.Gravity
    import android.view.Menu
    import android.view.MenuItem
    import android.widget.Toast
    import com.google.android.material.bottomnavigation.BottomNavigationView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.navigation.findNavController
    import androidx.navigation.ui.AppBarConfiguration
    import androidx.navigation.ui.setupActionBarWithNavController
    import androidx.navigation.ui.setupWithNavController
    import com.application.shopassist.database.ShopAssistDatabase
    import com.application.shopassist.database.models.Favourites
    import com.application.shopassist.firebase.dao.FirebaseCallback
    import com.application.shopassist.firebase.GlobalProducts
    import com.application.shopassist.firebase.dao.ProductDataManager
    import com.application.shopassist.firebase.models.Product
    import com.google.android.gms.location.*
    import kotlinx.coroutines.*
    import java.util.*
    import kotlin.concurrent.schedule


    class MainActivity : AppCompatActivity() {

        val PERMISSION_ID = 42
        // Set default value for shared preferences
        val default = "Canada"
        lateinit var mFusedLocationClient: FusedLocationProviderClient

        @Suppress("DEPRECATION")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Declare fused location object
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val wifiManager: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (isConnected(this@MainActivity).not()) {
                // Turning on wifi if wifi is not on
                wifiManager.isWifiEnabled = true
            }

            // Get the location of user
            getLastLocation()

            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            val navController = findNavController(R.id.nav_host_fragment)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_favourites,
                    R.id.navigation_shoppinglist,
                    R.id.navigation_pricecomparison
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            navView.itemIconTintList = null
            getAndSetProductsFromFireBase()

        }

        @Suppress("DEPRECATION")
        @SuppressLint("MissingPermission")
        private fun getLastLocation() {
            if (checkPermissions()) {
                if (isLocationEnabled()) {

                    mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                                getCountry(location)
                        }
                    }
                } else {
                    val toast = Toast.makeText(this, "The application needs location for price comparison", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM,0,280)
                    toast.show()

                    Timer("SettingUp", false).schedule(2000) {
                        // Exit when there is no location available
                        System.exit(0)
                    }

                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
            } else {
                requestPermissions()
            }
        }

        @SuppressLint("MissingPermission")
        private fun requestNewLocationData() {
            var mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mLocationRequest.fastestInterval = 0
            mLocationRequest.numUpdates = 1

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
        }


        @Suppress("DEPRECATION")
        private val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var mLastLocation: Location = locationResult.lastLocation
                getCountry(mLastLocation)
            }
        }

        private fun isLocationEnabled(): Boolean {
            var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

        private fun checkPermissions(): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }
        private fun requestPermissions() {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID
            )
        }
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            if (requestCode == PERMISSION_ID) {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLastLocation()
                }
                else{
                    val toast = Toast.makeText(this, "The application needs location for price comparison", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.BOTTOM,0,280)
                    toast.show()

                    Timer("SettingUp", false).schedule(2000) {
                        // Exit when there is no location available
                        System.exit(0)
                    }
                }
            }
        }

        // function to check for internet connection
        @Suppress("DEPRECATION")
        fun isConnected(context: Context): Boolean {
            var result = false
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = true
                        }
                    }
                }
            }
            return result
        }
        fun getCountry(location: Location){
            val gcd = Geocoder(this, Locale.getDefault())
            val addresses =
                gcd.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses.size > 0) {
                val sharedPreferences =
                    getSharedPreferences("Country_file", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("Country", addresses.get(0).getCountryName())
                editor.commit()

                // Changing user location
                if (sharedPreferences.getString(
                        "Country",
                        default
                    ) != "Canada"
                ) {
                    editor.putString("Country", "Canada")
                    editor.commit()
                }
            }
        }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.mymenu, menu)
            return super.onCreateOptionsMenu(menu)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.item1 -> {
                    var intent: Intent = Intent(this, Help::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.item2 -> {
                    var intent: Intent = Intent(this, AboutUs::class.java)
                    startActivity(intent)
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }

        private fun getAndSetProductsFromFireBase() {
            ProductDataManager.getProducts(object :
                FirebaseCallback {
                override fun onAsyncDataRetrieval(list: MutableList<Product>) {
                    GlobalProducts.productList = list
                }
            })
        }
    }
