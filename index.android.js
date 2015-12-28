'use strict'

let React = require('react-native')


class MapView extends React.Component {

  constructor(props) {
    super(props)
    this._onChange = this._onChange.bind(this)
  }

  _onChange(event: Event) {
    console.log("MapView._onChange :", event.nativeEvent)
    if (event.nativeEvent.type) {
      if (event.nativeEvent.type === "RegionChange" && this.props.onRegionChange) this.props.onRegionChange(event.nativeEvent)
    }
  }

  componentDidMount () {
    console.log("MapView.componentDidMount")
  }

  componentWillUnmount () {
    console.log("MapView.componentWillUnmount")
  }

  componentWillReceiveProps (nextProps) {
    console.log("MapView.componentWillReceiveProps(): nextProps = ", nextProps)
  }

  render () {
    return (
      <AndroidMapView
        { ...this.props }
        onChange = { this._onChange }
      />
    )
  }

}

MapView.propTypes = {
  ...React.View.propTypes,
  region: React.PropTypes.shape({
    latitude: React.PropTypes.number.isRequired,
    longitude: React.PropTypes.number.isRequired,
    zoom: React.PropTypes.number,
  }),
  onRegionChange: React.PropTypes.func,
}

let AndroidMapView = React.requireNativeComponent('AndroidMapView', MapView, {nativeOnly: {onChange: true}})

module.exports = MapView
