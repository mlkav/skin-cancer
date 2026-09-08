package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            startCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherCrop = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                currentImageUri = resultUri
                showImage()
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            showToast(cropError?.message ?: "Crop error")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
        binding.historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.newsButton.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_info -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg"))
        val uCropIntent = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .getIntent(this)
        launcherCrop.launch(uCropIntent)
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.alpha = 0f
            binding.previewImageView.setImageURI(it)
            binding.previewImageView.animate().alpha(1f).setDuration(500).start()
        }
    }

    private fun analyzeImage() {
        val uri = currentImageUri
        if (uri != null) {
            binding.progressIndicator.visibility = android.view.View.VISIBLE
            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        binding.progressIndicator.visibility = android.view.View.GONE
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?) {
                        binding.progressIndicator.visibility = android.view.View.GONE
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val topCategory = it[0].categories[0]
                                val label = topCategory.label
                                val scoreRaw = topCategory.score
                                val scoreDisplay = NumberFormat.getPercentInstance().format(scoreRaw)
                                
                                // Jika skor di bawah 60%, beri peringatan bahwa gambar mungkin tidak valid
                                if (scoreRaw < 0.6f) {
                                    showToast("Hasil mungkin tidak akurat. Pastikan gambar yang diunggah adalah gambar kulit.")
                                }
                                
                                moveToResult(uri, label, scoreDisplay)
                            }
                        }
                    }
                },
            )
            imageClassifierHelper.classifyStaticImage(uri)
        } else {
            showToast(getString(R.string.no_image_selected))
        }
    }

    private fun moveToResult(uri: Uri, label: String, score: String) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            putExtra(ResultActivity.EXTRA_RESULT, "$label $score")
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
