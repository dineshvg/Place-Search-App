package de.dinesh.location.locationApi;

/**
 * Created by dineshvg on 08.11.18 for LocationObtain in de.dinesh.location.locationApi.
 * 02: 06
 */
public class FormattedAddress {

    private String cityName;
    private String zipCode;
    private String street;
    private String doorNumber;

    public String getCityName() {
        return cityName;
    }

    void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    public String getZipCode() {
        return zipCode;
    }

    void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    void setStreet(final String street) {
        this.street = street;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    void setDoorNumber(final String doorNumber) {
        this.doorNumber = doorNumber;
    }
}
