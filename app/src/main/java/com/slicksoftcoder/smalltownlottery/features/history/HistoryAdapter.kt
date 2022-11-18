package com.slicksoftcoder.smalltownlottery.features.history

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.slicksoftcoder.smalltownlottery.R

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolder>()  {
    private var mList: ArrayList<HistoryModel> = ArrayList()
    private var onClickItem: ((HistoryModel) -> Unit)? = null

    fun addItems(items: ArrayList<HistoryModel>){
        this.mList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (HistoryModel)->Unit){
        this.onClickItem = callback

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
         val drawDate: TextView = itemView.findViewById(R.id.textViewHistoryDate)
         val drawTime: TextView = itemView.findViewById(R.id.textViewHistoryDraw)
         val transactionCode: TextView = itemView.findViewById(R.id.textViewHistoryTranscode)
         val totalAmount: TextView = itemView.findViewById(R.id.textViewHistoryAmount)
         var isVoid: TextView = itemView.findViewById(R.id.textViewHistoryStatus)

        fun bindView(historyModel: HistoryModel){
            drawDate.text= historyModel.drawDate
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

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val mPosition = mList[position]
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.bindView(mPosition)
        holder.itemView.setOnClickListener{onClickItem?.invoke(mPosition)}
        holder.transactionCode.text = mPosition.transactionCode
        holder.drawDate.text = mPosition.drawDate
        holder.drawTime.text = mPosition.drawTime
        holder.totalAmount.text = formatter.format(mPosition.totalAmount.toDouble()).toString()
        if(mPosition.isVoid == "0"){
            holder.isVoid.text = "VALID"
        }else{
            holder.isVoid.text = "VOID"
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}