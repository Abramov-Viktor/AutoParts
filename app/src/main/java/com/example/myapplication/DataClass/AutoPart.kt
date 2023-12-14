package com.example.myapplication.DataClass

import android.os.Parcel
import android.os.Parcelable

data class AutoPart(
    val name: String = "",
    val article: String = "",
    val block: String = "",
    val description: String = "",
    val body: List<String> = listOf(),
    val image: String? = ""
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(article)
        parcel.writeString(block)
        parcel.writeString(description)
        parcel.writeStringList(body)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AutoPart> {
        override fun createFromParcel(parcel: Parcel): AutoPart {
            return AutoPart(parcel)
        }

        override fun newArray(size: Int): Array<AutoPart?> {
            return arrayOfNulls(size)
        }
    }
}


