package cz.fojtik.radek.countryviewer.api;

/**
 * Created by Radek on 21. 1. 2018.
 */

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import cz.fojtik.radek.countryviewer.api.ResponseData;
public interface ApiInterface {
    @GET("rest/v2/name/{country}")
    Call<List<ResponseData>> getData(
            @Path("country") String country

    );
}
