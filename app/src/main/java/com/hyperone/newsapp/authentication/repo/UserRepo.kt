package com.hyperone.newsapp.authentication.repo

import com.hyperone.newsapp.authentication.model.UserModel
import com.hyperone.newsapp.localDatabase.dao.UserDao
import com.hyperone.newsapp.network.DataHandler
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val userDao: UserDao
) {

    suspend fun registerUser(user: UserModel): DataHandler<String> {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                userDao.insertUser(user)
                DataHandler.Success("User registered successfully")
            } else {
                DataHandler.Error("User with this email already exists")
            }
        } catch (e: Exception) {
            DataHandler.ServerError(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun loginUser(email: String, password: String): DataHandler<UserModel> {
        return try {
            val user = userDao.loginUser(email, password)
            if (user != null) {
                DataHandler.Success(user)
            } else {
                DataHandler.Error("Invalid email or password or not found this user")
            }
        } catch (e: Exception) {
            DataHandler.ServerError(e.message ?: "An unknown error occurred")
        }
    }
}