package com.liang.goldtracker.data.api;

import com.liang.goldtracker.data.model.VangTodayAllResponse;
import com.liang.goldtracker.data.model.VangTodayHistoryResponse;
import com.liang.goldtracker.data.model.VangTodaySingleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VangTodayService {

    // Lấy tất cả loại vàng
    @GET("prices")
    Call<VangTodayAllResponse> getAllPrices();

    // Lấy 1 loại vàng
    @GET("prices")
    Call<VangTodaySingleResponse> getSinglePrice(
            @Query("type") String typeCode
    );

    // Lấy lịch sử 1 loại vàng
    @GET("prices")
    Call<VangTodayHistoryResponse> getPriceHistory(
            @Query("type") String typeCode,
            @Query("days") int days
    );
}
