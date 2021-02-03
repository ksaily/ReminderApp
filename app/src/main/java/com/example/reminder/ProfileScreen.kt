package com.example.reminder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reminder.databinding.ProfileScreenBinding

class ProfileScreen : AppCompatActivity() {
    //User profile can be seen in this page
    private lateinit var binding: ProfileScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileScreenBinding.inflate(layoutInflater)
        val view = binding.root
        val username = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("username","")
        binding.showUserName.text = username
        setContentView(view)
        binding.LogOutBtn.setOnClickListener {
            //Log out
            var mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
            applicationContext.getSharedPreferences(
                    getString(R.string.sharedPreference),
                    Context.MODE_PRIVATE
            ).edit().putInt("LoginStatus", 0).apply()
            startActivity(mainActivityIntent)
        }
    }
}