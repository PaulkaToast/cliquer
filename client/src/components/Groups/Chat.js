import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert, Badge, Button, InputGroupAddon, Input, InputGroup} from 'reactstrap'
import SockJsClient from 'react-stomp'

import '../../css/Chat.css'
import { getChatLog, postChatMessage, updateChatLog } from '../../redux/actions'
import url from '../../server'

const Message = ({message, sender, align}) => {
  if (!message) return <div></div>;
  return (
    <div className={align}>
      <Badge className={align.concat("-badge")}>{sender}</Badge>
      <br/>
      <Alert className="single-message" className={align.concat("-alert")}> {message} </Alert>
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
    if(nextProps.group != this.props.group){
      this.state.messages = [];
      this.setState(this.state);
      setTimeout(() => this.onWebsocketConnect(), 1)
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
    this.clientRef.sendMessage('/app/' + this.props.user.uid + '/' 
                               + this.props.group.groupID + '/' + this.state.msgInput +'/sendMessage');
    this.state.msgInput = "";
    event.target.reset();
  }

  handleMessage = (data) => {
    //if data is an array.
    if(data.type) {
      this.props.handleNotification(data)
    } else {
      if (data[0]){
        this.state.messages = data.map( (m) => {
          return {sender: m.senderName, message: m.content, id: m.senderID}
        })
        this.setState(this.state)
      } else {
        this.state.messages.push({
          sender: data.senderName, message: data.content, id: data.senderID
        })
        this.setState(this.state)
      }
    }
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
    if (this.props.group && this.clientRef.state.connected) {
      //YYYY-MM-DD
      this.clientRef.sendMessage('/app/'+ this.props.user.uid + '/' + this.props.group.groupID + '/messageHistory', "");
    }
  }

  getWebsocket() {
    if (this.props.group) {
      return <SockJsClient url={`${url}/sockJS`} topics={['/group/'+ this.props.user.uid + '/' + this.props.group.groupID]}
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
    const messages = this.state.messages;

    return (
      <div className="Chat">
        <div className="message-container">
          {
            messages.map((c, index) => { 
              if( c.id === this.props.user.uid ){
                return <Message align="sender-message-right" key={index} sender={c.sender} message={c.message}></Message>
              } else {
                return <Message align="sender-message-left" key={index} sender={c.sender} message={c.message}></Message> 
              }
            })
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
