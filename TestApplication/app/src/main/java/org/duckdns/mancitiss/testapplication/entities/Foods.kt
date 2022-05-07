package org.duckdns.mancitiss.testapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Food")
data class Foods(
    @PrimaryKey(autoGenerate = true)
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "price")
    var price:Float,

    @ColumnInfo(name = "img")
    var img: Int
) : Serializable