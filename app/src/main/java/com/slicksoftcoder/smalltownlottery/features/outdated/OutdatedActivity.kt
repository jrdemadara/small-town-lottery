package com.slicksoftcoder.smalltownlottery.features.outdated

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.databinding.ActivityAutdatedBinding

class OutdatedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAutdatedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAutdatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonOutdatedClose.setOnClickListener {
            finish()
        }
    }
}
