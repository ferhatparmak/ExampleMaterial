package com.wikia.wikiaoriginals.service;

import com.wikia.wikiaoriginals.service.model.WikiMap;
import com.wikia.wikiaoriginals.service.model.WikiSummaryList;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Retrofit service interface
 * It defines request types and acceptable query parameters
 */
public interface RetrofitService {
    public static final String CONTROLLER = "WikisApi";
    public static final String METHOD_GETLIST = "getList";
    public static final String METHOD_GETDETAILS = "getDetails";

    @GET("/wikia.php")
    public WikiSummaryList wikiSummaries(@Query("controller") String controller, @Query("method") String method, @Query("batch") int batch);

    @GET("/wikia.php")
    public WikiMap wikis(@Query("controller") String controller, @Query("method") String method, @Query("ids") String ids);
}