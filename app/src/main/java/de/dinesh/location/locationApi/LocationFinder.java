package de.dinesh.location.locationApi;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by dineshvg on 08.11.18 for LocationObtain in de.dinesh.location.locationApi.
 * 01: 35
 */
public class LocationFinder {

    private static final String TAG = LocationFinder.class.getSimpleName();

    private FormattedAddress formattedAddress = null;

    public LocationFinder() {
    }

    public LocationSearchEnum getAddressFromSearch(final int resultCode, final Intent data, final Context context, final Geocoder geocoder) {

        switch (resultCode) {
            default:
                return LocationSearchEnum.RESULT_CANCELLED;
            case RESULT_OK:
                final Place place = PlaceAutocomplete.getPlace(context, data);
                final LatLng latLng = place.getLatLng();
                try {
                    formattedAddress = getFormattedAddressFromCoordinates(geocoder, latLng);
                    return LocationSearchEnum.RESULT_OK;
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    return LocationSearchEnum.RESULT_ERROR;
                }
            case PlaceAutocomplete.RESULT_ERROR:
                Status status = PlaceAutocomplete.getStatus(context, data);
                Log.i(TAG, status.getStatusMessage());
                return LocationSearchEnum.RESULT_ERROR;
            case RESULT_CANCELED:
                // The user canceled the operation.
                return LocationSearchEnum.RESULT_CANCELLED;
        }
    }

    public FormattedAddress getFormattedAddress() {
        return formattedAddress;
    }

    private FormattedAddress makeAddress(final Address address) {
        final FormattedAddress formattedAddress = new FormattedAddress();
        if (address.getLocality() != null)
            formattedAddress.setCityName(address.getLocality());
        else
            formattedAddress.setCityName("");

        if (address.getPostalCode() != null)
            formattedAddress.setZipCode(address.getPostalCode());
        else
            formattedAddress.setZipCode("");

        if (address.getThoroughfare() != null)
            formattedAddress.setStreet(address.getThoroughfare());
        else
            formattedAddress.setStreet("");

        if (address.getSubThoroughfare() != null)
            formattedAddress.setDoorNumber(address.getSubThoroughfare());
        else
            formattedAddress.setDoorNumber("");

        return formattedAddress;
    }

    public FormattedAddress getFormattedAddressFromCoordinates(final Geocoder geocoder, final double latitude, final double longitude) throws IOException {
        final List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
        final Address address = fromLocation.get(0);
        return makeAddress(address);
    }

    private FormattedAddress getFormattedAddressFromCoordinates(final Geocoder geocoder, final LatLng latLng) throws IOException {
        final List<Address> fromLocation = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        final Address address = fromLocation.get(0);
        return makeAddress(address);
    }

    public AutocompleteFilter setAddressFilter() {
        return new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
    }

    public enum LocationSearchEnum {
        RESULT_OK, RESULT_ERROR, RESULT_CANCELLED
    }
}
