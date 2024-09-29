package com.fintechhub.democalender

import java.util.Date

data class Event(
    var id: Long,
    var title: String,
    var description:String,
    var date: Date,
    var reminder:Boolean,
    var startTime:String,
    var endTime:String
)