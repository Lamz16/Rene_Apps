package com.lamz.reneapps.data


import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.lamz.reneapps.api.ApiConfig
import com.lamz.reneapps.api.ApiService
import com.lamz.reneapps.data.database.StoryDatabase
import com.lamz.reneapps.data.database.StoryRemoteMediator
import com.lamz.reneapps.data.pref.UserModel
import com.lamz.reneapps.data.pref.UserPreference
import com.lamz.reneapps.response.DetailResponse
import com.lamz.reneapps.response.ListStoryItem
import com.lamz.reneapps.response.LoginResponse
import com.lamz.reneapps.response.MapsResponse
import com.lamz.reneapps.response.UploadRegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun registerAccount(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadRegisterResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        } catch (e: Exception) {
            emit(ResultState.Error("Error : ${e.message.toString()}"))
        }

    }

    suspend fun loginAccount(name: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.login(name, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        } catch (e: Exception) {
            emit(ResultState.Error("Error : ${e.message.toString()}"))
        }
    }

    suspend fun getStoriesWithLocation(): LiveData<ResultState<MapsResponse>> =
        liveData {
            emit(ResultState.Loading)
            try {
                userPreference.getSession()
                val user = runBlocking { userPreference.getSession().first() }
                val apiService = ApiConfig.getApiService(user.token)
                val successResponse = apiService.getStoriesWithLocation()
                emit(ResultState.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, MapsResponse::class.java)
                emit(errorResponse.message.let { ResultState.Error(it) })
            } catch (e: Exception) {
                emit(ResultState.Error("Error : ${e.message.toString()}"))
            }
        }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        userPreference.getSession()
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
//                StoriesPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getDetailStories(id: String) = liveData {
        emit(ResultState.Loading)
        try {
            userPreference.getSession()
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.getDetailStories( id)
            emit(ResultState.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DetailResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        } catch (e: Exception) {
            emit(ResultState.Error("Error : ${e.message.toString()}"))
        }
    }

    //    Dengan lokasi upload image nya
    suspend fun uploadImage(
        imageFile: File,
        description: String,
        lat: String,
        lon: String
    ) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val lat = lat.toRequestBody("text/plain".toMediaType())
        val lon = lon.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            userPreference.getSession()
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse =
                apiService.uploadImage(multipartBody, requestBody, lat, lon)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadRegisterResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        } catch (e: Exception) {
            emit(ResultState.Error("Error : ${e.message.toString()}"))
        }

    }

    //    Tidak dengan lokasi upload image nya
    suspend fun uploadImage( imageFile: File, description: String) = liveData {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            userPreference.getSession()
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse =
                apiService.uploadImage( multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadRegisterResponse::class.java)
            emit(errorResponse.message.let { ResultState.Error(it) })
        } catch (e: Exception) {
            emit(ResultState.Error("Error : ${e.message.toString()}"))
        }

    }


    companion object {

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(storyDatabase,userPreference, apiService)
            }.also { instance = it }
    }
}