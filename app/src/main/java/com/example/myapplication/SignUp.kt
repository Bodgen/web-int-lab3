package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val firstNameField: EditText = findViewById(R.id.first_name)
        val lastNameField: EditText = findViewById(R.id.last_name)
        val emailField: EditText = findViewById(R.id.email)
        val passwordField: EditText = findViewById(R.id.password)
        val sexSpinner: Spinner = findViewById(R.id.sex)
        val birthDateField: EditText = findViewById(R.id.birth_date)
        val signUpButton: Button = findViewById(R.id.signup_button)
        val apiService = ApiClient.create(this)
        // Set up the spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.sex_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sexSpinner.adapter = adapter
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            val registrationPayload = RegistrationRequest(
                firstNameField.text.toString() + lastNameField.text.toString(),
                emailField.text.toString(),
                birthDateField.text.toString(),
                passwordField.text.toString()
            )

            apiService.register(registrationPayload).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        // Handle successful login response
                        val responseBody = response.body()
                        // Do something with the response
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle unsuccessful login response
                        // For example, show an error message
                        Toast.makeText(this@SignUpActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("E", "", t)
                    // Handle network errors or other failures
                    // For example, show an error message
                    Toast.makeText(this@SignUpActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
