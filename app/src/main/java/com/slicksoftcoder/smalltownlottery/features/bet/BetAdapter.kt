package com.slicksoftcoder.smalltownlottery.features.bet

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R

class BetAdapter : RecyclerView.Adapter<BetAdapter.ViewHolder>() {
    private var mList: ArrayList<BetDetailsModel> = ArrayList()
    private var onClickItem: ((BetDetailsModel) -> Unit)? = null

    fun addItems(items: ArrayList<BetDetailsModel>) {
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (BetDetailsModel) -> Unit) {
        this.onClickItem = callback
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val betNUmber: TextView = itemView.findViewById(R.id.textViewBetNumber)
        val win: TextView = itemView.findViewById(R.id.textViewBetWin)
        val amount: TextView = itemView.findViewById(R.id.textViewBetAmount)
        val type: TextView = itemView.findViewById(R.id.textViewBetType)

        fun bindView(betDetailsModel: BetDetailsModel) {
            betNUmber.text = betDetailsModel.betNumber
            amount.text = betDetailsModel.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_bets, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mPosition = mList[position]
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.bindView(mPosition)
        holder.itemView.setOnClickListener { onClickItem?.invoke(mPosition) }
        holder.win.text = formatter.format(mPosition.win.toDouble()).toString()
        holder.amount.text = mPosition.amount
        if (mPosition.isLowWin == "0") {
            holder.betNUmber.text = "#" + mPosition.betNumber
            holder.type.text = "REGULAR"
        } else {
            holder.betNUmber.text = "#" + mPosition.betNumber + "-LW"
            holder.type.text = "LOW WIN"
        }
        if (mPosition.isRambolito == "1") {
            holder.betNUmber.text = "#" + mPosition.betNumber + "-R"
            holder.type.text = "RAMBOLITO"
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}
