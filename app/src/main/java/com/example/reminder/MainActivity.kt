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

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.SignIn.setOnClickListener {
            Log.d("Lab", "Sign in Button Clicked")
            //Authentication goes here
            if (checkLoginInfo() == 1) {
                //save loginstatus
                applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                ).edit().putInt("LoginStatus", 1).apply()
                //use getInt above after user has registered
                startActivity(
                        Intent(applicationContext, MenuActivity::class.java)
                )
            }/* else {
                checkLoginStatus()
            }*/
        }

        binding.textViewSignUp.setOnClickListener {
            Log.d("Signup", "Sign up Button Clicked")

            var signUpIntent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }
        }


    /*override fun onResume() {
        super.onResume()
        checkLoginStatus()
    }*/
    private fun checkLoginInfo(): Int {
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
        val loginStatus = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference),
                Context.MODE_PRIVATE
        ).getInt("LoginStatus", 0)
        if (loginStatus == 1) {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
        }
    }
}