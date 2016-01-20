package comsysto.reactnative.android.mapview;

import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableArray;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;
import java.util.Set;


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


    @ReactProp(name="mapType")
    public void setMapType(ReactMapView reactMapView, final String mapType) {
        Log.d("MapViewManager", "setMapType(): mapType = " + mapType);
        reactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "setMapType(): googleMap = " + googleMap);
                int googleMapType = GoogleMap.MAP_TYPE_NONE;
                switch (mapType) {
                    case "standard": googleMapType = GoogleMap.MAP_TYPE_NORMAL; break;
                    case "satellite": googleMapType = GoogleMap.MAP_TYPE_SATELLITE; break;
                    case "hybrid": googleMapType = GoogleMap.MAP_TYPE_HYBRID; break;
                }
                googleMap.setMapType(googleMapType);
            }
        });

    }


    @ReactProp(name="annotations")
    public void setAnnotations(final ReactMapView reactMapView, final ReadableArray annotations) {
        Log.d("MapViewManager", "setAnnotations(): annotations = " + annotations);
        reactMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("MapViewManager", "setAnnotations(): googleMap = " + googleMap);
                Set<String> markerIdSet = new HashSet<>();
                for (int i = 0; i < annotations.size(); i++) {
                    final ReadableMap annotation = annotations.getMap(i);
                    final LatLng position = new LatLng(annotation.getDouble("latitude"), annotation.getDouble("longitude"));
                    final String title = annotation.getString("title");
                    final String id = annotation.getString("id");
                    markerIdSet.add(id);
                    if (reactMapView.getMarkerMap().containsKey(id)) {
                        Marker marker = reactMapView.getMarkerMap().get(id);
                        marker.setPosition(position);
                        marker.setTitle(title);
                    } else {
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(title));
                        Log.d("MapViewManager", "setAnnotations(): adding Marker for id = " + id);
                        reactMapView.getMarkerMap().put(id, marker);
                    }
                }
                // remove missing markers from the map
                for (String id : reactMapView.getMarkerMap().keySet()) {
                    if (!markerIdSet.contains(id)) {
                        reactMapView.getMarkerMap().get(id).remove();
                        Log.d("MapViewManager", "setAnnotations(): removing Marker for id = " + id);
                        reactMapView.getMarkerMap().remove(id);
                    }
                }
            }
        });
    }

}
