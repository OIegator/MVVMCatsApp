package api.interceptors

import com.olegator.mvvmcatsapp.util.Constants.Companion.API_KEY
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyRequestInterceptor: Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
                .header("x-api-key", API_KEY)
                .method(originalRequest.method, originalRequest.body)
                .build()

        return chain.proceed(newRequest)
    }
}