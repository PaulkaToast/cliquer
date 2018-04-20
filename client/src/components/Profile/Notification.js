import React, { Component } from 'react'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  

  getResponse = (notification) => {
    switch (notification.type) {
      case 0:
      case 12:
        // Group invite
        return (
            <ButtonGroup>
              <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Join</Button>
              <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Ignore</Button>
            </ButtonGroup>
        )
      case 1:
      case 4:
      case 9:
        // Friend invite
        return (
            <ButtonGroup>
              <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Accept</Button>
              <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Reject</Button>
            </ButtonGroup>
        )
      case 11:
        // Mod invite
        // TODO: Show mod application
      default:
        return null
    }
  }

  render() {
    const { notification } = this.props
    if(notification.type === 14) {
      return null
    }

    return (
      <div className="Notification">
        <div className="notification-info">
         {notification.content}
         {this.getResponse(notification)}
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(notification.messageID)}></i> 
        <i className="fa fa-envelope" onClick={() => this.props.markAsRead(notification.messageID)}></i> 
        {this.getResponse(notification)}
      </div>
    )
  }
}

export default Notification
