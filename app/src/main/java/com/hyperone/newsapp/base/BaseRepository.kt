package com.hyperone.newsapp.base

import android.app.Application
import android.util.Log
import com.hyperone.newsapp.R
import com.hyperone.newsapp.network.DataHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

open class BaseRepository @Inject constructor() {

    @Inject
    lateinit var appContext: Application
    inline fun <T> performApiCall(
        crossinline apiCall: suspend () -> Response<T>,
        parseErrorResponse: Boolean = true // pending variable for handle error on all Api has same error response
    ): Flow<DataHandler<T>> =
        flow {
            emit(DataHandler.ShowLoading)
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    emit(DataHandler.Success(response.body()))
                } else {
                    when (response.code()) {
                        500 -> {
                            emit(DataHandler.ServerError(appContext.getString(R.string.server_error)))
                        }

                        else -> {
                            if (parseErrorResponse) {
                                emit(
                                    DataHandler.Error(
                                        parseErrorDefaultResponse(response.errorBody())
                                    )
                                )
                            } else emit(
                                DataHandler.Error(
                                    parseErrorCustomResponse(
                                        response.errorBody()?.string().toString()
                                    ),
                                )
                            )
                        }
                    }
                }
                emit(DataHandler.HideLoading)
            } catch (e: Exception) {
                Log.d("MyDebugData", "BaseRepository : performApiCall :  " + e.localizedMessage);
                emit(
                    DataHandler.ServerError(
                        e.localizedMessage ?: appContext.getString(R.string.server_error)
                    )
                )
                emit(DataHandler.HideLoading)
            }
        }.flowOn(Dispatchers.IO) as Flow<DataHandler<T>>


    fun parseErrorCustomResponse(errorBody: String): String {
        return errorBody
    }

    fun parseErrorDefaultResponse(errorBody: ResponseBody?): String {
        var errorMessage = ""
        try {
            val jsonObject = JSONObject(errorBody!!.string())
            val errorsArray = jsonObject.getJSONArray("Error")
            errorMessage = errorsArray.getString(0)
        } catch (e: Exception) {
            return errorMessage
        }
        return errorMessage
    }
}