import React, { Component } from 'react'
import { Route, Redirect, Switch } from 'react-router'
import { connect } from 'react-redux'

import '../css/App.css'
import { firebase } from '../firebase'
import { logIn, logOut, setToken, getProfile, 
         addObjectID, requestFriend, clearProfile, clearObjectID,
         clearGroups, clearSkills, addIsMod } from '../redux/actions'
import { history } from '../redux/store'
import url from '../server'
import Login from './Login'
import Register from './Register'
import Main from './Main'

var oldProfile = undefined;

class App extends Component {

  constructor(props) {
    super(props)

    this.state = { 
      error: '',
      isLoading: true,
    }
  }

  componentWillReceiveProps(nextProps) {
    if(this.loggedIn() && ((nextProps.profile && !this.props.profile && !this.props.accountID) || nextProps.account)) {
      if(nextProps.profile && nextProps.profile.suspended) {
        alert(`Your account has been suspended for ${this.formatDuration(nextProps.profile.suspendTime)}.`)
        this.props.logOut(this.props.user)
      }
      this.props.addObjectID(nextProps.profile ? nextProps.profile.accountID : nextProps.account.accountID)
      this.props.addIsMod(nextProps.profile ? nextProps.profile.moderator : nextProps.account.moderator)
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
            this.props.clearGroups()
            this.props.clearSkills()
            console.log('app call')
            this.props.getProfile(`${url}/api/getProfile?username=${authUser.uid}&type=user`, { 'X-Authorization-Firebase': token})
          })
      } else {
        this.props.clearProfile()
        this.props.clearObjectID()
        this.props.clearGroups()
        this.props.clearSkills()
        this.props.addIsMod(false)
        this.props.logOut(authUser)
      }
      this.setState({ isLoading: false })
    })
  }

  loggedIn = () => {
    return this.props.loggedIn
  }

  isMod = () => {
    return this.props.isMod
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

  formatDuration = (totalTime) => {
    let minutes = totalTime
    let hours = Math.floor(minutes / 60) 
    minutes = minutes % 60
    let days = Math.floor(hours / 24) 
    hours = hours % 24
    let output = ""
    if(days) output += `${days} ${days === 1 ? 'day' : 'days'}, `
    if(hours) output += `${hours} ${hours === 1 ? 'hour' : 'hours'}, `
    if(minutes) {
      output += `${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`
    } else {
      //Cut out ending comma and space
      output = output.substr(0, output.length-2)
    }
    return output
  }

  goToProfile = (ev, memberID, button1, button2) => {
    if((!ev && !button1 && !button2) || (ev.target === ev.currentTarget)) {
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
                ownProfile={this.props.ownProfile}
                isMod={this.isMod}
                formatDuration={this.formatDuration}
              />
            : <Redirect to="/login" />
          }/>
        </Switch>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	var props = {
    user: state.user.data,
    position: state.user.position,
    loggedIn: state.auth.loggedIn,
    accountID: state.user.accountID,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
    isMod: state.user.isMod,
    account: state.auth.data ? state.auth.data : null,
  }

  if (state.profile.getData && 
      (state.user.accountID === state.profile.getData.accountID)) {
    oldProfile = state.profile.getData;
  }
  if (state.profile.getData && 
      (state.user.accountID !== state.profile.getData.accountID)) {
    props.ownProfile = oldProfile;
  }
  
  return props;
}

const mapDispatchToProps = (dispatch) => {
	return {
    logIn: (user) => dispatch(logIn(user)),
    logOut: () => dispatch(logOut()),
    setToken: (token) => dispatch(setToken(token)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    addObjectID: (id) => dispatch(addObjectID(id)),
    addIsMod: (isMod) => dispatch(addIsMod(isMod)),
    requestFriend: (url, headers) => dispatch(requestFriend(url, headers)),
    clearProfile: () => dispatch(clearProfile()),
    clearGroups: () => dispatch(clearGroups()),
    clearObjectID: () => dispatch(clearObjectID()),
    clearSkills: () => dispatch(clearSkills()),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(App)

