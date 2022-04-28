package org.duckdns.mancitiss.testapplication

import android.graphics.Bitmap
import java.math.BigDecimal

class Product {
    var name: String? = null
    var short_description: String? = null
    var description: String? = null
    var price: BigDecimal? = null
    var category: String? = null
    var id: String? = null
    var quantity = 0

    var cardViewID: Int? = null
    var imageViewID: Int? = null
    var image: Bitmap? = null

    fun Product() {}

    fun Product(
        id: String?,
        name: String?,
        short_description: String?,
        category: String?,
        price: BigDecimal?,
        quantity: Int
    ) {
        this.id = id
        this.name = name
        this.short_description = short_description
        this.category = category
        this.price = price
        this.quantity = quantity
    }
}