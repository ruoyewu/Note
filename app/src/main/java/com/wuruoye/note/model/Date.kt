package com.wuruoye.note.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by wuruoye on 2017/6/10.
 * this file is to do
 */

class Date (
        var year:  Int,
        var month: Int,
        var day: Int
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Date> = object : Parcelable.Creator<Date> {
            override fun createFromParcel(source: Parcel): Date = Date(source)
            override fun newArray(size: Int): Array<Date?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readInt(),
    source.readInt(),
    source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(year)
        dest.writeInt(month)
        dest.writeInt(day)
    }
}
