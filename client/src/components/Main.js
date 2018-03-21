import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router'
import NotificationSystem from 'react-notification-system'
import { Button, ButtonGroup } from 'reactstrap'

import '../css/Main.css'
import Navbar from './Navbar'
import CreateGroup from './CreateGroup'
import Groups from './Groups/Groups'
import PublicGroups from './PublicGroups'
import Profile from './Profile/Profile'
import Settings from './Settings'
import SearchResults from './SearchResults'

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

  render() {
    return (
      <div className="Main h-100">
        <Navbar {...this.props} />
        <Switch>
            <Route path="/create" render={(navProps) => <CreateGroup {...navProps} />}/>
            <Route path="/groups" render={(navProps) => <Groups {...navProps} />}/>
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} />}/>
            <Route path="/profile/:ownerUID" render={(navProps) => <Profile {...navProps} />}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path="/search/:category/:query" render={(navProps) => <SearchResults {...navProps} />}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>

        {/*TODO: hook up Websocket for notifications*/}
        <NotificationSystem ref="notificationSystem" allowHTML={this.props.allowHTML} />
      </div>
    )
  }
}

export default Main
