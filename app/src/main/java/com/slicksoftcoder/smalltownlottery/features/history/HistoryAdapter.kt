package com.slicksoftcoder.smalltownlottery.features.history

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var mList: ArrayList<HistoryModel> = ArrayList()
    private var onClickItem: ((HistoryModel) -> Unit)? = null

    fun addItems(items: ArrayList<HistoryModel>) {
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (HistoryModel) -> Unit) {
        this.onClickItem = callback
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val drawDate: TextView = itemView.findViewById(R.id.textViewHistoryBetDate)
        val drawTime: TextView = itemView.findViewById(R.id.textViewHistoryBetDraw)
        val transactionCode: TextView = itemView.findViewById(R.id.textViewHistoryBetTranscode)
        val totalAmount: TextView = itemView.findViewById(R.id.textViewHistoryBetTotalAmount)
        var isVoid: TextView = itemView.findViewById(R.id.textViewHistoryBetStatus)

        fun bindView(historyModel: HistoryModel) {
            drawDate.text = historyModel.drawDate
            drawTime.text = historyModel.drawTime
            transactionCode.text = historyModel.transactionCode
            totalAmount.text = historyModel.totalAmount
            isVoid.text = historyModel.isVoid
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_history, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mPosition = mList[position]
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.bindView(mPosition)
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(mPosition)
        }

        holder.transactionCode.text = mPosition.transactionCode
        holder.drawDate.text = mPosition.drawDate
        holder.drawTime.text = mPosition.drawTime
        holder.totalAmount.text = formatter.format(mPosition.totalAmount.toDouble()).toString()
        if (mPosition.isVoid == "0") {
            holder.isVoid.text = "VALID"
            holder.isVoid.setTextColor(Color.parseColor("#3AAC26"))
        } else {
            holder.isVoid.text = "VOID"
            holder.isVoid.setTextColor(Color.parseColor("#D72E1F"))
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
