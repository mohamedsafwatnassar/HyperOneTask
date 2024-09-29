package com.hyperone.newsapp.localDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyperone.newsapp.authentication.model.UserModel

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel)

    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserModel?

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    suspend fun loginUser(email: String, password: String): UserModel?
}