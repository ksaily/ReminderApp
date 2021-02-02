package com.example.reminder

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.reminder.databinding.ProfileScreenBinding

class ProfileScreen : AppCompatActivity() {
    private lateinit var binding: ProfileScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileScreenBinding.inflate(layoutInflater)
        val view = binding.root
        val username = applicationContext.getSharedPreferences(
                getString(R.string.sharedPreference), Context.MODE_PRIVATE
        ).getString("username","")
        binding.showUserName.setText(username)
        setContentView(view)
    }
}