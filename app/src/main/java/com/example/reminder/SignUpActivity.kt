package com.example.reminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.example.reminder.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    //Page where user can sign up
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.SignUpButton.setOnClickListener {
            Log.d("Signup", "Sign up Button Clicked")
            saveData()
            if (saveData() == 1) {
                var mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }
        }
    }

        private fun saveData(): Int {
            val username = binding.createUsername.text.toString()
            val password = binding.createPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                //If the username and is inserted, save them to shared preferences
                applicationContext.getSharedPreferences(
                        getString(R.string.sharedPreference),
                        Context.MODE_PRIVATE
                ).edit().putString("username", username).putString("password", password).apply()
                println("Created Username and password" + username + password)
                return 1

            }
            else {
                val text = "Write your desired username and password"
                val duration = Toast.LENGTH_SHORT

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                toast.show()
                return 0
            }
         }



}