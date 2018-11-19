# Places Search App

### Example app to search for location using the Google Places Autocomplete API and Goecoding API.

+  App searches for location with address filter to search using the Autocomplete API.
+  If GPS signal is available, reverse geo-coding is applied to search for the location using the latitude and longitude.

### App Abstraction
+ Module App:
    + LocationFinder.java used for autocomplete and reverse geocoding.
    + FormatAddress.java POJO class for address transportation.
    + AddressConformanceException.java exception to find when address is not right.
    + RequestCodes.java to differentiate between origin and destination address.
    + Util.java for activity.

### Task List
- [x] Code abstraction.
- [ ] Allow location apis plug and play.
