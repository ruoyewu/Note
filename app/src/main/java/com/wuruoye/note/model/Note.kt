package com.wuruoye.note.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by wuruoye on 2017/5/27.
 * this file is to do
 */
class Note (
        var id: Int,
        var style: Int,
        var direct: Int,
        var content: String,
        var year: Int,
        var month: Int,
        var day: Int,
        var week: Int
) : Parcelable {
    constructor(): this(0, 0, 1, "", 0, 0, 0, 0)

    constructor(year: Int, month: Int, day: Int, week: Int): this(0, 0, 1, "", year, month, day, week)

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Note> = object : Parcelable.Creator<Note> {
            override fun createFromParcel(source: Parcel): Note = Note(source)
            override fun newArray(size: Int): Array<Note?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readInt(),
    source.readInt(),
    source.readInt(),
    source.readString(),
    source.readInt(),
    source.readInt(),
    source.readInt(),
    source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeInt(style)
        dest.writeInt(direct)
        dest.writeString(content)
        dest.writeInt(year)
        dest.writeInt(month)
        dest.writeInt(day)
        dest.writeInt(week)
    }
}