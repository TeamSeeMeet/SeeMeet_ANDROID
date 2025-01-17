package org.seemeet.seemeet.data.api

import org.seemeet.seemeet.data.model.request.login.RequestKakaoLogin
import org.seemeet.seemeet.data.model.request.login.RequestLoginList
import org.seemeet.seemeet.data.model.response.login.ResponseKakaoLogin
import org.seemeet.seemeet.data.model.response.login.ResponseLoginList
import org.seemeet.seemeet.data.model.response.login.ResponsePostRefreshToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginService {
    @Headers("Content-Type:application/json")
    @POST("auth/login")
    suspend fun postLogin(
        @Body body : RequestLoginList
    ): ResponseLoginList

    @POST("auth/social")
    suspend fun postKakaoLogin(
        @Body body : RequestKakaoLogin
    ): ResponseKakaoLogin

    @POST("auth/refresh")
    suspend fun postRefreshToken(
        @Header("accesstoken") accessToken: String,
        @Header("refreshtoken") refreshToken: String
    ) : ResponsePostRefreshToken
}