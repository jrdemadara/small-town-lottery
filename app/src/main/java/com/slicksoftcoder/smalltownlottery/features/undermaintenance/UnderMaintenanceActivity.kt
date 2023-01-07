package com.slicksoftcoder.smalltownlottery.features.undermaintenance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.slicksoftcoder.smalltownlottery.R

class UnderMaintenanceActivity : AppCompatActivity() {
    private lateinit var buttonClose: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_under_maintenance)
        buttonClose = findViewById(R.id.buttonMaintenanceClose)
        buttonClose.setOnClickListener {
            finish()
        }
    }
}