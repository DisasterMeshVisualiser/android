package jp.ac.dendai.im.cps.sipmesh4android.activities;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.dendai.im.cps.sipmesh4android.R;
import jp.ac.dendai.im.cps.sipmesh4android.dialogs.CheckBoxDialog;
import jp.ac.dendai.im.cps.sipmesh4android.dialogs.SpinningProgressDialog;
import jp.ac.dendai.im.cps.sipmesh4android.entities.Mesh;
import jp.ac.dendai.im.cps.sipmesh4android.entities.MeshType;
import jp.ac.dendai.im.cps.sipmesh4android.network.ApiClient;
import jp.ac.dendai.im.cps.sipmesh4android.utils.SharedPreferencesUtil;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

//public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, CheckBoxDialog.OnButtonClickListener {
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, CheckBoxDialog.OnButtonClickListener, Toolbar.OnMenuItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private final BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

    private GoogleMap mMap;
    private GeoJsonLayer mLayer;

    private View rootView;
    private Handler handler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    private static final String CHECK_BOX_DIALOG = "check_box_dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycle.onNext(ActivityEvent.CREATE);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.root_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.icon_48dp);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setMeshType();
//            }
//        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                if (mMap != null) {
//                    LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(current));
//                }
//            }
//        });
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location myLocate = locationManager.getLastKnownLocation("gps");
        LatLng latlng;
        SpinningProgressDialog dialog = null;
        if (myLocate != null) {
            dialog = SpinningProgressDialog.newInstance(getString(R.string.app_name), "loading...");
            dialog.show(getSupportFragmentManager(), "dialog_spinner");
            latlng = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());
        } else {
            Toast.makeText(this, "現在地を取得できませんでした", Toast.LENGTH_SHORT).show();
            latlng = new LatLng(35.749882, 139.804975);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

        final SpinningProgressDialog finalDialog = dialog;
        ApiClient apiClient = new ApiClient();
        apiClient.getCpsMesh4Rx(1)
                .map(new Func1<Response, Mesh>() {
                    @Override
                    public Mesh call(Response response) {
                        try {
                            String str = response.body().string();
                            Mesh mesh = new ObjectMapper().readValue(str, Mesh.class);

                            Log.d(TAG, "onResponse: " + mesh.toString());

                            if (finalDialog != null) {
                                finalDialog.dismiss();
                            }
                            return mesh;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Mesh>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e.fillInStackTrace());
                        if (finalDialog != null) {
                            finalDialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(Mesh mesh) {
                        addPolyline(mesh);
                    }
                });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_select) {
            setMeshType();
            return true;
        }

        return false;
    }

    private void setMeshType() {
        final Context context = this;
        ApiClient apiClient = new ApiClient();
        apiClient.getCpsMeshType4Rx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e.fillInStackTrace());
                    }

                    @Override
                    public void onNext(Response response) {
                        String str = null;
                        try {
                            str = response.body().string();
                            MeshType meshType = new ObjectMapper().readValue(str, MeshType.class);
                            int[] idList = new int[meshType.getData().size()];

                            int c = 0;
                            for (MeshType.MeshTypeData data : meshType.getData()) {
                                SharedPreferencesUtil.putName(data.getId(), data.getName(), context);
                                SharedPreferencesUtil.putBool(data.getId(), SharedPreferencesUtil.getBool(data.getId(), context), context);
                                idList[c] = data.getId();
                                c++;
                            }
                            CheckBoxDialog.newInstance(idList).show(getSupportFragmentManager(), CHECK_BOX_DIALOG);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void addPolyline(Mesh mesh) {
        for (Mesh.MeshData data : mesh.getData()) {
            double[][] coordinates = data.getCoordinates();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions
                    .add(new LatLng(coordinates[0][0],coordinates[0][1]),
                        new LatLng(coordinates[1][0],coordinates[1][1]),
                        new LatLng(coordinates[2][0],coordinates[2][1]),
                        new LatLng(coordinates[3][0],coordinates[3][1]),
                        new LatLng(coordinates[4][0],coordinates[4][1]))
                    .fillColor(Color.argb((int)(25 * (data.getValue() * 20)), 255, 0, 0))
                    .strokeWidth(1.0f);
            mMap.addPolygon(rectOptions);
        }
    }

    private void reloadPolyline(Map<String, Mesh.MeshData> data, int division) {
        mMap.clear();
        for (Mesh.MeshData target : data.values()) {
            double[][] coordinates = target.getCoordinates();
            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions
                    .add(new LatLng(coordinates[0][0],coordinates[0][1]),
                            new LatLng(coordinates[1][0],coordinates[1][1]),
                            new LatLng(coordinates[2][0],coordinates[2][1]),
                            new LatLng(coordinates[3][0],coordinates[3][1]),
                            new LatLng(coordinates[4][0],coordinates[4][1]))
                    .fillColor(Color.argb((int)(25 * ((target.getValue() / division) * 20)), 255, 0, 0))
                    .strokeWidth(1.0f);
            mMap.addPolygon(rectOptions);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.ac.dendai.im.cps.sipmesh4android/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.ac.dendai.im.cps.sipmesh4android/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    @Override
    public void onPositiveClick(int[] dataArray, boolean[] boolArray) {
        final SpinningProgressDialog dialog = SpinningProgressDialog.newInstance(getString(R.string.app_name), "loading...");
        dialog.show(getSupportFragmentManager(), "dialog_spinner");

        List<Observable<Response>> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length; i++) {
            if (boolArray[i]) {
                ApiClient apiClient = new ApiClient();
                list.add(apiClient.getCpsMesh4Rx(dataArray[i]).subscribeOn(Schedulers.io()));
            }
        }

        if (list.size() == 0) {
            dialog.dismiss();
            mMap.clear();
            return;
        }

        Observable
                .combineLatest(list, new FuncN<List<Mesh>>() {
                    @Override
                    public List<Mesh> call(Object... args) {
//                        Log.d(TAG, "call: args.length " + args.length);
                        List<Mesh> typeList = new ArrayList<>();

                        for (Object obj : args) {
                            Response response = (Response) obj;
                            try {
//                                Log.d(TAG, "call: response " + response.toString());
                                String str = response.body().string();
                                typeList.add(new ObjectMapper().readValue(str, Mesh.class));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return typeList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Mesh>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e.fillInStackTrace());
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNext(List<Mesh> meshs) {
                        Map<String, Mesh.MeshData> meshDataMap = new HashMap<>();

                        for (int i = 0; i < meshs.size(); i++) {
                            for (Mesh.MeshData data : meshs.get(i).getData()) {
                                if (meshDataMap.containsKey(data.getMeshcode())) {
                                    double value = meshDataMap.get(data.getMeshcode()).getValue() + data.getValue();
//                                    Log.d(TAG, "onNext: meshDataMap: " + meshDataMap.get(data.getMeshcode()).getValue() + " data: " + data.getValue());
                                    data.setValue(data.getValue() + value);
                                    meshDataMap.put(data.getMeshcode(), data);
                                }
                                else {
//                                    Log.d(TAG, "onNext: false data: " + data.getValue());
                                    meshDataMap.put(data.getMeshcode(), data);
                                }
                            }
                        }

                        reloadPolyline(meshDataMap, meshs.size());

                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onNegativeClick() {

    }

    public final <T> Observable.Transformer<? super T, ? extends T> bindUntilEvent(ActivityEvent event) {
        return RxLifecycle.bindUntilActivityEvent(lifecycle, event);
    }
    public final <T> Observable.Transformer<? super T, ? extends T> bindToLifecycle() {
        return RxLifecycle.bindActivity(lifecycle);
    }
}
