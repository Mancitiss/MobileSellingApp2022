package org.duckdns.mancitiss.testapplication

import android.graphics.Bitmap
import java.math.BigDecimal

class Product {
    var name: String? = null
    var price: Long = 0
    var category: String? = null
    var id: String? = null
    var quantity = 0
    var description: String? = null
    var created: Long = 0
    var stars_1 = 0
    var stars_2 = 0
    var stars_3 = 0
    var stars_4 = 0
    var stars_5 = 0

    var cardViewID: Int? = null
    var imageViewID: Int? = null
    var image: Bitmap? = null

    fun Product() {}

    fun Product(
        id: String?,
        name: String?,
        category: String?,
        price: Long,
        quantity: Int
    ) {
        this.id = id
        this.name = name
        this.category = category
        this.price = price
        this.quantity = quantity
    }
}