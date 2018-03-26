import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css'
import { firebase } from '../firebase'
import { logIn, logOut, setToken, setLocation, getProfile, addObjectID, requestFriend } from '../redux/actions'
import url from '../server'
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

  componentWillReceiveProps(nextProps) {
    if(nextProps.profile && !this.props.profile && !this.props.accountID) {
      this.props.addObjectID(nextProps.profile.accountID)
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
            this.props.getProfile(`${url}/api/getProfile?username=${authUser.uid}&type=user`, { 'X-Authorization-Firebase': token})
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
                accountID={this.props.accountID}
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
    loggedIn: state.auth.loggedIn,
    accountID: state.user.accountID,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    logIn: (user) => dispatch(logIn(user)),
    logOut: () => dispatch(logOut()),
    setToken: (token) => dispatch(setToken(token)),
    setLocation: (position) => dispatch(setLocation(position)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    addObjectID: (id) => dispatch(addObjectID(id)),
    requestFriend: (url, headers) => dispatch(requestFriend(url, headers))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(App)

