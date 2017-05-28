package ru.vladislav.network;

import com.google.gson.*;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class RetrofitFactory {
    private static RetrofitFactory ourInstance = new RetrofitFactory();

    public static RetrofitFactory getInstance() {
        return ourInstance;
    }

    private RESTService service;

    private RetrofitFactory() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                    @Override
                    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return Instant.ofEpochSecond(json.getAsLong()).atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                })
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        long date = src.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
                        return new JsonPrimitive(date);
                    }
                })
                .create();
        this.service = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build().create(RESTService.class);
    }

    public RESTService getService() {
        return service;
    }
}
