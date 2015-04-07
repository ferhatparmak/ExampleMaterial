package com.wikia.wikiaoriginals.service;

import com.octo.android.robospice.retrofit.RetrofitJackson2SpiceService;

/**
 * RoboSpice service for calling Restful Api in a separate service
 */
public class JsonRetrofitSpiceService extends RetrofitJackson2SpiceService{
    private static final String BASE_URL = "http://www.wikia.com";

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }
}
