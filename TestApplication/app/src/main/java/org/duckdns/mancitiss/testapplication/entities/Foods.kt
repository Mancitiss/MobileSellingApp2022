package org.duckdns.mancitiss.testapplication.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.duckdns.mancitiss.testapplication.Product
import java.io.Serializable

@Entity(tableName = "Food")
data class Foods(
    @PrimaryKey(autoGenerate = false)
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "price")
    var price:Long,

    @ColumnInfo(name = "img")
    var img: Bitmap?,

    @ColumnInfo(name = "product")
    var product: Product
) : Serializable
{
    constructor(product: Product): this(product.id, product.name, product.price, product.image, product)
}