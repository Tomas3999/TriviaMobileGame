package com.fei.semestralnezadanie.enums

enum class Difficulty(val id: Int) {
    ANY(0),
    EASY(1),
    MEDIUM(2),
    HARD(3);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: ANY
    }
}