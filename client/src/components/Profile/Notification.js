import React, { Component } from 'react'
import { Button, ButtonGroup, Card, CardBody, Modal,
          ModalHeader, ModalBody, ModalFooter, Form,
          InputGroup, InputGroupAddon, Input } from 'reactstrap'

import '../../css/Notification.css'

class Notification extends Component {
  
  constructor(props) {
    super(props)

    this.state = { 
      modal: false
    }
  }

  toggle = () => {
    this.setState({ modal: !this.state.modal })
  }

  handleSubmit = (ev) => {
    ev.preventDefault();
    this.toggle();
    const reason = ev.currentTarget.reason.value;
    const messageId = this.props.notification.messageID;
    this.props.applyforModer(messageId, reason);
    //this.props.acceptNotification(messageId);
  }

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
      case 5:
        //rate user
        return (<h4 className="notification-response" key={this.props.notification.messageID}>
        Rate Enabled</h4>)

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
        return ([<h4 className="notification-response" key={this.props.notification.messageID}>
          Ready to become a Mod?</h4>,
        <ButtonGroup className="notification-buttons" key={this.props.notification.messageID + "button"}>
          <Button color="success" onClick={() => this.toggle()}>Accept</Button>
          <Button color="danger" onClick={() => this.props.rejectNotification(notification.messageID)}>Reject</Button>
        </ButtonGroup>,

        <Modal isOpen={this.state.modal} toggle={this.toggle} className="mod-application-modal">
        <ModalHeader toggle={this.toggle}>Mod Application</ModalHeader>
        <ModalBody>
        <Form onSubmit={this.handleSubmit}>
            <InputGroup>
              <InputGroupAddon addonType="prepend" className="input-header" required="true">Reason</InputGroupAddon>
              <Input type="textarea" className="message-box-report" 
                placeholder="Let us know why you would want to become a moderator!"
                name="reason"/>
            </InputGroup>
            <br/>
            <ButtonGroup>
              <Button color="success" type="submit">Apply</Button>
              <Button color="secondary" onClick={this.toggle}>Cancel</Button>
            </ButtonGroup>
          </Form>
        </ModalBody>
      </Modal>
        ])
        break;
        // Mod invite
        return 
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
