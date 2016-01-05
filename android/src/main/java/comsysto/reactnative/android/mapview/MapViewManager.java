package comsysto.reactnative.android.mapview;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class MapViewManager extends ViewGroupManager<ReactMapView> {

    private ReactMapView mReactMapView;
    private Bundle mSavedInstanceState;

    public MapViewManager(Bundle pSavedInstanceState) {
        mSavedInstanceState = pSavedInstanceState;
    }

    @Override
    public String getName() {
        return "AndroidMapView";
    }

    @Override
    protected ReactMapView createViewInstance(final ThemedReactContext reactContext) {
        Log.d("MapViewManager", "createViewInstance(): ... ");
        mReactMapView = new ReactMapView(reactContext);
        mReactMapView.onCreate(mSavedInstanceState);
        mReactMapView.onResume();
        mReactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "googleMap = " + googleMap);
                // Listen to Camera changes
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        Log.d("MapViewManager", "createViewInstance(): OnCameraChangeListener.onCameraChange(): cameraPosition = " + cameraPosition);
                        WritableMap event = Arguments.createMap();
                        event.putString("type", "RegionChange");
                        WritableMap target = Arguments.createMap();
                        target.putDouble("latitude", cameraPosition.target.latitude);
                        target.putDouble("longitude", cameraPosition.target.longitude);
                        event.putMap("target", target);
                        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(mReactMapView.getId(), "topChange", event);
                    }
                });
            }
        });

        return mReactMapView;
    }


    @ReactProp(name="region")
    public void setRegion(ReactMapView reactMapView, final ReadableMap region) {
        Log.d("MapViewManager", "setRegion(): region = " + region);
        reactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "googleMap = " + googleMap);
                double latitude = 48.15278342806624, longitude = 11.583065316081047;
                if (region.hasKey("latitude")) latitude = region.getDouble("latitude");
                if (region.hasKey("longitude")) longitude = region.getDouble("longitude");
                if (region.hasKey("latitudeDelta") && region.hasKey("longitudeDelta")) {
                    double latitudeDelta = region.getDouble("latitudeDelta");
                    double longitudeDelta = region.getDouble("longitudeDelta");
                    LatLng southwest = new LatLng(latitude-latitudeDelta/2, longitude-longitudeDelta/2);
                    LatLng northeast = new LatLng(latitude+latitudeDelta/2, longitude+longitudeDelta/2);
                    LatLngBounds bounds = new LatLngBounds(southwest, northeast);
                    int padding = 0;
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                } else {
                    double zoom = 10.0;
                    if (region.hasKey("zoom")) zoom = region.getDouble("zoom");
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), (float)zoom));
                }
            }
        });
    }


    @ReactProp(name="showsUserLocation", defaultBoolean = false)
    public void setShowsUserLocation(ReactMapView reactMapView, final Boolean showsUserLocation) {
        Log.d("MapViewManager", "setShowsUserLocation(): showsUserLocation = " + showsUserLocation);
        reactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "setShowsUserLocation(): googleMap = " + googleMap);
                googleMap.setMyLocationEnabled(showsUserLocation);
            }
        });
    }

}
