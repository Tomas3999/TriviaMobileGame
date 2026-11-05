package com.fei.semestralnezadanie.enums

enum class ResponseCode(val code: Int, val message: String) {
    SUCCESS(0, "Úspech"),
    NO_RESULTS(1, "Pre zvolenú kombináciu nie je k dispozícii dostatok otázok."),
    INVALID_PARAMETER(2, "Zadaný parameter požiadavky je neplatný."),
    TOKEN_NOT_FOUND(3, "Relačný token nebol nájdený."),
    TOKEN_EMPTY(4, "Všetky dostupné otázky pre tento filter už boli vyčerpané."),
    RATE_LIMIT(5, "Príliš veľa požiadaviek naraz. Počkajte chvíľu a skúste to znova."),
    UNKNOWN(-1, "Nastala neznáma chyba API.");

    companion object {
        fun fromCode(code: Int): ResponseCode =
            entries.find { it.code == code } ?: UNKNOWN
    }
}