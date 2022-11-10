package com.example.personalbudget.Models

data class TransModel(
    var id: String,
    var date: String,
    var time: String,
    var name: AddItem?,
    var note: String,
    var money: Long,
    var account: AddItem?,
    var collectmoney: Long = 0,
    var isTitle: Boolean = false,

    ): java.io.Serializable{
}