package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class PostActivity : ComponentActivity() {

    private lateinit var postsContainer: LinearLayout
    private val posts = mutableListOf<PostItem>()
    val apiService = ApiClient.create(this)
    var profileId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        postsContainer = findViewById(R.id.posts_container)

        findViewById<Button>(R.id.add_post_btn).setOnClickListener {
            openAddEditDialog(null)
        }

        val intent: Intent = intent
        profileId = intent.getStringExtra("profileId")

        loadPosts()
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun loadPosts() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }
        apiService.getPosts().enqueue(object : Callback<PostListResponse> {
            override fun onResponse(call: Call<PostListResponse>, response: Response<PostListResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        posts.clear()
                        responseBody.result.forEach {
                            posts.add(
                                PostItem(
                                    it.id,
                                    it.content,
                                    it.title,
                                    it.author,
                                    it.created_at,
                                    it.updated_at,
                                )
                            )
                        }
                        refreshPost()
                    }
                } else {
                    // Handle unsuccessful response (e.g., server errors)
                    Toast.makeText(this@PostActivity, "Failed to retrieve posts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PostListResponse>, t: Throwable) {

                if (t is IOException) {
                    // Handle network errors
                    Toast.makeText(this@PostActivity, "Network error", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle other errors
                    Toast.makeText(this@PostActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun refreshPost() {
        postsContainer.removeAllViews()
        for (post in posts) {
            val postView = layoutInflater.inflate(R.layout.activity_post_item, null)
            val postDescription = postView.findViewById<TextView>(R.id.post_description)
            val postTitle = postView.findViewById<TextView>(R.id.post_title)
            postDescription.text = post.content
            postTitle.text = post.title

            postView.findViewById<Button>(R.id.edit_post_btn).setOnClickListener {
                openAddEditDialog(post)
            }

            postView.findViewById<Button>(R.id.remove_post_btn).setOnClickListener {
                posts.remove(post)
                apiService.deletePost(post.id.toString()).enqueue(object : Callback<PostItem> {
                    override fun onResponse(call: Call<PostItem>, response: Response<PostItem>) {
                        if (response.isSuccessful) {
                            loadPosts()
                            refreshPost()
                        }
                    }

                    override fun onFailure(call: Call<PostItem>, t: Throwable) {
                        // Handle network errors or other failures
                        // For example, show an error message
                        Toast.makeText(this@PostActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            postsContainer.addView(postView)
        }
    }

    private fun openAddEditDialog(post: PostItem?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_post, null)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.post_description_input)
        val titleInput = dialogView.findViewById<EditText>(R.id.post_title_input)

        if (post != null) {
            descriptionInput.setText(post.content)
            titleInput.setText(post.title)
        }

        AlertDialog.Builder(this)
            .setTitle(if (post == null) "Add Post" else "Edit Post")
            .setView(dialogView)
            .setPositiveButton("Save") { dialog, _ ->
                val description = descriptionInput.text.toString()
                val title = titleInput.text.toString()

                if (true) {
                    if (post == null) {
                        val newPost = PostItem(null, description, title, author = 1, null, null)
                        apiService.addPost(newPost).enqueue(object : Callback<PostItem> {
                            override fun onResponse(call: Call<PostItem>, response: Response<PostItem>) {
                                if (response.isSuccessful) {
                                    loadPosts()
                                }
                            }

                            override fun onFailure(call: Call<PostItem>, t: Throwable) {

                                // Handle network errors or other failures
                                // For example, show an error message
                                Toast.makeText(this@PostActivity, "Network error", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        post.content = description
                        post.title = title

                        apiService.editPost(post.id, post).enqueue(object : Callback<PostItem> {
                            override fun onResponse(call: Call<PostItem>, response: Response<PostItem>) {
                                if (response.isSuccessful) {
                                    this@PostActivity.loadPosts()
                                    this@PostActivity.refreshPost()
                                }
                            }

                            override fun onFailure(call: Call<PostItem>, t: Throwable) {
                                // Handle network errors or other failures
                                // For example, show an error message
                                Toast.makeText(this@PostActivity, "Network error", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }


                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }
}
