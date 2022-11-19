package com.example.personalbudget.Models

data class PieChartItem(
    var persent: Int,
    var name: String,
    var total: Long,
    var color: Int
) {
}