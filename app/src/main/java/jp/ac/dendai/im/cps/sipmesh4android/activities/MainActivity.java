package jp.ac.dendai.im.cps.sipmesh4android.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPolygonStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jp.ac.dendai.im.cps.sipmesh4android.R;
import jp.ac.dendai.im.cps.sipmesh4android.network.ApiClient;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;
    private GeoJsonLayer mLayer;

    private View rootView;
    private Handler handler = new Handler();

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        if(myLocate != null){
            Snackbar.make(rootView, "「ゆれやすさ」を取得中・・・", Snackbar.LENGTH_LONG).show();
//            Toast.makeText(this, "「ゆれやすさ」を取得中・・・", Toast.LENGTH_SHORT).show();
            latlng = new LatLng(myLocate.getLatitude(), myLocate.getLongitude());
        }
        else {
            Snackbar.make(rootView, "現在地を取得できませんでした", Snackbar.LENGTH_LONG).show();
//            Toast.makeText(this, "現在地を取得できませんでした", Toast.LENGTH_SHORT ).show();
            latlng = new LatLng(35.749882, 139.804975);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

        ApiClient client = new ApiClient() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure", e.fillInStackTrace());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseCode = response.body().string();
                Log.d(TAG, "onPostCompleted: ok");
                Log.d(TAG, responseCode);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject != null) {
                    final JSONObject finalJsonObject = jsonObject;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLayer = new GeoJsonLayer(mMap, finalJsonObject);

                            addPolygons(mLayer);
                            mLayer.addLayerToMap();
                        }
                    });
                }
            }
        };

        client.getGeoJson4JSHIS(latlng);
    }

    private static int arvToColor(double ARV) {
        if (2.0 < ARV) {
            return 0x44ff0000;
        } else if (1.8 < ARV) {
            return 0x44ffff00;
        } else if (1.6 < ARV) {
            return 0x4400ff00;
        } else if (1.2 < ARV){
            return 0x440000ff;
        }
        return 0;
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
}
