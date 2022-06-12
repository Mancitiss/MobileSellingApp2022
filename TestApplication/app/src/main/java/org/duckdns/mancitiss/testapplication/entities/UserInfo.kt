package org.duckdns.mancitiss.testapplication.entities

class UserInfo(name: String, phone: String, email: String) {
    var name: String = name
    var phone: String = phone
    var email: String = email

    constructor (): this("", "", "")
}