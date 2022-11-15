package com.slicksoftcoder.smalltownlottery.server
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.UserUpdateModel
import com.slicksoftcoder.smalltownlottery.features.authenticate.UserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Headers("Content-Type:application/json")
    @POST("login")
    fun signin(@Body info: UserModel): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("updateUserDevice")
    fun updateUserDevice(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @GET("draws")
    fun updateDraws(): Call<List<DrawUpdateModel>>
//
//    @Headers("Content-Type:application/json")
//    @GET("operations")
//    fun getOperations(): Call<List<OperationData>>
//
//    @Headers("Content-Type:application/json")
//    @GET("weeks")
//    fun getWeeks(): Call<List<WeeksData>>
//
//    @Headers("Content-Type:application/json")
//    @GET("status")
//    fun getStatus(): Call<List<StatusData>>
//
//    @Headers("Content-Type:application/json")
//    @GET("lines")
//    fun getLines(): Call<List<LineData>>
//
//    @Headers("Content-Type:application/json")
//    @GET("designation")
//    fun getDesignation(): Call<List<DesignationData>>
//
//    @Headers("Content-Type:application/json")
//    @POST("monitoring")
//    fun transmitMonitoring(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>
//
//    @Headers("Content-Type:application/json")
//    @POST("accomplishment")
//    fun transmitAccomplishment(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>
}