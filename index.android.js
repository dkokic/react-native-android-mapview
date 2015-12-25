'use strict'

let React = require('react-native')

let {
  requireNativeComponent,
  View,
  Component,
} = React


let AndroidMapViewInterface = {
  name: 'AndroidMapView',
  propTypes: {
    ...View.propTypes,
  },
}

let AndroidMapView = requireNativeComponent('AndroidMapView', AndroidMapViewInterface)


class MapView extends Component {

  constructor(props) {
    super(props)
  }

  componentDidMount () {
  }

  componentWillUnmount () {
  }

  componentWillReceiveProps (nextProps) {
  }

  render () {
    return (
      <AndroidMapView
        { ...this.props }
      />
    )
  }

}

module.exports = MapView
