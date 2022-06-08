package org.duckdns.mancitiss.testapplication

import android.graphics.Bitmap
import java.io.Serializable

class Product () : Serializable{
    var name: String = ""
    var price: Long = 0
    var category: String = ""
    var id: String = ""
    var quantity = 0
    var description: String = ""
    var created: Long = 0
    var stars: Long = 0
    var ratingCount: Long = 0

    var averageStars: Float = 0f

    var avatar: String = ""
    var image: Bitmap? = null

    //constructor() : this() {}

    constructor(
        id: String,
        name: String,
        category: String,
        price: Long,
        quantity: Int
    ) : this() {
        this.id = id
        this.name = name
        this.category = category
        this.price = price
        this.quantity = quantity
    }
}