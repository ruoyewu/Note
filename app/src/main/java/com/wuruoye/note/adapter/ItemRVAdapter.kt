package com.wuruoye.note.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wuruoye.note.R
import com.wuruoye.note.model.Config

/**
 * Created by wuruoye on 2017/5/28.
 * this file is to do
 */

class ItemRVAdapter(
        private val itemList: ArrayList<Int>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<ItemRVAdapter.ViewHolder>() , View.OnClickListener{

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item = itemList[p1]
        val string =
        if (item > 2000){
            Config.yearList[item - 2013]
        }else{
            Config.numList[item]
        }
        p0.tv.text = string

        p0.itemView.tag = item
        p0.itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        listener.onItemClick(v!!.tag as Int)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_show_item,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv = itemView.findViewById<TextView>(R.id.tv_item_item) as TextView
    }

    interface OnItemClickListener{
        fun onItemClick(item: Int)
    }
}
