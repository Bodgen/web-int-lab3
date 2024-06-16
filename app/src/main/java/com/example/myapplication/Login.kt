package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.login_button)
        val apiService = ApiClient.create(this)

        loginButton.setOnClickListener {
            val loginPayload = LoginRequest(usernameField.text.toString(), passwordField.text.toString())

            apiService.login(loginPayload).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Handle successful login response
                        val responseBody = response.body()
                        if (responseBody != null) {
                            // Save the token in SharedPreferences
                            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", responseBody.token) // Assuming your User model has a token field
                            editor.apply()

                            // Start the next activity
                            val intent = Intent(this@LoginActivity, PostActivity::class.java)
                            intent.putExtra("profileId", responseBody.profileId)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Handle unsuccessful login response
                        // For example, show an error message
                        Toast.makeText(this@LoginActivity, "Wrong credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("E", "", t)
                    // Handle network errors or other failures
                    // For example, show an error message
                    Toast.makeText(this@LoginActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
