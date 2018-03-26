import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router'
import NotificationSystem from 'react-notification-system'
import { Button, ButtonGroup } from 'reactstrap'
import SockJsClient from 'react-stomp'
import { connect } from 'react-redux'

import '../css/Main.css'
import Navbar from './Navbar'
import CreateGroup from './CreateGroup'
import Groups from './Groups/Groups'
import PublicGroups from './PublicGroups'
import Profile from './Profile/Profile'
import Settings from './Settings'
import SearchResults from './SearchResults'
import url from '../server'


class Main extends Component {

  _notificationSystem = null

  componentDidMount =  () => {
    this._notificationSystem = this.refs.notificationSystem
  }

  handleNotification = (notification) => {
    if(notification) {
      switch (notification.type) {
        case 0:
          // Group invite
          this._notificationSystem.addNotification({
            title: 'Group Invite',
            message: 'You\'ve been invited to [GROUP NAME]',
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={this.joinGroup}>Join</Button>
                <Button color="danger" onClick={this.ignoreGroup}>Ignore</Button>
              </ButtonGroup>
            )
          })
          break
        case 1:
          // Friend invite
          this._notificationSystem.addNotification({
            title: 'Friend Invite',
            message: '[USERNAME] has send you a friend request!',
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={this.acceptFriendRequest}>Accept</Button>
                <Button color="danger" onClick={this.rejectFriendRequest}>Reject</Button>
              </ButtonGroup>
            )
          })
          break
        case 2:
          // Mod Warning
          this._notificationSystem.addNotification({
            title: 'Warning',
            message: 'You have received a warning from a moderator!',
            level: 'warning',
            action: {
              label: 'View Warning',
              callback: this.showWarning
            }
          })
          break
        case 3: 
          // Kick Notification
          this._notificationSystem.addNotification({
            title: 'Kick',
            message: 'You have been kicked from [GROUP NAME]',
            level: 'error',
          })
          break
        case 4:
          // Join Request
          this._notificationSystem.addNotification({
            title: 'Join request',
            message: '[NAME] has requested to join [GROUP NAME].',
            level: 'success',
            action: {
              label: 'Allow',
              callback: this.joinGroup
            }
          })
          break
        case 5:
          // Rate request
          this._notificationSystem.addNotification({
            title: 'Rate request',
            message: '[OWNER NAME] has requested you rate members of [GROUP NAME].',
            level: 'success',
            action: {
              label: 'Rate!',
              callback: this.rate
            }
          })
          break
        default:
          // Basic Notification
          // Add basic messages here if necessary
      }
    }
  }

  rate = () => {
    //TODO: make redux actions for these or find better place to put these functions
    console.log('rate clicked')
  }

  showWarning = () => {
    console.log('warning clicked')
  }

  acceptFriendRequest = () => {
    console.log('request accepted')
  }

  rejectFriendRequest = () => {
    console.log('request rejected')
  }

  joinGroup = () => {
    console.log('joined group')
  }

  ignoreGroup = () => {
    console.log('ignored group')
  }

  onWebsocketConnect = () => {
    if (this.props.group) {
      this.clientRef.sendMessage('/app/'+ this.props.user.uid + '/' + this.props.group.groupID +'/messageHistory', "");
    }
  }

  handleMessage = (data) => {
    //if data is an array
    console.log(data)
  }

  getWebsocket = () => {
    if (this.props.group) {
      return <SockJsClient url={`${url}/sockJS`} topics={[`/notification/${this.props.accountID}`]}
          onMessage={this.handleMessage}
          onConnect={this.onWebsocketConnect}
          ref={ (client) => { this.clientRef = client }} 
          subscribeHeaders={{ 'X-Authorization-Firebase': this.props.token }}
          headers={{ 'X-Authorization-Firebase': this.props.token }}
          debug
        />
    } 

    return
  }

  render() {
    return (
      <div className="Main h-100">
        <Navbar {...this.props} />
        <Switch>
            <Route path="/create" render={(navProps) => <CreateGroup {...navProps} />}/>
            <Route path="/groups" render={(navProps) => <Groups {...navProps} {...this.props} />}/>
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} />}/>
            <Route path="/profile/:ownerID" render={(navProps) => <Profile {...navProps} sendFriendRequest={this.props.sendFriendRequest} />}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path="/search/:category/:query" render={(navProps) => <SearchResults {...navProps} sendFriendRequest={this.props.sendFriendRequest} goToProfile={this.props.goToProfile}/>}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>

        {/*TODO: hook up Websocket for notifications*/}
        {this.getWebsocket()}
        <NotificationSystem ref="notificationSystem" allowHTML={this.props.allowHTML} />
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    token: state.auth.token,
    accountID: state.user.accountID,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {

	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Main)
