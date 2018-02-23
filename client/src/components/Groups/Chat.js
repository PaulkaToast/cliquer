import React, { Component } from 'react'
import { Alert } from 'reactstrap'
import '../../css/Chat.css'

class Chat extends Component {

  render() {
    return (
      <div className="Chat">
        <div>
          <Alert color="danger">
          Group chat system is currently under construction. Check back in sprint 2!
          </Alert>
        </div>
      </div>
    )
  }
}

export default Chat
