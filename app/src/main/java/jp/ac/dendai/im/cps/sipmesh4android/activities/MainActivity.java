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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;

import java.io.IOException;

import jp.ac.dendai.im.cps.sipmesh4android.R;
import jp.ac.dendai.im.cps.sipmesh4android.dialogs.CheckBoxDialog;
import jp.ac.dendai.im.cps.sipmesh4android.dialogs.SpinningProgressDialog;
import jp.ac.dendai.im.cps.sipmesh4android.entities.Mesh;
import jp.ac.dendai.im.cps.sipmesh4android.entities.MeshType;
import jp.ac.dendai.im.cps.sipmesh4android.network.ApiClient;
import jp.ac.dendai.im.cps.sipmesh4android.utils.SharedPreferencesUtil;
import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Func3;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, CheckBoxDialog.OnButtonClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rootView = findViewById(R.id.root_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Fragment current = getSupportFragmentManager().findFragmentById(R.id.map);
//                getSupportFragmentManager().beginTransaction().remove(crrent).add(R.id.map, MeshTypeSelectFragment.newInstance(1)).commit();
                setMeshType();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setMeshType() {
        final Context context = this;
        ApiClient client = new ApiClient() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: setMeshType");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
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
            }
        };
        client.getCpsMeshType();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location myLocate = locationManager.getLastKnownLocation("gps");
        LatLng latlng;
        SpinningProgressDialog dialog = null;
        if (myLocate != null) {
            dialog = SpinningProgressDialog.newInstance(TAG, "「ゆれやすさ」を取得中・・・");
            dialog.show(getSupportFragmentManager(), "dialog_spinner");
            latlng = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());
        } else {
            Toast.makeText(this, "現在地を取得できませんでした", Toast.LENGTH_SHORT).show();
            latlng = new LatLng(35.749882, 139.804975);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

        final SpinningProgressDialog finalDialog = dialog;
        ApiClient testClient = new ApiClient() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: test client");
                if (finalDialog != null) {
                    finalDialog.dismiss();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                final Mesh mesh = new ObjectMapper().readValue(str, Mesh.class);
                Log.d(TAG, "onResponse: " + mesh.toString());
                if (finalDialog != null) {
                    finalDialog.dismiss();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        addPolyline(mesh);
                    }
                });
            }
        };
        testClient.getCpsMesh(1);

//        ApiClient client = new ApiClient() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure", e.fillInStackTrace());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseCode = response.body().string();
//                Log.d(TAG, "onPostCompleted: ok");
//                Log.d(TAG, responseCode);
//
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(responseCode);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                if (jsonObject != null) {
//                    final JSONObject finalJsonObject = jsonObject;
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mLayer = new GeoJsonLayer(mMap, finalJsonObject);
//
//                            addPolygons(mLayer);
//                            mLayer.addLayerToMap();
//                        }
//                    });
//                }
//            }
//        };
//
//        client.getGeoJson4JSHIS(latlng);
    }

    private static int arvToColor(double ARV) {
        if (2.0 < ARV) {
            return 0x44ff0000;
        } else if (1.8 < ARV) {
            return 0x44ffff00;
        } else if (1.6 < ARV) {
            return 0x4400ff00;
        } else if (1.2 < ARV) {
            return 0x440000ff;
        }
        return 0;
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

    private void addPolygons(GeoJsonLayer layer) {
        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : layer.getFeatures()) {
            if (feature.hasProperty("ARV")) {
                double ARV = Double.parseDouble(feature.getProperty("ARV"));

                GeoJsonPolygonStyle polygonStyle = new GeoJsonPolygonStyle();

                polygonStyle.setFillColor(arvToColor(ARV));
                polygonStyle.setStrokeWidth(1);

                feature.setPolygonStyle(polygonStyle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        ApiClient client = new ApiClient() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        };

        Observable.combineLatest(
                Observable.just(1),
                Observable.just(1),
                Observable.just(1),
                new Func3<Integer, Integer, Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Integer integer2, Integer integer3) {
                        return false;
                    }
                }
        );
    }

    @Override
    public void onNegativeClick() {

    }
}
