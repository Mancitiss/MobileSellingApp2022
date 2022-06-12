package org.duckdns.mancitiss.testapplication.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Category")
data class Categories(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "img")
    var img: Int
) : Serializable