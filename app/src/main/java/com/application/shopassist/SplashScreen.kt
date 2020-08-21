package com.application.shopassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.os.Handler
import android.content.Intent

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hiding title bar
        window.requestFeature(Window.FEATURE_NO_TITLE)
        // activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash_screen)

        //4second splash time
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            //finish this activity
            finish()
        },1000)

    }

}
