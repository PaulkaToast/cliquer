import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert } from 'reactstrap'
import SockJsClient from 'react-stomp'

import '../../css/Chat.css'
import { getChatLog, postChatMessage, updateChatLog } from '../../redux/actions'
import url from '../../server.js'

class Chat extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.user && nextProps.token && nextProps.user.uid && !nextProps.group) {
      //TODO: Add URL to link up with backend
      //TODO: prevent unnecessary calling
      this.props.getLog(``, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  sendMessage = () => {
    const msg = {
      senderId: this.props.user.uid,
      content: 'Test message'
    }
    this.clientRef.sendMessage('/secured/chat', JSON.stringify(msg));
  }

  handleMessage = (data) => {
    console.log(data)
    if(data) {
      const message = data.message
    }
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    //TODO: verify message object structure, add URL to postmessage
    const message = { message: ev.target.message, owner: this.props.user.uid }
    this.props.updateLog(message)
    this.props.postMessage(``, { 'X-Authorization-Firebase': this.props.token})
  }

  render() {
    console.log(window.location.protocol)
    return (
      <div className="Chat">
        <div>
          <button onClick={() => this.sendMessage()} type="button">Send Message</button>
          <Alert color="danger">
          Group chat system is currently under construction. Check back in sprint 2!
          </Alert>
        </div>

        {/*TODO: link up websockets with backend*/}
        <SockJsClient url={`${url}/chat`} topics={['/group/message']}
          onMessage={this.handleMessage}
          ref={ (client) => { this.clientRef = client }} 
          subscribeHeaders={{ 'X-Authorization-Firebase': this.props.token }}
          headers={{ 'X-Authorization-Firebase': this.props.token }}
        />
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    token: state.auth.token,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getLog: (url, header) => dispatch(getChatLog(url, header)),
    postMessage: (url, header) => dispatch(postChatMessage(url, header)),
    updateLog: (message) => dispatch(updateChatLog(message)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Chat)
