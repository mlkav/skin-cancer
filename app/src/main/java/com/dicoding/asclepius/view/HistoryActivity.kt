package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.asclepius.data.local.AppDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
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

        supportActionBar?.title = "Prediction History"

        adapter = HistoryAdapter()
        val spanCount = if (resources.configuration.screenWidthDp >= 600) 2 else 1
        binding.rvHistory.layoutManager = GridLayoutManager(this, spanCount)
        binding.rvHistory.adapter = adapter

        val database = AppDatabase.getDatabase(this)
        database.historyDao().getAllHistory().observe(this) { historyList ->
            if (historyList.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                adapter.submitList(emptyList())
            } else {
                binding.tvEmpty.visibility = View.GONE
                adapter.submitList(historyList)
            }
        }
    }
}
