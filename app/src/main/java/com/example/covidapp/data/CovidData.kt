package com.example.covidapp.data

data class CovidData(
    val errors: List<Any>,
    val `get`: String,
    val parameters: Parameters,
    val response: List<Response>,
    val results: Int
)