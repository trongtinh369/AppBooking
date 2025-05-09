import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ImgBBAPI {
    companion object {
        const val BASE_URL = "https://api.imgbb.com/"
    }
    @Multipart
    @POST("1/upload")
    fun uploadImage(@Query("key") apiKey: String, @Part image: MultipartBody.Part): Call<ImgBBResponse>
}
