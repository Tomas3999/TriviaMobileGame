package com.fei.semestralnezadanie.enums

enum class GameMode(val id: Int) {
    CLASSIC(0),
    MARATHON(1);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: CLASSIC
    }
}