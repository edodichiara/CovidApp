package com.example.covidapp.data

data class Cases(
    val `1M_pop`: String,
    val active: Int,
    val critical: Int,
    val new: Int,
    val recovered: Int,
    val total: Int
)