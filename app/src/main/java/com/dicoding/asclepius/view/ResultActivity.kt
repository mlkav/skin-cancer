package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.dicoding.asclepius.databinding.ActivityResultBinding

import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.data.local.AppDatabase
import com.dicoding.asclepius.data.local.PredictionEntity
import kotlinx.coroutines.launch

import com.google.android.material.snackbar.Snackbar

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
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

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val resultText = intent.getStringExtra(EXTRA_RESULT)

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = resultText

        binding.resultText.alpha = 0f
        binding.resultText.animate().alpha(1f).setDuration(800).start()

        binding.saveButton.setOnClickListener {
            saveToHistory(imageUri.toString(), resultText ?: "")
        }
    }

    private fun saveToHistory(imageUri: String, result: String) {
        val database = AppDatabase.getDatabase(this)
        val historyDao = database.historyDao()
        val prediction = PredictionEntity(imageUri = imageUri, result = result)

        lifecycleScope.launch {
            historyDao.insert(prediction)
            showSnackbar()
            binding.saveButton.isEnabled = false
        }
    }

    private fun showSnackbar(message: String = "Result saved to history") {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}
