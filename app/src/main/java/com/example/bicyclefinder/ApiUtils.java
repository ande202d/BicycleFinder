package com.example.bicyclefinder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ApiUtils {
    private static ApiUtils apiUtils = null;
    private RESTService restService = null;

    private ApiUtils(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://anbo-bicyclefinder.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restService = retrofit.create(RESTService.class);
    }

    public static ApiUtils getInstance(){
        if (apiUtils == null) apiUtils = new ApiUtils();
        return apiUtils;
    }

    public RESTService getRESTService(){
        return restService;
    }
}
