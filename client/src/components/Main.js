import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router'
import NotificationSystem from 'react-notification-system'
import { Button, ButtonGroup, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
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
import ModPanel from './ModPanel'
import url from '../server'
import { loadNotifications, handleNotifications, deleteNotification} from '../redux/actions'


class Main extends Component {

  constructor(props) {
    super(props)

    this.state = {
      modal: false,
      groupID: '',
    }
  }

  _notificationSystem = null

  componentDidMount =  () => {
    this._notificationSystem = this.refs.notificationSystem
  }

  handleNotification = (data) => {
    if(data && !data.senderID && !this.props.notifications) {
      this.props.loadNotifications(data)
    } else if(data) {
      switch (data.type) {
        case 0:
        case 12:
          // Group invite
          this._notificationSystem.addNotification({
            title: 'Group Invite',
            message: data.content,
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={() => this.acceptNotification(data.messageID)}>Join</Button>
                <Button color="danger" onClick={() => this.rejectNotification(data.messageID)}>Ignore</Button>
              </ButtonGroup>
            )
          })
          break
        case 1:
          // Friend invite
          this._notificationSystem.addNotification({
            title: 'Friend Invite',
            message: data.content,
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={() => this.acceptNotification(data.messageID)}>Accept</Button>
                <Button color="danger" onClick={() => this.rejectNotification(data.messageID)}>Reject</Button>
              </ButtonGroup>
            )
          })
          break
        case 2:
          // Mod Warning
          this._notificationSystem.addNotification({
            title: 'Warning',
            message: data.content,
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
            message: data.content,
            level: 'error',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
          break
        case 4:
          // Join Request
          this._notificationSystem.addNotification({
            title: 'Join Request',
            message: data.content,
            level: 'success',
            children: (
              <ButtonGroup>
                <Button color="success" onClick={() => this.acceptNotification(data.messageID)}>Accept</Button>
                <Button color="danger" onClick={() => this.rejectNotification(data.messageID)}>Reject</Button>
              </ButtonGroup>
            )
          })
          break
        case 5:
          // Rate request
          this._notificationSystem.addNotification({
            title: 'Rate request',
            message: data.content,
            level: 'success',
          })
          break
        case 6:
          //Accepted join request
          this._notificationSystem.addNotification({
            title: 'Accepted Join Request',
            message: data.content,
            level: 'success',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        break
        case 7:
          //Accepted friend request
          this._notificationSystem.addNotification({
            title: 'Accepted Friend Request',
            message: data.content,
            level: 'success',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        case 8:
          // Event invite
          this._notificationSystem.addNotification({
            title: 'Event Invite',
            message: data.content,
            level: 'success',
            autoDismiss: 8,
            children: (
              <ButtonGroup>
                <Button color="success" onClick={() => this.acceptNotification(data.messageID)}>Join</Button>
                <Button color="danger" onClick={() => this.rejectNotification(data.messageID)}>Ignore</Button>
              </ButtonGroup>
            )
          })
        case 9:
          // Mod request
          // TODO: Show mod application
          this._notificationSystem.addNotification({
            title: 'Mod Request',
            message: data.content,
            level: 'success',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        case 10:
          // Mod request accepted
          this._notificationSystem.addNotification({
            title: 'Mod Application Accepted',
            message: data.content,
            level: 'success',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        case 11:
          // Mod invite
          // TODO: Show mod application
          this._notificationSystem.addNotification({
            title: 'Submit a Mod Application',
            message: data.content,
            level: 'success',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        case 13:
          //Mod report
          //Todo all mod to respond
          this._notificationSystem.addNotification({
            title: 'Report',
            message: data.content,
            level: 'error',
            action: {
              label: 'OK',
              callback: () => this.acceptNotification(data.messageID)
            }
          })
        default:
          // Basic Notification
          // Add basic messages here if necessary
      }
    }
  }

  acceptNotification = (messageID) => {
    this.clientRef.sendMessage(`/app/acceptNotification/${this.props.accountID}/${messageID}`)
  }

  rejectNotification = (messageID) => {
    this.clientRef.sendMessage(`/app/rejectNotification/${this.props.accountID}/${messageID}`)
  }

  deleteNotification = (messageID) => {
    this.clientRef.sendMessage(`/app/deleteNotification/${this.props.accountID}/${messageID}`)
  }

  markAsRead = (messageID) => {
    this.clientRef.sendMessage(`/app/readNotification/${this.props.accountID}/${messageID}`)
  }

  inviteToGroup = (groupID, accountID) => {
    this.clientRef.sendMessage(`/app/inviteToGroup/${this.props.accountID}/${accountID}/${groupID}`)
  }

  sendFriendRequest = (friendID) => {
    this.clientRef.sendMessage(`/app/requestFriend/${this.props.accountID}/${friendID}`)
  }

  requestToJoin = (groupID, ownerID) => {
    this.clientRef.sendMessage(`/app/requestToJoin/${this.props.accountID}/${ownerID}/${groupID}`)
  }

  allowRating = (groupID) => {
    this.clientRef.sendMessage(`/app/${this.props.accountID}/${groupID}/rate`) 
  }

  onWebsocketConnect = () => {
    this.clientRef.sendMessage(`/app/${this.props.accountID}/allMessages`)
  }

  getWebsocket = () => {
    if(this.props.accountID) {
      return <SockJsClient url={`${url}/sockJS`} topics={[`/notification/${this.props.accountID}`]}
          onMessage={this.handleNotification}
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
            <Route path="/profile/:ownerID" render={(navProps) => 
              <Profile 
                {...navProps} 
                sendFriendRequest={this.sendFriendRequest} 
                goToProfile={this.props.goToProfile} 
                markAsRead={this.markAsRead} 
                deleteNotification={this.deleteNotification} 
                inviteToGroup={this.inviteToGroup}
                isMod={this.props.isMod} 
              />}
            />
            <Route path="/groups" render={(navProps) => 
              <Groups 
                {...navProps} 
                {...this.props} 
                allowRating={this.allowRating} 
              />}
            />
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} accountID={this.props.accountID}/>}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path="/mod" render={(navProps) => 
              this.props.isMod()
              ? <ModPanel {...navProps} />
              : <Redirect to="/groups" />
            }/>
            <Route path="/search/:category/:query" render={(navProps) => <SearchResults {...navProps} sendFriendRequest={this.sendFriendRequest} goToProfile={this.props.goToProfile} requestToJoin={this.requestToJoin}/>}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>
        
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
    notifications: state.messages.data,
    groups: state.groups ? state.groups.getGroupsData : [],
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    loadNotifications: (notifications) => dispatch(loadNotifications(notifications)),
    handleNotification: (url, headers) => dispatch(handleNotifications(url, headers)),
    deleteNotification: (messageID) => dispatch(deleteNotification(messageID)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Main)
