package com.example.slidegym.data.model

enum class WeekDay {
    SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO;

    companion object {
        fun fromIndex(index: Int): WeekDay = values()[index]
    }
}