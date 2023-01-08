package com.slicksoftcoder.smalltownlottery.features.timeauto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.slicksoftcoder.smalltownlottery.databinding.ActivityTimeAutoBinding

class TimeAutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeAutoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeAutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonTimeAutoClose.setOnClickListener {
            finish()
        }
    }
}
