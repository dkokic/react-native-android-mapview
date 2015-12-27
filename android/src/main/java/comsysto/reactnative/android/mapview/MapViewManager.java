package comsysto.reactnative.android.mapview;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


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
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        });

        return mReactMapView;
    }

}
