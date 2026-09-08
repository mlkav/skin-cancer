package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.remote.Article
import com.dicoding.asclepius.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
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

        val article = IntentCompat.getParcelableExtra(intent, EXTRA_ARTICLE, Article::class.java)
        article?.let { populateDetail(it) }
    }

    private fun populateDetail(article: Article) {
        binding.tvDetailTitle.text = article.title
        binding.tvDetailAuthor.text = article.author ?: "Unknown Author"
        binding.tvDetailDescription.text = article.description
        binding.tvDetailContent.text = article.content

        Glide.with(this)
            .load(article.urlToImage)
            .into(binding.ivDetailImage)

        binding.btnOpenWeb.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, article.url.toUri())
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }
}
