import React, { Component } from 'react'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  
  componentDidMount = () => {
    //get full name and icon with API call here using this.props
  }

  acceptGroupInvite = () => {
    //join group (api call), use redux
  }

  acceptFriendRequest = () => {
    //accept friend request, use redux
  }

  showOffense = () => {
    //show offense modal (see add skills form) SPRINT 3 
  }

  renderGroupInvite = (notification, i) => {
    return (
      <div className="Notification">
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
          <div>You have been invited to <strong>{/*this.props.group.name, use redux*/}</strong></div>
          <ButtonGroup className="buttons">
            <Button color="success" onClick={this.acceptGroupInvite}>Accept</Button>{' '}
            <Button color="warning" onClick={() => this.props.deleteNotification(i)}>Reject</Button>
          </ButtonGroup>
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  renderFriendInvite = (notification, i) => {
    return (
      <div className="Notification">
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
          <div><strong>{/*this.props.friend.name, use redux*/}</strong> has sent you a friend request!</div>
          <ButtonGroup className="buttons">
            <Button color="success" onClick={this.acceptFriendInvite}>Accept</Button>{' '}
            <Button color="warning" onClick={() => this.props.deleteNotification(i)}>Reject</Button>
          </ButtonGroup>
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  renderModWarning = (notification, i) => {
    return (
      <div className="Notification" onClick={this.showOffense}>
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
          You have received a warning from a moderator! Click to view offense.
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  renderKickNotification = (notification, i) => {
    //Sprint 2
    return (
      <div className="Notification">
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
      
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  renderRateRequest = (notification, i) => {
    //Sprint 2 or 3
    return (
      <div className="Notification">
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
      
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  renderBasicNotification = (notification, i) => {
    //Sprint 2 or 3
    return (
      <div className="Notification">
        <div className="notification-icon">ICON</div>
        <div className="notification-info">
      
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(i)}></i> 
      </div>
    )
  }

  render() {
    const { notification, i } = this.props
    switch(notification.type) {
      case 0:
          return this.renderGroupInvite(notification, i)
      case 1:
          return this.renderFriendInvite(notification, i)
      case 2:
          return this.renderModWarning(notification, i)
      case 3: 
          return this.renderKickNotification(notification, i)
      case 4:
          return this.renderRateRequest(notification, i)
      default:
          return this.renderBasicNotification(notification, i)
    }
  }
}

export default Notification
