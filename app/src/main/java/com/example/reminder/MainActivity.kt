package com.example.reminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        /*private lateinit var binding: ActivityMainBinding
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.SignIn.setOnClickListener {
            Log.d("Lab", "Login Button Clicked")

            //Authentication goes here

            //save loginstatus

            applicationContext.getSharedPreferences(
                //Replace shared preference with its key
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
            ).edit().putInt("LoginStatus", 1).apply()
            startActivity(
                Intent(applicationContext, MenuActivity::class.java)
            )
        }*/

        findViewById<TextView>(R.id.textViewSignUp).setOnClickListener {
            var signupIntent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(signupIntent)
        }
    }
}