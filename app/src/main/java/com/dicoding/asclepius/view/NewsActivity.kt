package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.ApiConfig
import com.dicoding.asclepius.data.remote.NewsResponse
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        supportActionBar?.title = "Cancer News"

        adapter = NewsAdapter()
        val spanCount = if (resources.configuration.screenWidthDp >= 600) 2 else 1
        binding.rvNews.layoutManager = GridLayoutManager(this, spanCount)
        binding.rvNews.adapter = adapter

        fetchNews()
    }

    private fun fetchNews() {
        binding.progressBar.visibility = View.VISIBLE
        val client = ApiConfig.getApiService().getCancerNews(apiKey = BuildConfig.API_KEY)
        client.enqueue(
            object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.submitList(it.articles)
                        }
                    } else {
                        Toast.makeText(this@NewsActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@NewsActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            },
        )
    }
}
