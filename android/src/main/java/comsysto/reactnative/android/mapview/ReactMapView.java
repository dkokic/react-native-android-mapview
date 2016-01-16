package comsysto.reactnative.android.mapview;

import com.facebook.react.bridge.ReactContext;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;


public class ReactMapView extends MapView {

    private Map<String, Marker> mMarkerMap = new HashMap<>();

    public ReactMapView(ReactContext context) {
        super(context);
    }

    public Map<String, Marker> getMarkerMap() {
        return mMarkerMap;
    }

}
