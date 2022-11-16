package com.slicksoftcoder.smalltownlottery.server
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.UserUpdateModel
import com.slicksoftcoder.smalltownlottery.features.authenticate.UserModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.DrawModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.PnlModel
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
    @GET("updateDraw")
    fun updateDraws(): Call<List<DrawUpdateModel>>

    @Headers("Content-Type:application/json")
    @GET("updateUser")
    fun updateUser(@Query("username") username: String): Call<List<UserUpdateModel>>

    @Headers("Content-Type:application/json")
    @POST("pnl")
    fun getPNL(@Query("date") date: String?, @Query("agent") agent: String?): Call<List<PnlModel>>

    @Headers("Content-Type:application/json")
    @POST("draw")
    fun getDraw(@Query("date") date: String?, @Query("agent") agent: String?, @Query("draw") draw: String?): Call<List<DrawModel>>
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