package com.example.reminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.reminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //Log in screen

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.SignIn.setOnClickListener {
            Log.d("Lab", "Sign in Button Clicked")
            //checkLoginInfo will check if the username & password are correct
            //they are retrieved from shared preferences

            if (checkLoginInfo() == 1) {
                //save loginstatus
                applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                ).edit().putInt("LoginStatus", 1).apply()
                startActivity(
                        //Move to menu
                        Intent(applicationContext, MenuActivity::class.java)
                )
            }
        }

        binding.textViewSignUp.setOnClickListener {
            Log.d("Signup", "Sign up Button Clicked")
            //Move to sign up screen where the user can create a profile
            var signUpIntent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }
    }


    override fun onResume() {
        //if the user has already signed in, move straight to menu screen
        super.onResume()
        checkLoginStatus()
    }
    private fun checkLoginInfo(): Int {
        //check username and password from shared preferences, return 1 if found
        val username = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("username", "")
        val password = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getString("password", "")
        val editUsername = binding.editUsername.getText().toString()
        val editPassword = binding.editPassword.getText().toString()
        println("Username and password" + editUsername + editPassword)
        if (editUsername == username && editPassword == password) {
            Log.d("Signedin", "Sign in was successful")
            return 1
        }
        else {
            val text = "Incorrect username or password, try again."
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(applicationContext, text, duration)
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
            return 0
        }
    }

    private fun checkLoginStatus() {
        //check if the user has previously logged in
        val loginStatus = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1) {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }
    }
}