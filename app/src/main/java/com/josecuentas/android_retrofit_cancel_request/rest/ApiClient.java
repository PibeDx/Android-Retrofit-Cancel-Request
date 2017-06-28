package com.josecuentas.android_retrofit_cancel_request.rest;

import com.josecuentas.android_retrofit_cancel_request.rest.response.UserResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jcuentas on 28/06/17.
 */

public class ApiClient {
    private static final String PATH = "https://api.backendless.com/";
    private static final long READ_TIMEOUT_SECON = 60;
    private static final long CONNECT_TIMEOUT_SECON = 60;

    private Retrofit mRetrofit;
    private static OkHttpClient HTTP_CLIENT;

    public Retrofit build() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(PATH)
                .client(getBasicClientInterceptor())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return mRetrofit;
    }

    public Client getClient() {
        return build().create(Client.class);
    }


    public static OkHttpClient getBasicClientInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(logging);//LOGS
        builder.readTimeout(READ_TIMEOUT_SECON, TimeUnit.SECONDS).connectTimeout(CONNECT_TIMEOUT_SECON, TimeUnit.SECONDS);
        HTTP_CLIENT = builder.build();
        return HTTP_CLIENT;
    }

    public interface Client {
        @GET("{applicationId}/{restApiKey}/data/User")
        Call<UserResponse> getUser(
                @Path("applicationId") String applicationId,
                @Path("restApiKey") String restApiKey,
                @Query("pageSize") int pageSize,
                @Query("offset") int offset,
                @Query("props") String props
                );

        @GET("/")
        Call<UserResponse> getUser();
    }

}
