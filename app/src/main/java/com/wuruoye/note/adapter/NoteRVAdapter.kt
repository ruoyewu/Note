package com.wuruoye.note.adapter

import android.annotation.SuppressLint
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NavUtils
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.wuruoye.note.R
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.util.NoteUtil

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class NoteRVAdapter(
        private val notes: ArrayList<Note>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<NoteRVAdapter.ViewHolder>(), View.OnClickListener {
    private var redColor = 0
    private var defaultColor = 0

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder?, p1: Int) {
        val note = notes[p1]
        p0!!.itemView.tag = note
        p0.itemView.setOnClickListener(this)
        p0.wait.setTextColor(defaultColor)
        p0.wait.text = "待"
        if (note.week == -1){
            p0.info.visibility = View.GONE
            p0.wait.visibility = View.VISIBLE
            p0.wait.text = "上个月"
        }else if (note.week == -2){
            p0.info.visibility = View.GONE
            if (note.month == NoteUtil.getMonth()){
                p0.wait.text = "明日再续"
            }else{
                p0.wait.text = "下个月"
            }
        }else{
            if (note.content != "" || note.style != 0) {
                p0.wait.visibility = View.GONE
                p0.info.visibility = View.VISIBLE
                p0.day.text = Config.numList[note.day]
                p0.title.text = note.content
                p0.week.text = "周${Config.weekList[note.week]}"
            } else {
                p0.info.visibility = View.GONE
                p0.wait.visibility = View.VISIBLE
                if (note.week == 1 || note.week == 7) {
                    p0.wait.setTextColor(redColor)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): ViewHolder {
        redColor = ActivityCompat.getColor(p0!!.context,R.color.carnation)
        defaultColor = ActivityCompat.getColor(p0.context,R.color.gray)
        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_note_1,p0,false)
        return ViewHolder(view)
    }

    override fun onClick(v: View?) {
        listener.onItemClick(v!!)
    }

    interface OnItemClickListener{
        fun onItemClick(v: View)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day = itemView.findViewById(R.id.note_1_day) as TextView
        val week = itemView.findViewById(R.id.note_1_week) as TextView
        val title = itemView.findViewById(R.id.note_1_title) as TextView
        val wait = itemView.findViewById(R.id.note_1_null) as TextView
        val info = itemView.findViewById(R.id.note_1_info) as LinearLayout
    }
}