package com.wuruoye.note.view

import android.annotation.SuppressLint
import android.app.Activity
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.wuruoye.note.util.*
import com.wuruoye.note.widget.SpringScrollView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(),
        NoteRVAdapter.OnItemClickListener,
        View.OnClickListener{
    private lateinit var noteGet: NoteGet
    private lateinit var noteCache: NoteCache

    //year {from 2013 to currentYear}
    private var mYear = 0
    //month {from 1 to 12}
    private var mMonth = 0
    //save the search text
    private var search = ""
    //show the animator direct, from up to down or from down to up
    private var isUpDirect = true
    //show current state { EXPEND, CLOSE, SEARCH }
    private lateinit var currentState: State
    private lateinit var mNoteAdapter: NoteRVAdapter

    private enum class State{
        CLOSE,
        EXPEND,
        SEARCH
    }

    private val noteView = object : IAbsView<ArrayList<Note>>{
        override fun setModel(model: ArrayList<Note>) {
            setNote(model)
        }

        override fun setWorn(message: String) {
            toast(message)
        }
    }
    private val mDragListener = object : SpringScrollView.OnDragListener{
        override fun onUpDrag() {
            if (currentState == State.EXPEND) {
                upMonth(true)
            }
        }

        override fun onDownDrag() {
            if (currentState == State.EXPEND) {
                upMonth(false)
            }
        }

        override fun onLeftDrag() {
        }

        override fun onRightDrag() {

        }

    }

    override val contentView: Int
        get() = R.layout.activity_main

    override fun initData(bundle: Bundle?) {
        noteCache = NoteCache(this)
        mMonth = NoteUtil.getMonth()
        mYear = NoteUtil.getYear()
        currentState =
                when (noteCache.autoState){
                    0 -> State.EXPEND
                    1 -> State.CLOSE
                    2 -> State.SEARCH
                    else -> State.EXPEND
                }
        val isOpen = bundle?.getBoolean("isOpen")
        if (isOpen != null && isOpen == true){

        }else{
            if (noteCache.isLock){
                startActivity(Intent(this, LockActivity::class.java))
                finish()
            }
        }
        UpdateUtil.requestUtil(applicationContext)
    }

    override fun initPresenter() {
        noteGet = NoteGet(this)
        presenterList.add(noteGet)
        viewList.add(noteView)

        super.initPresenter()
    }

    override fun initView() {
        getNote()
        initYearAndMonth()
//        tv_note_month.text = Config.numList[mMonth]
//        tv_note_year.text = Config.yearList[mYear - FIRST_YEAR]

        sv_note.setOnCloseListener {
            currentState = getState(noteCache.autoState)
            changeState(currentState)
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

        ssv_note.setDragListener(mDragListener)
//        tv_note_year.setOnClickListener(this)
//        tv_note_month.setOnClickListener(this)
        tv_note_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val year = Config.yearList.indexOf(getItemList(1)[position]) + FIRST_YEAR
                if (mYear != year){
                    mYear = year
                    if (year == NoteUtil.getYear()){
                        if (mMonth > NoteUtil.getMonth()){
                            mMonth = NoteUtil.getMonth()
                        }
                    }
                    initYearAndMonth()
                    getNote()
                }
            }

        }
        tv_note_month.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val month = Config.numList.indexOf(getItemList(2)[position])
                if (mMonth != month){
                    mMonth = month
                    initYearAndMonth()
                    getNote()
                }
            }

        }

        tv_note_write.setOnClickListener(this)
        tv_note_close.setOnClickListener(this)
        tv_note_search.setOnClickListener(this)
        tv_note_setting.setOnClickListener(this)

        checkBackup()
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
                currentState =
                when (currentState){
                    State.EXPEND -> {
                        State.CLOSE
                    }
                    State.CLOSE -> {
                        State.EXPEND
                    }
                    else -> {
                        getState(noteCache.autoState)
                    }
                }
                changeState(currentState)
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
                if (currentState == State.SEARCH){
                    currentState = getState(noteCache.autoState)
                }else {
                    currentState = State.SEARCH
                }
                changeState(currentState)
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
                    mNoteAdapter.setView(note, viewHolder)
                }
                .setNegativeButton("否") { _, _ -> }
                .show()
    }

    override fun onBackPressed() {
        if (currentState == State.SEARCH){
            currentState = getState(noteCache.autoState)
            changeState(currentState)
        }else {
            super.onBackPressed()
        }
    }

    private fun initYearAndMonth(){
        tv_note_year.adapter = ArrayAdapter(this, R.layout.item_pop_spinner, getItemList(1))
        tv_note_month.adapter = ArrayAdapter(this, R.layout.item_pop_spinner, getItemList(2))
        tv_note_year.setSelection(mYear - FIRST_YEAR)
        tv_note_month.setSelection(mMonth - 1)
    }

    private fun checkBackup(){
        if (noteCache.isAutoBackup){
            if (System.currentTimeMillis() - noteCache.lastBackup > 1000 * 60 * 60 * 12){
                Thread({
                    BackupUtil.backupNoteRemote(applicationContext)
                    runOnUiThread {
                        noteCache.lastBackup = System.currentTimeMillis()
                    }
                }).start()
            }
        }
    }

    private fun upMonth(b: Boolean){
        isUpDirect = !b
        val data = NoteUtil.getMonthUp(b, mYear, mMonth)
        mYear = data[0]
        mMonth = data[1]
        if (mYear == NoteUtil.getYear() && mMonth > NoteUtil.getMonth()){
            mMonth --
        }else{
            initYearAndMonth()
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

//                tv_note_year.text = Config.yearList[num - FIRST_YEAR]
//                tv_note_month.text = Config.numList[mMonth]
            }
            R.id.tv_note_month -> {
                mMonth = num
//                tv_note_month.text = Config.numList[num]
            }
        }
        getNote()
    }

    @SuppressLint("InflateParams")
    private fun showPopMenu(v: View){
        val popWindow = PopupWindow(this)
        val view = LayoutInflater.from(this).inflate(R.layout.item_popup_window,null)
        val rv =  view.findViewById<RecyclerView>(R.id.rv_show_item) as RecyclerView
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

    private fun changeState(state: State){
        when (state){
            State.EXPEND -> {
                sv_note.visibility = View.GONE
                tv_note_close.text = "折"
            }
            State.CLOSE -> {
                sv_note.visibility = View.GONE
                tv_note_close.text = "展"
            }
            State.SEARCH -> {
                sv_note.visibility = View.VISIBLE
                sv_note.isIconified = false
                sv_note.queryHint = "可输入关键字，天数"
            }
        }
        getNote()
    }

    private fun getNote(){
        when (currentState){
            State.EXPEND -> {
                noteGet.requestNote(mMonth, mYear, false)
            }
            State.CLOSE -> {
                noteGet.requestNote(mMonth, mYear, true)
            }
            State.SEARCH -> {
                noteGet.requestNote(search)
            }
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

    private fun getItemList(flag: Int): ArrayList<String>{
        val list = ArrayList<String>()
        if (flag == 1){
            var start = FIRST_YEAR
            while (start <= NoteUtil.getYear()){
                list.add(Config.yearList[start - 2013])
                start ++
            }
        }else if (flag == 2){
            var start = 1
            val maxMonth =
                    if (mYear == NoteUtil.getYear()){
                        NoteUtil.getMonth()
                    }else{
                        12
                    }
            while (start <= maxMonth){
                list.add(Config.numList[start])
                start ++
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
        mNoteAdapter = NoteRVAdapter(noteList, this)
        rv_note.adapter = mNoteAdapter
        ssv_note.post {
            ssv_note.scrollBy(0, ssv_note.measuredHeight * 10)
        }
    }

    private fun getState(state: Int): State{
        return when (state){
            0 -> State.EXPEND
            1 -> State.CLOSE
            2 -> State.SEARCH
            else -> State.EXPEND
        }
    }

    companion object{
        val FIRST_YEAR = 2013
        val WRITE_NOTE = 1
        val START_SETTING = 2
    }
}
