# React Native MapView Component for Android

WiP MapView component based on Google Maps SDK for Android.

### Example MapView

Note: The style prop with 'height' and 'width' are required to render the component.

``` js
let MapView = require('react-native-android-mapview')

...

<MapView
  style = { { height: 300, width: 300 } }
  region = { { latitude: 48.15278342806624, longitude: 11.583065316081047, zoom: 10.0 } }
  onRegionChange = { (e) => console.log(e) }
/>
```

### Props

##### onRegionChange

https://facebook.github.io/react-native/docs/mapview.html#onregionchange

##### region

https://facebook.github.io/react-native/docs/mapview.html#region

