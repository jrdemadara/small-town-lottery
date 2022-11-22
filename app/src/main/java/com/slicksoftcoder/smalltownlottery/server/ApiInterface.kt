package com.slicksoftcoder.smalltownlottery.server
import com.slicksoftcoder.smalltownlottery.common.model.DrawUpdateModel
import com.slicksoftcoder.smalltownlottery.common.model.UserUpdateModel
import com.slicksoftcoder.smalltownlottery.features.authenticate.UserModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw2pmModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw5pmModel
import com.slicksoftcoder.smalltownlottery.features.dashboard.Draw9pmModel
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
    @POST("2pmdraw")
    fun get2pmDraw(@Query("date") date: String?, @Query("agent") agent: String?, @Query("draw") draw: String?): Call<List<Draw2pmModel>>

    @Headers("Content-Type:application/json")
    @POST("5pmdraw")
    fun get5pmDraw(@Query("date") date: String?, @Query("agent") agent: String?, @Query("draw") draw: String?): Call<List<Draw5pmModel>>

    @Headers("Content-Type:application/json")
    @POST("9pmdraw")
    fun get9pmDraw(@Query("date") date: String?, @Query("agent") agent: String?, @Query("draw") draw: String?): Call<List<Draw9pmModel>>

    @Headers("Content-Type:application/json")
    @POST("transmitheader")
    fun transmitBetHeaders(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @POST("transmitdetails")
    fun transmitBetDetails(@QueryMap filter: HashMap<String, String>): Call<ResponseBody>

    @Headers("Content-Type:application/json")
    @PATCH("voidbet")
    fun voidBetHeader(@Query("headerserial") headerSerial: String?, @Query("isvoid") void: Int?): Call<ResponseBody>
}