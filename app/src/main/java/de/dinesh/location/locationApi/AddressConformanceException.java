package de.dinesh.location.locationApi;

/**
 * Created by dineshvg on 08.11.18 for LocationObtain in de.dinesh.location.locationApi.
 * 05: 16
 */
public class AddressConformanceException extends Exception {

    public AddressConformanceException() {
        super("Address not complete");
    }
}
