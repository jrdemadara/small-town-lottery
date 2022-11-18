package com.slicksoftcoder.smalltownlottery.features.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.slicksoftcoder.smalltownlottery.R
import com.slicksoftcoder.smalltownlottery.features.bet.BetActivity
import com.slicksoftcoder.smalltownlottery.features.bet.BetAdapter
import com.slicksoftcoder.smalltownlottery.features.dashboard.DashboardActivity
import com.slicksoftcoder.smalltownlottery.server.LocalDatabase

class HistoryActivity : AppCompatActivity() {
    private lateinit var localDatabase: LocalDatabase
    private lateinit var recyclerView: RecyclerView
    private var adapter: HistoryAdapter? = null
    private lateinit var floatingButtonHistoryBack: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        localDatabase = LocalDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewHistory)
        floatingButtonHistoryBack = findViewById(R.id.floatingButtonHistoryBack)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HistoryAdapter()
        recyclerView.adapter = adapter
        retrieveHistory()
        floatingButtonHistoryBack.setOnClickListener {
            val intent = Intent(this, DashboardActivity ::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun retrieveHistory(){
        val list = localDatabase.retrieveHistory()
        adapter?.addItems(list)
    }
}