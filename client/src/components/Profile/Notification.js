import React, { Component } from 'react'
import { Button, ButtonGroup, Card, CardBody } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  

  getResponse = (notification) => {
    switch (notification.type) {
      case 0:
      case 12:
        // Group invite
        return ([
          <h4 className="notification-response" key={this.props.notification.messageID}>Group Invite</h4>,
          <ButtonGroup className="notification-buttons" key={this.props.notification.messageID + "button"}>
            <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Join</Button>
            <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Ignore</Button>
          </ButtonGroup>
      ]);

      case 1:
         // Friend invite
         return ([
          <h4 className="notification-response" key={this.props.notification.messageID}>Friend Invite</h4>,
          <ButtonGroup className="notification-buttons" key={this.props.notification.messageID + "button"}>
            <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Accept</Button>
            <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Reject</Button>
          </ButtonGroup>
      ])
      case 2:
        break;

      case 3:
        // Kick Notification
        return (<h4 className="notification-response" key={this.props.notification.messageID}>
          Kicked ):</h4>
        )

      case 4:
      //Join request
         return ([<h4 className="notification-response" key={this.props.notification.messageID}>
          Join Request</h4>,
          <ButtonGroup className="notification-buttons" key={this.props.notification.messageID + "button"}>
            <Button color="success" onClick={() => this.props.acceptNotification(notification.messageID)}>Accept</Button>
            <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Reject</Button>
          </ButtonGroup>
        ])

      case 6: 
      // Group Request response
        return (
          <h4 className="notification-response">
            Group Request [Response]
          </h4>
        )

      case 7:
      //accepted friend request
        return (<h4 className="notification-response" key={this.props.notification.messageID}>
          Friend Request [Response]</h4>
        )

      case 8:
      //Event Invite
      return (<h4 className="notification-response" key={this.props.notification.messageID}>
        Hey look a wild Event!</h4>
      )
      case 9:
       break;
      case 11:
        break;
        // Mod invite
        // TODO: Show mod application
      default:
        return null
    }
  }

  render() {
    const { notification } = this.props
    if(notification.type === 14 || notification.type === 9) {
      return null
    }
    var read = ""
    if(notification.read){
      read = "yes-"
    }
    return (
      <Card className="notification-card">
        <CardBody>
          <i className={read.concat("read-notification fas fa-asterisk")} onClick={() => this.props.markAsRead(notification.messageID)}></i>
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
