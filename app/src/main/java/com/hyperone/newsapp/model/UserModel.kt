package com.hyperone.newsapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserModel(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userName: String,

    val email: String,

    val password: String,

    val isLogged: Boolean = false,
)