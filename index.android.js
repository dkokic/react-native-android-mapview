var { requireNativeComponent, View } = require('react-native');

var iface = {
  name: 'AndroidMapView',
  propTypes: {
    ...View.propTypes,
  },
};

module.exports = requireNativeComponent('AndroidMapView', iface);
