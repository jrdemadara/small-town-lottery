package com.slicksoftcoder.smalltownlottery.features.outdated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.slicksoftcoder.smalltownlottery.databinding.ActivityOutdatedBinding

class OutdatedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutdatedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutdatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonOutdatedClose.setOnClickListener {
            finish()
        }
    }
}
