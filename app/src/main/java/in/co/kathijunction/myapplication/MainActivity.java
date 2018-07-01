package in.co.kathijunction.myapplication;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
         {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // A reference to the service used to get location updates.

    // Tracks the bound state of the service.

        // UI elements.
        private Button mRequestLocationUpdatesButton;
        private Button mRemoveLocationUpdatesButton;

        // Monitors the state of the connection to the service.
        private final ServiceConnection mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                mBound = false;
            }
        };

    TextView _number, _password, _response, _restaurant_id;
    android.support.v7.widget.AppCompatButton _sendRequest;
    ProgressBar _proProgressBar;

    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    public Double Longitude;
    public Double Latitude;
    public String latitude;
    public String longitude;
    public static String restaurant_id;
    public static String number;
    public static String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mLatitudeLabel = getResources().getString(R.string.response3);
        //mLongitudeLabel = getResources().getString(R.string.response1);
        //mLatitudeText = (TextView) findViewById((R.id.response));
         //mLongitudeText = (TextView) findViewById((R.id.response2));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Hooking the UI views for usage
        _number = (TextView) findViewById(R.id.number);
        _password = (TextView) findViewById(R.id.password);
        _response = (TextView) findViewById(R.id.response);
        _proProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        _sendRequest = (AppCompatButton) findViewById(R.id.send_request);
        _restaurant_id = (TextView) findViewById(R.id.restaurant_id);
        final Intent intent = new Intent(MainActivity.this, Main2Activity.class);

        //hooking onclick listener of button
        _sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override


            public void onClick(View view) {
                // Instantiate the RequestQueue.
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    //mService.requestLocationUpdates();
                }
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                //this is the url where you want to send the request
                //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
                String url = "http://eatlot.com/admin2/android/test.php";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the response string.
                                _response.setText(response);

                                if(response != "success") {
                                    restaurant_id = _restaurant_id.getText().toString();
                                    number = _number.getText().toString();
                                    password = _password.getText().toString();

                                    Log.i(TAG, "RESTAURANTANT ID : "+restaurant_id);
                                    Log.i(TAG, "number : "+number);
                                    Log.i(TAG, "Password : "+password);
                                    intent.putExtra("restaurant_id_5",_restaurant_id.getText().toString());
                                    intent.putExtra("number",number);
                                    intent.putExtra("password",password);
                                    startActivity(intent);

                                   // mService.requestLocationUpdates();

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _response.setText("That didn't work!");
                    }
                }) {
                    //adding parameters to the request
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("number", _number.getText().toString());
                        params.put("password", _password.getText().toString());
                        params.put("restaurant_id", _restaurant_id.getText().toString());
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

/*
        mRemoveLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.removeLocationUpdates();
            }
        });
*/
        // Restore the state of the buttons when the activity (re)launches.

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
    }



    @Override
    protected void onStop() {

        super.onStop();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            }
        }
    }

}