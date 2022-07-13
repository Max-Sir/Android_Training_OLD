package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities

enum class PetStatus(val value: String) {
    AVAILABLE("available"),
    PENDING("pending"),
    SOLD("sold"),
    ALL("available,pending,sold")
}