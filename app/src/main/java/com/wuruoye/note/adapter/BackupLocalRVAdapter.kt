package com.wuruoye.note.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.wuruoye.note.R
import com.wuruoye.note.model.Backup

import java.util.ArrayList

/**
 * Created by wuruoye on 2017/6/19.
 * this file is to do
 */

class BackupLocalRVAdapter(
        private val backupList: ArrayList<Backup>,
        private val listener: OnItemClickListener) :
        RecyclerView.Adapter<BackupLocalRVAdapter.ViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_backup, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemView.setOnClickListener(this)
        val backup = backupList[i]
        viewHolder.itemView.tag = backup.name

        with(viewHolder){
            tvTime.text = "备份时间: " + backup.name
            tvSize.text = "日记数目: " + backup.size.toString()
        }
    }

    override fun getItemCount(): Int {
        return backupList.size
    }

    override fun onClick(v: View) {
        listener.onItemClick(v)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTime: TextView = itemView.findViewById<TextView>(R.id.tv_item_backup_time)
        var tvSize: TextView = itemView.findViewById<TextView>(R.id.tv_item_backup_size)

    }
}
