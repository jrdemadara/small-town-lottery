package com.slicksoftcoder.smalltownlottery.features.history

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R

class HistoryBetAdapter : RecyclerView.Adapter<HistoryBetAdapter.ViewHolder>() {
    private var mList: ArrayList<HistoryBetModel> = ArrayList()
    private var onClickItem: ((HistoryBetModel) -> Unit)? = null

    fun addItems(items: ArrayList<HistoryBetModel>) {
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (HistoryBetModel) -> Unit) {
        this.onClickItem = callback
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val betNUmber: TextView = itemView.findViewById(R.id.textViewHistoryBetNumber)
        val win: TextView = itemView.findViewById(R.id.textViewHistoryBetWin)
        val amount: TextView = itemView.findViewById(R.id.textViewHistoryBetAmount)
        val type: TextView = itemView.findViewById(R.id.textViewHistoryBetType)

        fun bindView(historyBetModel: HistoryBetModel) {
            betNUmber.text = historyBetModel.betNumber
            amount.text = historyBetModel.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_history_bets, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mPosition = mList[position]
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.bindView(mPosition)
        holder.itemView.setOnClickListener { onClickItem?.invoke(mPosition) }
        holder.win.text = formatter.format(mPosition.win.toDouble()).toString()
        holder.amount.text = mPosition.amount
        if (mPosition.isRambolito == "0") {
            holder.betNUmber.text = "#" + mPosition.betNumber
            holder.type.text = "REGULAR"
        } else if (mPosition.isRambolito == "1") {
            holder.betNUmber.text = "#" + mPosition.betNumber + "-R"
            holder.type.text = "RAMBOLITO"
        } else {
            holder.betNUmber.text = "#" + mPosition.betNumber
            holder.type.text = "NO WIN"
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
