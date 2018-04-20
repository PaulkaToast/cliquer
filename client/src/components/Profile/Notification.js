import React, { Component } from 'react'
import { Button, ButtonGroup, Card, CardTitle, CardBody } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  

  getResponse = (notification) => {
    switch (notification.type) {
      case 6:
        return (
          <h4 className="notification-response">
            Group Request [Response]
          </h4>
        )
      case 0:
        
      case 8:

      case 12:
        // Group invite
        return ([
            <h4 className="notification-response">Group Invite</h4>,
            <ButtonGroup className="notification-buttons">
              <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Join</Button>
              <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Ignore</Button>
            </ButtonGroup>
        ]);
      case 1:
        
      case 4:
        // Friend invite
        return ([
            <h4 className="notification-response">Friend Invite</h4>,
            <ButtonGroup className="notification-buttons">
              <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Accept</Button>
              <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Reject</Button>
            </ButtonGroup>
        ])
      case 9:
        // Mod request
        // TODO: Show mod application
        return
      case 11:
        // Mod invite
        // TODO: Show mod application
        return
      default:
        return
    }
  }

  render() {
    const { notification } = this.props
    if(notification.type === 14) {
      return
    }

    return (
      <Card className="notification-card">
        <CardBody>
          <i className="fas fa-asterisk read-notification" onClick={() => this.props.markAsRead(notification.messageID)}></i>
          {this.getResponse(notification)}
          <hr/>
          <div className="d-flex justify-content-between align-items-center">
            {notification.content}
            <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(notification.messageID)}></i>
          </div>       
        </CardBody>
      </Card>
    )
  }
}

export default Notification
