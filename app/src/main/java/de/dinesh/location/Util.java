package de.dinesh.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.dinesh.location.locationApi.FormattedAddress;

/**
 * Created by dineshvg on 08.11.18 for LocationObtain in de.dinesh.location.
 * 04: 04
 */
class Util {

    static String makeAddressLineFromFormattedAddress(final FormattedAddress formattedAddress) {
        return formattedAddress.getStreet() + " "
                + formattedAddress.getDoorNumber() + ", "
                + formattedAddress.getZipCode() + ", "
                + formattedAddress.getCityName();
    }

    static boolean isAddressConformed(final FormattedAddress formattedAddress) {
        return !formattedAddress.getCityName().isEmpty() &&
                !formattedAddress.getZipCode().isEmpty() &&
                !formattedAddress.getDoorNumber().isEmpty() &&
                !formattedAddress.getStreet().isEmpty();
    }


    static void makeSnackBar(final ConstraintLayout root, final String message, final String action) {
        Snackbar snackbar = Snackbar
                .make(root, message, Snackbar.LENGTH_LONG)
                .setAction(action, view -> {
                    //can make actions.
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    static void infoSnackBar(final ConstraintLayout root, final String message) {
        Snackbar snackbar = Snackbar
                .make(root, message, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    static boolean isLocationEnabled(final Context context) {
        String le = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) context.getSystemService(le);
        Log.d("", ""+locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    static boolean checkLocationPermission(final Context context) {
        final String permission = context.getString(R.string.location_permission);
        final int res = context.checkCallingOrSelfPermission(permission);
        Log.d("", ""+(res == PackageManager.PERMISSION_GRANTED));
        return (res == PackageManager.PERMISSION_GRANTED);
    }

}
