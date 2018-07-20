package io.ginius.cp.kt.lostfound;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.ginius.cp.kt.lostfound.models.Result;

import static com.android.volley.VolleyLog.TAG;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_FNAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.DOC_NAME;
import static io.ginius.cp.kt.lostfound.PreferenceManager.LAT;
import static io.ginius.cp.kt.lostfound.PreferenceManager.LON;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKEDBY;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKEDBYCONTACT;
import static io.ginius.cp.kt.lostfound.PreferenceManager.PICKUPLOCATION;

/**
 * Created by cyprian on 7/4/18.
 */

public class ShowPickUpLocation extends MainBaseActivity
        implements OnMapReadyCallback {

    public int SUCCESS = 0;
    ArrayList<HashMap<String, String>> transfersList;
    Toolbar toolbar;
    Result docObjList[];
    private Context mContext;
    private Activity mActivity;
    InputMethodManager imm;
    String fname, lname, docid, comments, doctype, pickup;
    Double lat = 0.0, lng = 0.0;
    TextView names, foundbyphone, type, foundbynames, docnumber, pickuplocation;
    ArrayList<Double> location;
    Button call;
    SupportMapFragment mapFragment;
    String phonecall = "";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    RelativeLayout no_mapdata;
    private GoogleMap mMap;
    public io.ginius.cp.kt.lostfound.PreferenceManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_doc_three);
        toolbar = findViewById(R.id.toolbar);
        mContext = getApplicationContext();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        location = new ArrayList<Double>();
        foundbynames = findViewById(R.id.foundbyname);
        foundbyphone = findViewById(R.id.foundbycontact);
        docnumber = findViewById(R.id.doc_num);
        pickuplocation = findViewById(R.id.pickup_location);
        no_mapdata = findViewById(R.id.no_mapdata);
        names = findViewById(R.id.doc_names);
        type = findViewById(R.id.doc_type);
        call = findViewById(R.id.btn_next);
        mActivity = ShowPickUpLocation.this;

        prefManager = new io.ginius.cp.kt.lostfound.PreferenceManager(this);
        phonecall = prefManager.loadPrefs(PICKEDBYCONTACT, "");
        foundbynames.setText("Found by: "+prefManager.loadPrefs(PICKEDBY, ""));
        docnumber.setText("Document no: "+prefManager.loadPrefs(PreferenceManager.DOC_ID, ""));
        pickuplocation.setText("Where to pick it: "+prefManager.loadPrefs(PICKUPLOCATION, ""));
        foundbyphone.setText("Contact: "+phonecall);
        names.setText("Document Names: "+prefManager.loadPrefs(DOC_FNAME, "")+"  "+prefManager.loadPrefs(DOC_FNAME, ""));
        type.setText("Document Type: "+prefManager.loadPrefs(DOC_NAME, ""));

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        call.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try{
                Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phonecall));
                startActivity(intent);
                }catch (Exception e){
                    Log.e(TAG, e.getMessage());
                }

            }
        });






    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("latlong", Double.valueOf(prefManager.loadPrefs(LAT,""))+" "+ Double.valueOf(prefManager.loadPrefs(LON,"")));
        LatLng sydney = new LatLng(Double.valueOf(prefManager.loadPrefs(LAT,"")), Double.valueOf(prefManager.loadPrefs(LON,"")));
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title(prefManager.loadPrefs(PICKUPLOCATION, "")));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        moveToCurrentLocation(sydney);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
