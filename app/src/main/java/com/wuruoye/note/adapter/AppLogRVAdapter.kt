package com.wuruoye.note.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.wuruoye.note.R
import com.wuruoye.note.model.AppLog

/**
 * Created by wuruoye on 2017/6/25.
 * this file is to do
 */

class AppLogRVAdapter (
        private val logList: ArrayList<AppLog>
) : RecyclerView.Adapter<AppLogRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder? {
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_app_log, viewGroup, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val log = logList[i]
        viewHolder.tvInfo.text = log.info
        viewHolder.tvVersion.text = "版本号：${log.version}"
    }

    override fun getItemCount(): Int {
        return logList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvVersion: TextView = itemView.findViewById<TextView>(R.id.tv_app_log_version)
        var tvInfo: TextView = itemView.findViewById<TextView>(R.id.tv_app_log_info) as TextView
    }
}
