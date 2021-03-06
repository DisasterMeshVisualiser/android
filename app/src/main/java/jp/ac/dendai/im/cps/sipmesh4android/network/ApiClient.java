package jp.ac.dendai.im.cps.sipmesh4android.network;


import android.util.Log;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

public class ApiClient {

    private static final String TAG = ApiClient.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private Request request;
    private Map<String, String> params;
    private Map<String, String> requestBodies;
    private ApiClient own;

    private static final String EPSG = "epsg";
    private static final String FORMAT = "format";
    private static final String FILTER = "filter";
    private static final String RADIUS = "radius";

    public ApiClient() {
        own = this;
    }

    public Observable<Response> getCpsMeshType4Rx() {
        HttpUrl.Builder builder = UrlBuilder.buildCpsMeshTypeUrl();
        return enqueue4Rx(builder);
    }

    public Observable<Response> getCpsMesh4Rx(int mesh_type) {
        HttpUrl.Builder builder = UrlBuilder.buildCpsMeshUrl();
        builder.addQueryParameter("mesh_type", String.valueOf(mesh_type));
        return enqueue4Rx(builder);
    }

    private Observable<Response> enqueue4Rx(HttpUrl.Builder builder) {
        request = new Request.Builder()
                .url(builder.build())
                .get().build();

        request.url().toString();
        Log.d(TAG, "enqueue: " + request.url().toString());

        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e.fillInStackTrace());
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

//    public void getGeoJson4JSHIS(LatLng latLng) {
//        HttpUrl.Builder builder = UrlBuilder.buildMeshSearchUrl();
//
//        builder.addQueryParameter("center", String.valueOf(latLng.longitude) + "," + String.valueOf(latLng.latitude));
//        builder.addQueryParameter(EPSG, String.valueOf(4612));
//        builder.addQueryParameter(FORMAT, "geojson");
//        builder.addQueryParameter(FILTER, "ARV_gt_1.2");
//        builder.addQueryParameter(RADIUS, "3");
//        enqueue(builder);
//    }

//    public void getCpsMeshType() {
//        HttpUrl.Builder builder = UrlBuilder.buildCpsMeshTypeUrl();
//        enqueue(builder);
//    }
//
//    public void getCpsMesh(int mesh_type) {
//        HttpUrl.Builder builder = UrlBuilder.buildCpsMeshUrl();
//        builder.addQueryParameter("mesh_type", String.valueOf(mesh_type));
//        enqueue(builder);
//    }

//    private void enqueue(HttpUrl.Builder builder) {
//        request = new Request.Builder()
//                .url(builder.build())
//                .get().build();
//
//        request.url().toString();
//        Log.d(TAG, "enqueue: " + request.url().toString());
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                own.onFailure(call, e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                own.onResponse(call, response);
//            }
//        });
//    }
}

