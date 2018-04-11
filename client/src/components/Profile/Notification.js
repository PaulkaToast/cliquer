import React, { Component } from 'react'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  
  render() {
    const { notification } = this.props
    return (
      <div className="Notification">
        <div className="notification-info">
         {notification.content}
        </div>
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(notification.messageID)}></i> 
        <i className="fa fa-envelope" onClick={() => this.props.markAsRead(notification.messageID)}></i> 
      </div>
    )
  }
}

export default Notification
