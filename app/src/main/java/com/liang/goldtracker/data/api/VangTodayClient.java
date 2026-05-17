package com.liang.goldtracker.data.api;

import com.liang.goldtracker.data.model.VangTodayAllResponse;
import com.liang.goldtracker.data.model.VangTodayHistoryResponse;
import com.liang.goldtracker.data.model.VangTodaySingleResponse;
import com.liang.goldtracker.exception.NoDataException;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VangTodayClient {

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://www.vang.today/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final VangTodayService service = retrofit.create(VangTodayService.class);

    public static VangTodayAllResponse getAllPrices() throws Exception {
        Response<VangTodayAllResponse> response = service.getAllPrices().execute();

        if (!response.isSuccessful()) throw new IOException("Request failed");
        if (response.body() == null || !response.body().isSuccess()) throw new NoDataException();
        if (response.body().getPrices() == null || response.body().getPrices().isEmpty())
            throw new NoDataException();

        return response.body();
    }

    public static VangTodaySingleResponse getSinglePrice(String typeCode) throws Exception {
        Response<VangTodaySingleResponse> response = service.getSinglePrice(typeCode).execute();

        if (!response.isSuccessful()) throw new IOException("Request failed");
        if (response.body() == null || !response.body().isSuccess()) throw new NoDataException();

        return response.body();
    }

    public static VangTodayHistoryResponse getPriceHistory(String typeCode, int days) throws Exception {
        Response<VangTodayHistoryResponse> response =
                service.getPriceHistory(typeCode, days).execute();

        if (!response.isSuccessful()) throw new IOException("Request failed");
        if (response.body() == null || !response.body().isSuccess()) throw new NoDataException();
        if (response.body().getHistory() == null || response.body().getHistory().isEmpty())
            throw new NoDataException();

        return response.body();
    }
}
