package com.wuruoye.note.util

import android.content.Context
import com.droi.sdk.DroiError
import com.droi.sdk.core.*
import com.wuruoye.note.model.*
import com.wuruoye.note.util.NoteUtil.getDate
import com.wuruoye.note.util.NoteUtil.getTime
import com.wuruoye.note.util.SQLiteUtil.sortNoteList
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Created by wuruoye on 2017/6/4.
 * this file is to do
 */

/**
 * 备份方案
 * 1 全局备份
 * 先获取所有的 云端日记 和 本地日记
 * 先删除所有的跟本地日记同日期的云端日记，再将本地日记上传
 * 2 全局同步
 * 先获取所有 云端日记 和 本地日记
 * 将所有的 没有和本地日记有日期冲突的 保存到本地
 * 3 自动备份
 * 每次写完日记的时候，先将云端的 此日期的日记 删除，再上传当前编辑日记
 *
 * worn 数据容易丢失，没办法
 */
object BackupUtil{
    private fun getUser(noteCache: NoteCache): DroiUser?{
        val user = DroiUser.getCurrentUser()
        if (user != null && user.isLoggedIn && user.isAuthorized && !user.isAnonymous){
            return user
        }else if (noteCache.isLogin){
            val error = DroiError()
            DroiUser.login(noteCache.userName, noteCache.userPass, error)
            if (error.isOk){
                return user
            }
        }
        return null
    }

    private fun upNote2Note(cloudList: ArrayList<UpNote>): ArrayList<Note>{
        val list = ArrayList<Note>()
        for (i in cloudList){
            val year = i.year
            val month = i.month
            val day = i.day
            val week = NoteUtil.getWeek(year,month,day)
            val note = Note(year, month, day, week)
            note.style = i.color
            note.content = i.content
            if (i.direct != 0){
                note.direct = i.direct
            }
            if (i.bkFile != null){
                note.bkImage = "note_${i.year}-${i.month}-${i.day}"
            }
            note.setUpNote(i)
            list.add(note)
        }
        return sortNoteList(list)
    }

    fun backupNoteRemote(context: Context): String {
        val noteCache = NoteCache(context)
        val name: String
        val user = getUser(noteCache)
        if (user == null){
            return "备份失败，请重新登录"
        }else{
            name = user.userId
            val cond = DroiCondition.cond("user", DroiCondition.Type.EQ, name)
            val query = DroiQuery.Builder.newBuilder()
                    .query(UpNote::class.java)
                    .where(cond)
                    .build()
            val errorGet = DroiError()
            val list = query.runQuery<UpNote>(errorGet)
            val cloudList = ArrayList<UpNote>()
            if (errorGet.isOk){
                cloudList += list
            }
            val localList = SQLiteUtil.getAllNote(context)
            val deleteList = ArrayList<UpNote>()
            val saveList = ArrayList<UpNote>()

            for (i in localList){
                val note = UpNote()
                note.user = name
                note.color = i.style
                note.content = i.content
                note.year = i.year
                note.month = i.month
                note.day = i.day
                note.direct = i.direct
                if (i.bkImage != ""){
                    note.bkFile = DroiFile(File(Config.imagePath + i.bkImage))
                }
                saveList.add(note)
            }
            for (i in cloudList){
                val year = i.year
                val month = i.month
                val day = i.day
                for (j in saveList){
                    if (j.month == month && j.year == year && j.day == day){
                        deleteList.add(i)
                        break
                    }
                }
            }
            for (i in deleteList){
                if (i.bkFile != null){
                    i.bkFile.delete()
                }
                i.delete()
            }
            val errorSave = DroiObject.saveAll(saveList)
            if (errorSave.isOk){
                return "备份成功"
            }else{
                return errorSave.toString()
            }
        }
    }

    fun saveCloudNote(upNoteList: ArrayList<UpNote>, context: Context){
        for (i in upNoteList){
            val year = i.year
            val month = i.month
            val day = i.day
            val week = NoteUtil.getWeek(year,month,day)
            val note = Note(year, month, day, week)
            note.style = i.color
            note.content = i.content
            if (i.direct != 0){
                note.direct = i.direct
            }
            if (i.bkFile != null){
                note.bkImage = "note_${i.year}-${i.month}-${i.day}"
                if (i.bkFile != null){
                    val errorImage = DroiError()
                    val byteArray = i.bkFile.get(errorImage)
                    if (errorImage.isOk){
                        ImageCompressUtil.writeToFile(byteArray, "note_${i.year}-${i.month}-${i.day}")
                    }
                }
            }
            SQLiteUtil.saveNote(context,note)
        }
    }

    fun readBackupRemote(context: Context): ArrayList<Note>{
        val cloudList = ArrayList<UpNote>()
        val noteCache = NoteCache(context)
        val user = getUser(noteCache)
        if (user != null){
            val name = user.userId
            val cond = DroiCondition.cond("user", DroiCondition.Type.EQ, name)
            val query = DroiQuery.Builder.newBuilder()
                    .query(UpNote::class.java)
                    .where(cond)
                    .build()
            val errorGet = DroiError()
            val list = query.runQuery<UpNote>(errorGet)
            if (errorGet.isOk){
                cloudList += list
            }
        }
        return upNote2Note(cloudList)
    }

    fun backupNoteLocal(context: Context){
        val list = SQLiteUtil.getAllNote(context)
        val directoryName = Config.backupPath + getDate() + "/"
        val imageDirect = directoryName + "images/"
        val file = File(imageDirect)
        if (!file.exists()){
            file.mkdirs()
        }

        val jsonArray = JSONArray()
        for (i in list){
            val jsonObject = JSONObject()
            jsonObject.put("year", i.year)
            jsonObject.put("month", i.month)
            jsonObject.put("day", i.day)
            jsonObject.put("week", i.week)
            jsonObject.put("content", i.content)
            jsonObject.put("style", i.style)
            jsonObject.put("direct", i.direct)
            jsonObject.put("bkImage", i.bkImage)
            jsonArray.put(jsonObject)
            if (i.bkImage != ""){
                val pathFrom = Config.imagePath + i.bkImage
                val pathTo = imageDirect + i.bkImage
                FileUtil.transportFile(pathFrom, pathTo)
            }
        }
        FileUtil.writeText(directoryName + "out.json", jsonArray.toString())
    }

    fun downloadNoteLocal(path: String): ArrayList<Note>{
        val list = ArrayList<Note>()
        val directoryName = Config.backupPath + path
        val file = File(directoryName)
        for (i in file.listFiles()){
            if (i.isFile){
                val text = FileUtil.readText(i.absolutePath)
                val jsonArray = JSONArray(text)
                for (j in 0..jsonArray.length() - 1){
                    val jsonObject = jsonArray[j] as JSONObject
                    val year = jsonObject.getInt("year")
                    val month = jsonObject.getInt("month")
                    val day = jsonObject.getInt("day")
                    val week = jsonObject.getInt("week")
                    val content = jsonObject.getString("content")
                    val style = jsonObject.getInt("style")
                    val direct = jsonObject.getInt("direct")
                    val bkImage = jsonObject.getString("bkImage")
                    if (bkImage != ""){
                        val pathFrom = directoryName + "/image/" + bkImage
                        val pathTo = Config.imagePath + bkImage
                        FileUtil.transportFile(pathFrom, pathTo)
                    }
                    val note = Note(year, month, day, week, content, style, direct, bkImage)
                    list.add(note)
                }
            }else if (i.isDirectory){
                for (j in i.listFiles()){
                    FileUtil.transportFile(j.absolutePath, Config.imagePath + j.name)
                }
            }
        }
        return list
    }

    fun deleteNoteLocal(path: String){
        val file = File(Config.backupPath + path)
        if (file.isFile){
            file.delete()
        }else if (file.isDirectory){
            val list = file.listFiles()
            for (i in list){
                deleteNoteLocal(path + "/" + i.name)
            }
            file.delete()
        }
    }

    fun readBackupLocal(): ArrayList<Backup>{
        val list = ArrayList<Backup>()
        val directory = Config.backupPath
        val file = File(directory)
        for (i in file.listFiles()){
            if (i.isDirectory){
                val name = i.name
                var size = 0
                for (j in i.listFiles()){
                    if (j.isFile){
                        val text = FileUtil.readText(j.absolutePath)
                        val jsonArray = JSONArray(text)
                        size = jsonArray.length()
                    }
                }
                val backup = Backup(name, size)
                list.add(backup)
            }
        }
        return list
    }

    //保存本地日记的同时保存到云端
    fun upNote(context: Context, note: Note){
        val noteCache = NoteCache(context)
        val name: String
        val user = getUser(noteCache)
        if (user == null){

        }else{
            name = user.userId
            val cond1 = DroiCondition.cond("user",DroiCondition.Type.EQ,name)
            val query = DroiQuery.Builder.newBuilder()
                    .query(UpNote::class.java)
                    .where(cond1)
                    .build()
            var error = DroiError()
            val list = query.runQuery<UpNote>(error)
            if (error.isOk){
                for (i in list){
                    if (i.year == note.year && i.month == note.month && i.day == note.day){
                        if (i.bkFile != null){
                            i.bkFile.delete()
                        }
                        i.delete()
                    }
                }
            }
            val upNote = UpNote()
            upNote.user = name
            upNote.content = note.content
            upNote.color = note.style
            upNote.year = note.year
            upNote.month = note.month
            upNote.day = note.day
            upNote.direct = note.direct
            if (note.bkImage != ""){
                upNote.bkFile = DroiFile(File(Config.imagePath + note.bkImage))
            }
            upNote.save()
        }
    }
}
