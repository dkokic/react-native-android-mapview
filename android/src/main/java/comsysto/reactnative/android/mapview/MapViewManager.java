package comsysto.reactnative.android.mapview;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.uimanager.ReactProp;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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
    protected ReactMapView createViewInstance(ThemedReactContext reactContext) {
        mReactMapView = new ReactMapView(reactContext);
        mReactMapView.onCreate(mSavedInstanceState);
        mReactMapView.onResume();
        mReactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "googleMap = " + googleMap);
                // Add a marker in Sydney and move the camera
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        });
        return mReactMapView;
    }
/*
    @ReactMethod
    public void getMapAsync(final Callback successCallback, Callback errorCallback) {
        try {
            mReactMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    successCallback.invoke();
                }
            });
        } catch (IllegalViewOperationException e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactProp(name="setMinimumWidth")
    public void setMinimumWidth(ReactMapView view, int minWidth) {
        view.setMinimumWidth(minWidth);
    }
*/
}
