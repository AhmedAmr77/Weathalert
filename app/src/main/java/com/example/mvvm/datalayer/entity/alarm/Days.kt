package com.example.mvvm.datalayer.entity.alarm

data class Days(
    var sat: Boolean = false,
    var sun: Boolean = false,
    var mon: Boolean = false,
    var tue: Boolean = false,
    var wed: Boolean = false,
    var thu: Boolean = false,
    var fri: Boolean = false
) {
    fun getDays(): List<String>{
        var days = mutableListOf<String>()
        if (sat) days.add("sat")
        if (sun) days.add("sun")
        if (mon) days.add("mon")
        if (tue) days.add("tue")
        if (wed) days.add("wed")
        if (thu) days.add("thu")
        if (fri) days.add("fri")
        return days
    }
}