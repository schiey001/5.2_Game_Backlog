package com.example.gamebacklog.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "games")
@Parcelize
data class Game(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "gameTitle")
    var gameTitle: String,

    @ColumnInfo(name = "gamePlatform")
    var gamePlatform: String,

    @ColumnInfo(name = "day")
    var day: String,

    @ColumnInfo(name = "month")
    var month: String,

    @ColumnInfo(name = "year")
    var year: String

) : Parcelable {

}