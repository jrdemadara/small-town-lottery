package com.slicksoftcoder.smalltownlottery.features.bet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R

class BetAdapter : RecyclerView.Adapter<BetAdapter.ViewHolder>()  {
    private var mList: ArrayList<BetDetailsModel> = ArrayList()
    private var onClickItem: ((BetDetailsModel) -> Unit)? = null

    fun addItems(items: ArrayList<BetDetailsModel>){
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (BetDetailsModel)->Unit){
        this.onClickItem = callback

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val betNUmber: TextView = itemView.findViewById(R.id.textViewBetNumber)
        val win: TextView = itemView.findViewById(R.id.textViewBetWin)
        val amount: TextView = itemView.findViewById(R.id.textViewBetAmount)
        val type: TextView = itemView.findViewById(R.id.textViewBetType)

        fun bindView(betDetailsModel: BetDetailsModel){
            betNUmber.text= betDetailsModel.betNumber
            win.text = betDetailsModel.win
            amount.text = betDetailsModel.amount
            type.text = betDetailsModel.isRambolito
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_bets, parent, false)
        )

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val mPosition = mList[position]
        holder.bindView(mPosition)
        holder.itemView.setOnClickListener{onClickItem?.invoke(mPosition)}
        holder.betNUmber.text = mPosition.betNumber
        holder.win.text = mPosition.win
        holder.amount.text = mPosition.amount
        holder.type.text = mPosition.isRambolito
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}