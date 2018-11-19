package de.dinesh.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;

import java.io.IOException;

import de.dinesh.location.locationApi.AddressConformanceException;
import de.dinesh.location.locationApi.FormattedAddress;
import de.dinesh.location.locationApi.LocationFinder;

import static de.dinesh.location.Util.checkLocationPermission;
import static de.dinesh.location.Util.makeAddressLineFromFormattedAddress;
import static de.dinesh.location.locationApi.RequestCodes.DEST_PLACE_AUTOCOMPLETE_REQUEST_CODE;
import static de.dinesh.location.locationApi.RequestCodes.GPS_REQUEST_CODE;
import static de.dinesh.location.locationApi.RequestCodes.ORIGIN_PLACE_AUTOCOMPLETE_REQUEST_CODE;

public class StartActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = StartActivity.class.getSimpleName();
    private ConstraintLayout rootView;
    private TextView originTextView;
    private TextView destinationTextView;
    private ImageView originImageView;
    private ImageView destinationImageView;
    private Button endButton;
    private GoogleProgressBar progressBar;

    private Geocoder geocoder;
    private LocationFinder locationFinder;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private SelectLocation selectLocation;

    private Merlin merlin;
    private MerlinsBeard merlinsBeard;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        init();
    }

    private void init() {
        rootView = findViewById(R.id.root);
        originTextView = findViewById(R.id.start_location_textView);
        destinationTextView = findViewById(R.id.end_location_textView);
        originImageView = findViewById(R.id.start_imageView);
        destinationImageView = findViewById(R.id.end_imageView);
        progressBar = findViewById(R.id.google_progress_bar);
        endButton = findViewById(R.id.end_button);
    }

    @Override
    public void onStart() {
        super.onStart();
        initListeners();
        initMerlin();
        requestPermission();
    }

    private void initMerlin() {
        merlin = new Merlin.Builder().withConnectableCallbacks().build(this);
        merlinsBeard = MerlinsBeard.from(this);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
        initLocationServices();
        registerMerlin();
        registerMerlinsBeard();
    }

    private void registerMerlinsBeard() {
        if (merlinsBeard.isConnected()) {
            Log.d(TAG, "connected merlins beard");
            Util.infoSnackBar(rootView, "Internet available");
        } else {
            Log.d(TAG, "disconnected merlins beard");
            Util.infoSnackBar(rootView, "Internet disconnected");
        }
    }

    private void registerMerlin() {
        merlin.registerConnectable(() -> Log.d(TAG, "register merlin"));
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }

    private void initLocationServices() {
        geocoder = new Geocoder(this);
        locationFinder = new LocationFinder();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void initListeners() {
        locationListener = this;
        originTextView.setOnClickListener(view -> startLocationSearch(ORIGIN_PLACE_AUTOCOMPLETE_REQUEST_CODE));
        destinationTextView.setOnClickListener(view -> startLocationSearch(DEST_PLACE_AUTOCOMPLETE_REQUEST_CODE));
        originImageView.setOnClickListener(view -> {
            if (checkLocationPermission(this) && Util.isLocationEnabled(this)) {
                selectLocation = SelectLocation.ORIGIN;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Util.makeSnackBar(rootView, "Provide permission", "CLOSE");
            }
        });
        destinationImageView.setOnClickListener(view -> {
            if (checkLocationPermission(this) && Util.isLocationEnabled(this)) {
                selectLocation = SelectLocation.DESTINATION;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                Util.makeSnackBar(rootView, "Provide permission", "CLOSE");
            }
        });

        endButton.setOnClickListener(view -> finish());
    }


    private void startLocationSearch(final int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(locationFinder.setAddressFilter())
                            .build(this);
            startActivityForResult(intent, requestCode);
        } catch (final GooglePlayServicesRepairableException e) {
            Log.e(TAG, e.getMessage());
            Util.makeSnackBar(rootView, "Error on response", "RETRY");
        } catch (final GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage());
            Util.makeSnackBar(rootView, "Service not available", "CLOSE");
        }
    }

    public void onRequestPermissionsResult(final int requestCode, @NonNull final String permissions[], @NonNull final int[] grantResults) {
        switch (requestCode) {
            case GPS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Util.infoSnackBar(rootView, "Location permission enabled");
                } else {
                    Util.infoSnackBar(rootView, "Location permission disabled");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        updateAddress(requestCode, resultCode, data);
    }

    private void updateAddress(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == ORIGIN_PLACE_AUTOCOMPLETE_REQUEST_CODE || requestCode == DEST_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            switch (locationFinder.getAddressFromSearch(resultCode, data, this, geocoder)) {
                case RESULT_OK:
                    final FormattedAddress formattedAddress = locationFinder.getFormattedAddress();
                    if (Util.isAddressConformed(formattedAddress)) {
                        final String addressLine = makeAddressLineFromFormattedAddress(formattedAddress);
                        if (requestCode == ORIGIN_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                            originTextView.setText(addressLine);
                        } else if (requestCode == DEST_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                            destinationTextView.setText(addressLine);
                        }
                    } else {
                        try {
                            throw new AddressConformanceException();
                        } catch (final AddressConformanceException e) {
                            Log.e(TAG, e.getMessage());
                            Util.makeSnackBar(rootView, "Address not complete for this location", "RETRY");
                        }
                    }

                    break;
                case RESULT_CANCELLED:
                    //user cancelled location search.
                    break;
                case RESULT_ERROR:
                    Util.makeSnackBar(rootView, "Error on address resolution", "RETRY");
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        progressBar.setVisibility(View.INVISIBLE);
        try {
            if (selectLocation == SelectLocation.ORIGIN) {
                final FormattedAddress address = locationFinder.getFormattedAddressFromCoordinates(geocoder, location.getLatitude(), location.getLongitude());
                if (Util.isAddressConformed(address)) {
                    originTextView.setText(makeAddressLineFromFormattedAddress(address));
                } else {
                    throw new AddressConformanceException();
                }

            } else if (selectLocation == SelectLocation.DESTINATION) {
                final FormattedAddress address = locationFinder.getFormattedAddressFromCoordinates(geocoder, location.getLatitude(), location.getLongitude());
                if (Util.isAddressConformed(address)) {
                    destinationTextView.setText(makeAddressLineFromFormattedAddress(address));
                } else {
                    throw new AddressConformanceException();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Util.makeSnackBar(rootView, "Error on address resolution", "RETRY");
        } catch (final AddressConformanceException e) {
            Log.e(TAG, e.getMessage());
            Util.makeSnackBar(rootView, "Address not complete for this location", "RETRY");
        }
    }

    @Override
    public void onStatusChanged(final String s, final int i, final Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(final String s) {
    }

    @Override
    public void onProviderDisabled(final String s) {
    }

    private enum SelectLocation {ORIGIN, DESTINATION}
}
