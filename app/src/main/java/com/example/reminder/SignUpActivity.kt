package com.example.reminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.reminder.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        binding.SignUpButton.setOnClickListener {
            Log.d("Lab", "Sign up Button Clicked")

            //Authentication goes here

            //save loginstatus

            applicationContext.getSharedPreferences(
                    getString(R.string.sharedPreference),
                    Context.MODE_PRIVATE
            ).edit().putString("LoginStatus", 1.toString()).apply()
            startActivity(
                    Intent(applicationContext, MenuActivity::class.java))
    }
}
}