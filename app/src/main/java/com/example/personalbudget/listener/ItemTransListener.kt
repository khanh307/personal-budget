package com.example.personalbudget.listener

import com.example.personalbudget.Models.TransModel

interface ItemTransListener {
    fun itemClicked(item: TransModel)
}