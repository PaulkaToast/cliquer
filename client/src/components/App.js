import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css'
import { firebase } from '../firebase'
import { logIn, logOut, setToken, setLocation, getProfile, 
         addObjectID, requestFriend, clearProfile, clearObjectID } from '../redux/actions'
import { history } from '../redux/store'
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
    if(this.loggedIn() && nextProps.profile && !this.props.profile && !this.props.accountID) {
      this.props.addObjectID(nextProps.profile.accountID)
    }
  }

  componentDidMount() {
    firebase.onAuthStateChanged(authUser => {
      if(authUser) {
        this.props.logIn(authUser)
        authUser.getIdToken(true)
          .then((token) => {
            this.props.setToken(token)
            this.props.clearProfile()
            this.props.getProfile(`${url}/api/getProfile?username=${authUser.uid}&type=user`, { 'X-Authorization-Firebase': token})
          })
      } else {
        this.props.clearProfile()
        this.props.clearObjectID()
        this.props.logOut(authUser)
      }
      this.setState({ isLoading: false })
    })
  }

  loggedIn = () => {
    return this.props.loggedIn
  }

  isMod = () => {
    return true
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

  goToProfile = (ev, memberID, button1, button2) => {
    if(ev.target !== button1 && ev.target !== button2) {
      history.push(`/profile/${memberID}`)
    }
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
                allowHTML={false}
                accountID={this.props.accountID}
                goToProfile={this.goToProfile}
                isMod={this.isMod}
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
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    addObjectID: (id) => dispatch(addObjectID(id)),
    requestFriend: (url, headers) => dispatch(requestFriend(url, headers)),
    clearProfile: () => dispatch(clearProfile()),
    clearObjectID: () => dispatch(clearObjectID()),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(App)

