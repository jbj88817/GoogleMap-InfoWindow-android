package com.bjiang.map_ex;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends Activity
        implements OnMapLongClickListener{

    class MyInfoWindowAdapter implements InfoWindowAdapter{

        private final View myContentsView;
        boolean not_first_time_showing_info_window;

        MyInfoWindowAdapter(){
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            ImageView imageView = (ImageView)myContentsView.findViewById(R.id.imgView);
            if (not_first_time_showing_info_window) {
                Picasso.with(MainActivity.this).load("http://assets3.parliament.uk/iv/main-large//ImageVault/Images/id_7382/scope_0/ImageVaultHandler.aspx.jpg").into(imageView);
            } else { // if it's the first time, load the image with the callback set
                not_first_time_showing_info_window=true;
                Picasso.with(MainActivity.this).load("http://assets3.parliament.uk/iv/main-large//ImageVault/Images/id_7382/scope_0/ImageVaultHandler.aspx.jpg").into(imageView,new InfoWindowRefresher(marker));
            }


            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    final int RQS_GooglePlayServices = 1;
    GoogleMap myMap;
    TextView tvLocInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLocInfo = (TextView)findViewById(R.id.locinfo);

        FragmentManager myFragmentManager = getFragmentManager();
        MapFragment myMapFragment
                = (MapFragment)myFragmentManager.findFragmentById(R.id.map);
        myMap = myMapFragment.getMap();

        myMap.setMyLocationEnabled(true);

        //myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setMyLocationButtonEnabled(true);

        myMap.getUiSettings().setRotateGesturesEnabled(true);
        myMap.getUiSettings().setScrollGesturesEnabled(true);
        myMap.getUiSettings().setTiltGesturesEnabled(true);
        myMap.getUiSettings().setZoomGesturesEnabled(true);
        //or myMap.getUiSettings().setAllGesturesEnabled(true);

        myMap.setTrafficEnabled(true);

        myMap.setOnMapLongClickListener(this);

        myMap.setInfoWindowAdapter(new MyInfoWindowAdapter());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_legalnotices:
                String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
                        getApplicationContext());
                AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MainActivity.this);
                LicenseDialog.setTitle("Legal Notices");
                LicenseDialog.setMessage(LicenseInfo);
                LicenseDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS){
            //Toast.makeText(getApplicationContext(),
              //      "isGooglePlayServicesAvailable SUCCESS",
                //    Toast.LENGTH_LONG).show();
        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        tvLocInfo.setText("New marker added@" + point.toString());

        Marker newMarker = myMap.addMarker(new MarkerOptions()
                .position(point)
                .snippet(point.toString()));

        newMarker.setTitle(newMarker.getId());

    }

    private class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        private InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {}
    }


}
