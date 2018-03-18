import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert, Badge, Button, InputGroupAddon, Input, InputGroup} from 'reactstrap'
import SockJsClient from 'react-stomp'

import '../../css/Chat.css'
import { getChatLog, postChatMessage, updateChatLog } from '../../redux/actions'
import url from '../../server.js'

const Message = ({message, sender}) => {
  return (
    <div>
      <Badge color="secondary">{sender}</Badge>
      <Alert className="single-message" color="secondary"> {message} </Alert>
    </div>);
};

class Chat extends Component {

  constructor(props) {
    super(props);

    this.state = {
      messagesEnd: "",
      messages: [],
      msgInput: ""
    }
    this.handleChange = this.handleChange.bind(this);
    this.sendMessage = this.sendMessage.bind(this);
  }

  handleChange(event) {
    this.setState({msgInput: event.target.value});
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.user && nextProps.token && nextProps.user.uid && !nextProps.group) {
      //TODO: Add URL to link up with backend
      //TODO: prevent unnecessary calling
      this.props.getLog(``, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  sendMessage = (event) => {
    event.preventDefault();
    if (this.state.msgInput == "") {
      return;
    }
    const msg = {
      senderId: this.props.user.uid,
      content: this.state.msgInput,
    }
    this.clientRef.sendMessage('/chat/'+  this.props.group.groupID +'/sendMessage', JSON.stringify(msg));
    this.state.msgInput = "";
    event.target.reset();
  }

  handleMessage = (data) => {
    //if data is an array
    if (data[0]){
      this.state.messages = data.map( (m) => {
        return {sender: m.senderName, message: m.content}
      })
      this.setState(this.state)
    } else {
      this.state.messages.push({
        sender: data.senderName, message: data.content
      })
      this.setState(this.state)
    }
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    //TODO: verify message object structure, add URL to postmessage
    const message = { message: ev.target.message, owner: this.props.user.uid }
    this.props.updateLog(message)
    this.props.postMessage(``, { 'X-Authorization-Firebase': this.props.token})
  }

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth" });
  }
  
  componentDidMount() {
    this.scrollToBottom();
    
  }
  
  componentDidUpdate() {
    this.scrollToBottom();
  }

  onWebsocketConnect() {
    if (this.props.group) {
      this.clientRef.sendMessage('/chat/'+ this.props.user.uid + '/' + this.props.group.groupID +'/messageHistory', "");
    }
  }

  getWebsocket() {
    if (this.props.group) {
      return <SockJsClient url={`${url}/sockJS`} topics={['/group/'+ this.props.group.groupID + '/message', '/group/' + this.props.user.uid + '/' + this.props.group.groupID]}
          onMessage={this.handleMessage.bind(this)}
          onConnect={this.onWebsocketConnect.bind(this)}
          ref={ (client) => { this.clientRef = client }} 
          subscribeHeaders={{ 'X-Authorization-Firebase': this.props.token }}
          headers={{ 'X-Authorization-Firebase': this.props.token }}
          debug
        />
    } else {
      return;
    }
  }

  render() {
    console.log(window.location.protocol)
    const messages = this.state.messages;

    return (
      <div className="Chat">
        <div className="message-container">
          {
            messages.map((c, index) => 
              <Message key={index} sender={c.sender} message={c.message}></Message> 
            )
          }
          <div ref={(el) => { this.messagesEnd = el; }}></div>
        </div>
        <div className="send-message-container">
        <form onSubmit={this.sendMessage}>
          <InputGroup>
            <Input value={this.state.value} onChange={this.handleChange}/>
            <InputGroupAddon addonType="append">
              <Button color="success">Send Message</Button>
            </InputGroupAddon>
          </InputGroup>
        </form>
        </div>
        {this.getWebsocket()}
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
