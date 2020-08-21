package com.application.shopassist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage


class AboutUs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view
        setContentView(R.layout.activity_about_us)

        // Action bar
        val actionbar = supportActionBar

        // Set action bar title
        actionbar!!.title = "About us"

        //Set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        // Set about page
        val aboutPage: View = AboutPage(this)
            .isRTL(false)
            .setDescription("Five Individuals with a passion for creativity.As a team we are continuously working on providing the best service to our customers ")
            .setImage(R.drawable.splashimage)
            .addGroup("Connect with us")
            .addEmail("support@shopassit.com")
            .addFacebook("facebook_link")
            .addTwitter("twitter_link")
            .addYoutube("youtube_link")
            .addPlayStore("com.application.shopassist")
            .addInstagram("instagramlink")
            .create()

        setContentView(aboutPage)
    }

    // Back button functionality
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
