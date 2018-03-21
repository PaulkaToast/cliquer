import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css'
import { firebase } from '../firebase'
import { logIn, logOut, setToken, setLocation } from '../redux/actions'
import Login from './Login'
import Register from './Register'
import Main from './Main'

class App extends Component {

  constructor(props) {
    super(props)

    this.state = { 
      error: '',
      isLoading: true,
    }
  }

  componentDidMount() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(position => {
          this.props.setLocation(position)
        })
    } else {
      //TODO: Geolocation is not supported
    }

    firebase.onAuthStateChanged(authUser => {
      if(authUser) {
        this.props.logIn(authUser)
        authUser.getIdToken(true)
          .then((token) => {
            this.props.setToken(token)
          })
      } else {
        this.props.logOut(authUser)
      }
      this.setState({ isLoading: false })
    })
  }

  loggedIn = () => {
    return this.props.loggedIn
  }

  isLoggedInWithFacebook = () => {
    if(this.props.loggedIn) {
      const providers = this.props.user.providerData
      for (const i in providers) {
        if (providers[i].providerId === 'facebook.com') {
          return true
        }
      }
    }
    return false 
  }

  sendFriendRequest = (user) => {
    //TODO: API call for friend request
  }

  render() {
    return (
      <div className="App h-100">
        <Switch>
          <Route path="/login" render={(navProps) => 
            !this.loggedIn() || this.state.isLoading
            ? <Login {...navProps} />
            : <Redirect to="/groups"/>
          }/>
          <Route path="/register" render={(navProps) =>
            !this.loggedIn() || this.state.isLoading
            ? <Register {...navProps} />
            : <Redirect to="/groups"/>
          }/>
          <Route path="/" render={(navProps) =>
            this.loggedIn() || this.state.isLoading
            ? <Main 
                {...this.props}
                logOut={this.logOut}
                sendFriendRequest={this.sendFriendRequest}
                allowHTML={false}
              />
            : <Redirect to="/login" />
          }/>
        </Switch>
      </div>
    )
  }
}


const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    position: state.user.position,
    loggedIn: state.auth.loggedIn
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    logIn: (user) => dispatch(logIn(user)),
    logOut: () => dispatch(logOut()),
    setToken: (token) => dispatch(setToken(token)),
    setLocation: (position) => dispatch(setLocation(position)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(App)

