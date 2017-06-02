package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.transitionseverywhere.Fade
import com.transitionseverywhere.Slide
import com.transitionseverywhere.TransitionManager
import com.transitionseverywhere.TransitionSet
import com.wuruoye.note.R
import com.wuruoye.note.adapter.ItemRVAdapter
import com.wuruoye.note.adapter.NoteRVAdapter
import com.wuruoye.note.base.BaseActivity
import com.wuruoye.note.base.IAbsView
import com.wuruoye.note.model.Config
import com.wuruoye.note.model.Note
import com.wuruoye.note.model.NoteCache
import com.wuruoye.note.presenter.NoteGet
import com.wuruoye.note.util.NoteUtil
import com.wuruoye.note.util.SQLiteUtil
import com.wuruoye.note.util.toast
import com.wuruoye.note.widget.SpringScrollView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() ,NoteRVAdapter.OnItemClickListener,View.OnClickListener{
    private lateinit var noteGet: NoteGet
    private lateinit var noteCache: NoteCache

    private var mYear = 0
    private var mMonth = 0
    private var isClose = false
    private var isSearch = false
    private var search = ""
    private var isUpDirect = true



    private val noteView = object : IAbsView<ArrayList<Note>>{
        override fun setModel(model: ArrayList<Note>) {
            setNote(model)
        }

        override fun setWorn(message: String) {
            toast(message)
        }

    }
    private val dragListener = object : SpringScrollView.OnDragListener{
        override fun onUpDrag() {
            upMonth(true)
        }

        override fun onDownDrag() {
            upMonth(false)
        }

    }

    override val contentView: Int
        get() = R.layout.activity_main

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        mMonth = NoteUtil.getMonth()
        mYear = NoteUtil.getYear()
    }

    override fun initPresenter() {
        noteGet = NoteGet(this)
        presenterList.add(noteGet)
        viewList.add(noteView)

        super.initPresenter()
    }

    override fun initView() {
        getNote()

        tv_note_month.text = Config.numList[mMonth]
        tv_note_year.text = Config.yearList[mYear - FIRST_YEAR]

        sv_note.setOnCloseListener {
            openSearch(false)
            true
        }
        sv_note.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                search = p0!!
                getNote()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.e("ruoyenote",p0)
                search = p0!!
                getNote()
                return false
            }

        })

        ssv_note.setDragListener(dragListener)
        tv_note_year.setOnClickListener(this)
        tv_note_month.setOnClickListener(this)
        tv_note_write.setOnClickListener(this)
        tv_note_close.setOnClickListener(this)
        tv_note_search.setOnClickListener(this)
        tv_note_setting.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            WRITE_NOTE -> {
                if (resultCode == Activity.RESULT_OK){
                    getNote()
                }
            }
            START_SETTING -> {
                if (resultCode == Activity.RESULT_OK){
                    recreate()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.tv_note_close -> {
                isClose = !isClose
                openSearch(false)
                tv_note_close.text = if (isClose) "展" else "折"
            }
            R.id.tv_note_write -> {
                val year = NoteUtil.getYear()
                val month = NoteUtil.getMonth()
                val day =  NoteUtil.getDay()
                val note = SQLiteUtil.isContain(this,year,month,day)
                if (note != null){
                    toWriteNote(note)
                }else{
                    toWriteNote(Note(year,month,day,NoteUtil.getWeek()))
                }
            }
            R.id.tv_note_year -> {
                showPopMenu(tv_note_year)
            }
            R.id.tv_note_month -> {
                showPopMenu(tv_note_month)
            }
            R.id.tv_note_search -> {
                if (isSearch){
                    openSearch(false)
                }else{
                    openSearch(true)
                }
            }
            R.id.tv_note_setting -> {
                val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        tv_note_write,getString(R.string.translate_note_button))
                ActivityCompat.startActivityForResult(this,
                        Intent(this,SettingActivity::class.java), START_SETTING,compat.toBundle())
            }
        }
    }

    override fun onItemClick(v: View) {
        val note = v.tag as Note
        if (note.week == -1 || note.week == -2){
            if (note.week == -1) {
                upMonth(false)
            }else{
                upMonth(true)
            }
        }else {
            toWriteNote(note)
        }
    }

    override fun onLongItemClick(v: View) {
        val viewHolder = v.getTag(R.id.note_view_holder) as NoteRVAdapter.ViewHolder
        AlertDialog.Builder(this)
                .setTitle("是否删除日记?")
                .setPositiveButton("是") { _, _ ->
                    val note = v.tag as Note
                    SQLiteUtil.deleteNote(applicationContext,note)
                    note.style = 0
                    note.content = ""
                    setView(note,viewHolder)
                }
                .setNegativeButton("否") { _, _ -> }
                .show()
    }

    override fun onBackPressed() {
        if (isSearch){
            openSearch(false)
        }else {
            super.onBackPressed()
        }
    }

    private fun upMonth(b: Boolean){
        isUpDirect = !b
        if (b){
            if (mMonth == NoteUtil.getMonth() && mYear == NoteUtil.getYear()){

            }else{
                if (mMonth == 12){
                    if (mYear == NoteUtil.getYear()){

                    }else{
                        mMonth = 1
                        mYear ++
                    }
                }else{
                    mMonth ++
                }
                tv_note_year.text = Config.yearList[mYear - FIRST_YEAR]
                tv_note_month.text = Config.numList[mMonth]
                getNote()
            }
        }else{
            if (mMonth == 1){
                mMonth = 12
                mYear --
            }else{
                mMonth --
            }
            tv_note_year.text = Config.yearList[mYear - FIRST_YEAR]
            tv_note_month.text = Config.numList[mMonth]
            getNote()
        }
    }

    private fun onYorMClick(v: View, num: Int){
        when (v.id){
            R.id.tv_note_year -> {
                mYear = num
                if (mYear == NoteUtil.getYear()){
                    if (mMonth > NoteUtil.getMonth()){
                        mMonth = NoteUtil.getMonth()
                    }
                }

                tv_note_year.text = Config.yearList[num - FIRST_YEAR]
                tv_note_month.text = Config.numList[mMonth]
            }
            R.id.tv_note_month -> {
                mMonth = num
                tv_note_month.text = Config.numList[num]
            }
        }
        getNote()
    }

    @SuppressLint("InflateParams")
    private fun showPopMenu(v: View){
        val popWindow = PopupWindow(this)
        val view = LayoutInflater.from(this).inflate(R.layout.item_popup_window,null)
        val rv =  view.findViewById(R.id.rv_show_item) as RecyclerView
        val adapter = ItemRVAdapter(getItemList(v),object : ItemRVAdapter.OnItemClickListener{
            override fun onItemClick(item: Int) {
                   onYorMClick(v,item)
                popWindow.dismiss()
            }
        })
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        popWindow.contentView  = view
        popWindow.isFocusable = true
        popWindow.showAsDropDown(v,0,-100)
    }

    private fun openSearch(isOpen: Boolean){
        when (isOpen) {
            false -> {
                isSearch = false
                sv_note.visibility = View.GONE
                getNote()
            }
            true -> {
                isSearch = true
                sv_note.visibility = View.VISIBLE
                sv_note.isIconified = false
                sv_note.queryHint = "可输入关键字，天数"
                getNote()
            }
        }
    }

    private fun getNote(){
        if (isSearch){
            noteGet.requestNote(mMonth,mYear,search)
        }else{
            noteGet.requestNote(mMonth,mYear,isClose)
        }
    }

    private fun getItemList(v: View): ArrayList<Int>{
        val list = ArrayList<Int>()
        when (v.id){
            R.id.tv_note_year -> {
                var start = FIRST_YEAR
                while (start <= NoteUtil.getYear()){
                    list.add(start)
                    start ++
                }
            }
            R.id.tv_note_month -> {
                var start = 1
                val maxMonth =
                if (mYear == NoteUtil.getYear()){
                    NoteUtil.getMonth()
                }else{
                    12
                }
                while (start <= maxMonth){
                    list.add(start)
                    start ++
                }
            }
        }
        return list
    }

    private fun toWriteNote(note: Note){
        val intent = Intent(this,WriteActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("note",note)
        intent.putExtras(bundle)
        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,tv_note_write,getString(R.string.translate_note_button))
        ActivityCompat.startActivityForResult(this,intent, WRITE_NOTE,compat.toBundle())
//        val pair2 = android.support.v4.util.Pair<View,String>(tv_note_write,getString(R.string.translate_note_button))
//        val compat: ActivityOptionsCompat =
//        if (v != null){
//            val pair1 = Pair<View,String>(v,getString(R.string.translate_note))
//            ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair1,pair2)
//        }else{
//            ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair2)
//        }
    }

    private fun setNote(noteList: ArrayList<Note>){
        val set =
                when (isUpDirect){
                    false -> {
                        TransitionSet()
                                .addTransition(Fade(Fade.MODE_OUT))
                                .addTransition(Slide(Gravity.BOTTOM))
                    }
                    else -> {
                        TransitionSet()
                                .addTransition(Fade(Fade.MODE_OUT))
                                .addTransition(Slide(Gravity.TOP))
                    }
                }
        TransitionManager.beginDelayedTransition(ssv_note,set)
        val layout = object : LinearLayoutManager(this){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        layout.isAutoMeasureEnabled = true
        rv_note.layoutManager = layout
        rv_note.adapter = NoteRVAdapter(noteList,this)
        ssv_note.post {
            val y = ssv_note.measuredHeight
            ssv_note.scrollBy(0,y * 10)
//            if (noteList.size > 2) {
//                rv_note.scrollToPosition(noteList.size - if (isClose) 1 else 2)
////                rv_note.smoothScrollToPosition(noteList.size - if (isClose) 1 else 2)
//            }
        }
    }


    private fun setView(note: Note, p0: NoteRVAdapter.ViewHolder){
        p0.wait.setTextColor(ActivityCompat.getColor(this,R.color.gray))
        if (NoteUtil.isToday(note.year,note.month,note.day)){
            p0.wait.text = "待"
        }else{
            p0.wait.text = "逝"
        }
        if (note.week == -1){
            p0.info.visibility = View.GONE
            p0.wait.visibility = View.VISIBLE
            p0.wait.text = "上个月"
        }else if (note.week == -2){
            p0.info.visibility = View.GONE
            p0.wait.visibility = View.VISIBLE
            if (note.month == NoteUtil.getMonth() && note.year == NoteUtil.getYear()){
                p0.wait.text = "明日再续"
            }else{
                p0.wait.text = "下个月"
            }
        }else{
            if (note.content != "" || note.style != 0) {
                val item = noteCache.itemShow
                var day = ""
                var week = ""
                if (item == 1){
                    day = Config.numList[note.day]
                    week = "周${Config.weekList[note.week]}"
                }else if (item == 2){
                    day = note.day.toString()
                    week = "周${Config.weekList[note.week]}"
                }else if (item == 3){
                    day = Config.numList[note.month] + "月" + Config.numList[note.day] + "日"
                    week = "星期${Config.weekList[note.week]}"
                }else if (item == 4){
                    day = Config.yearList[note.year - 2013] + "年" +
                            Config.numList[note.month] + "月" +
                            Config.numList[note.day] + "日"
                    week = "星期${Config.weekList[note.week]}"
                }else if (item == 5){
                    day = note.month.toString() + "月" + note.day + "日"
                    week = "星期" + Config.weekList[note.week]
                }else if (item == 6){
                    day = note.month.toString() + "月" + note.day + "日"
                    week = ""
                }
                p0.wait.visibility = View.GONE
                p0.info.visibility = View.VISIBLE
                p0.day.text = day
                p0.title.text = note.content
                p0.week.text = week
            } else {
                p0.info.visibility = View.GONE
                p0.wait.visibility = View.VISIBLE
                if (note.week == 1 || note.week == 7) {
                    p0.wait.setTextColor(ActivityCompat.getColor(this,R.color.carnation))
                }
            }
        }
    }

    companion object{
        val FIRST_YEAR = 2013
        val WRITE_NOTE = 1
        val START_SETTING = 2
    }
}
