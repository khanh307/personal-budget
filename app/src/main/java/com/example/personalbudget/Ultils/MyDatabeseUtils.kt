package com.example.personalbudget.Ultils

import android.graphics.Color

object MyDatabeseUtils {
    val DATABASE_NAME: String = "personal_budget.db"
    val TABLE_TRANS = "trans"
    val TABLE_TITLE = "title"
    val KEY_TYPE_TRANS: String = "idtype"
    val KEY_ACCOUNT_TRANS: String = "idaccount"
    val KEY_NOTE_TRANS = "note"
    val KEY_DATE_TRANS = "date"
    val KEY_TIME_TRANS = "time"
    val KEY_MONEY_TRANS = "money"
    val KEY_TYPE_COLL_TRANS = "idtypecollect"

    val PIE_COLORS = intArrayOf(
        Color.parseColor("#5be5ce"), Color.parseColor("#ff7551"), Color.parseColor("#27cbb0"),
        Color.parseColor("#015cab"), Color.parseColor("#fee487"),
        Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
        Color.rgb(191, 134, 134), Color.rgb(179, 48, 80)
    )
}