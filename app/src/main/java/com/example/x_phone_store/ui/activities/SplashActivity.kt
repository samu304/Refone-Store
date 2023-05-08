package com.example.x_phone_store.ui.activities

import FirestoreClass
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.x_phone_store.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        @Suppress("DEPRECATION")
        (Handler()).postDelayed(
            {
                var currentUserID = FirestoreClass().getCurrentUserID()
                try{

                    if(currentUserID.isNotEmpty()){
                        startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                        finish()
                    }else{
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }


/*
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()*/

        },
            1500
        )

        /*val typeface: Typeface = Typeface.createFromAsset(assets, "Montserrat-Regular.ttf")
        tv_app_name.typeface = typeface*/

    }
}