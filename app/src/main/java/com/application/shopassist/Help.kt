package com.application.shopassist

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.shopassist.database.models.DataModel


class Help : AppCompatActivity() {
    var itemsData = ArrayList<DataModel>()
    lateinit var adapter: RVAdapter
    lateinit var mcontext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_displaytext)

        // Action bar
        val actionbar = supportActionBar

        // Set action bar title
        actionbar!!.title = "Help and support"

        //Set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        mcontext = this.baseContext

        adapter = RVAdapter(itemsData)
        val llm = LinearLayoutManager(this)

        val item: RecyclerView = findViewById(R.id.itemsrv)

            item.setHasFixedSize(true)
            item.layoutManager = llm
            getData()
            item.adapter = adapter
    }

    private fun getData() {
        itemsData = ArrayList()
        itemsData = Data.items
        adapter.notifyDataSetChanged()
        adapter = RVAdapter(itemsData)
    }

    // Back button functionality
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
