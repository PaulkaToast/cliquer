import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert, Badge, Button, InputGroupAddon, Input, InputGroup,
        Card, CardImg, CardText, CardBody, CardTitle, Row,
        ButtonGroup} from 'reactstrap'
import SockJsClient from 'react-stomp'

import '../../css/Chat.css'
import { getChatLog, postChatMessage, updateChatLog } from '../../redux/actions'
import url from '../../server'
import Logo from '../../img/cliquerLogoWarn.png'

const Message = ({message, sender, align, time}) => {
  if (!message) return <div></div>;
  if (align == "sender-message-left"){
    return  <div className={align}>
            <Badge className={align.concat("-badge")}>{sender}</Badge>
            <br/>
              <Alert className="single-message" className={align.concat("-alert")}> {message} </Alert>
              <ButtonGroup vertical className="up-vote-down-vote">
                <i class="fas fa-thumbs-up"></i>
                <span>0</span>
                <div className="thumbs-down-flip">
                  <i class="fas fa-thumbs-down"></i>
                </div>
              </ButtonGroup>
              <span className="time-stamp-left">{time}</span>
            </div>
  }
  return (
    <div className={align}>
      <Badge className={align.concat("-badge")}>{sender}</Badge>
      <br/>
        <span className="time-stamp-right">{time}</span>
        <ButtonGroup vertical className="up-vote-down-vote">
          <i class="fas fa-thumbs-up"></i>
          <span>0</span>
          <div className="thumbs-down-flip">
            <i class="fas fa-thumbs-down"></i>
          </div>
        </ButtonGroup>
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
                               + this.props.group.groupID + '/sendMessage', this.state.msgInput)
    this.state.msgInput = "";
    event.target.reset()
  }

  handleMessage = (data) => {
    if (data.length == 0) {
      return;
    } else if (data[0]){
      this.state.messages = data.map( (m) => {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(m.creationTime["hour"] > 12){
          hour = (m.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          if(m.creationTime["hour"] == 0){
            hour = 12 + ":"
          }else{
            hour = m.creationTime["hour"] + ":"
          }
          ampm = " AM"
        }
        if(Math.floor(m.creationTime["minute"] / 10) == 0){
          minute = "0" + m.creationTime["minute"]
        }else{
          minute = m.creationTime["minute"]
        }
        time = hour + minute + ampm
        return {sender: m.senderName, message: m.content, id: m.senderID, 
          date: m.creationDate["dayOfWeek"] + " " + m.creationDate["month"] + 
          " " + m.creationDate["dayOfMonth"], time: time}
      })
      this.setState(this.state)
    } else {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(data.creationTime["hour"] > 12){
          hour = (data.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          if(data.creationTime["hour"] == 0){
            hour = 12 + ":"
          }else{
            hour = data.creationTime["hour"] + ":"
          }
          ampm = " AM"
        }
        if(Math.floor(data.creationTime["minute"] / 10) == 0){
          minute = "0" + data.creationTime["minute"]
        }else{
          minute = data.creationTime["minute"]
        }
        time = hour + minute + ampm
      this.state.messages.push({
        sender: data.senderName, message: data.content, id: data.senderID, 
        date: data.creationDate["dayOfWeek"] + " " + data.creationDate["month"] + 
        " " + data.creationDate["dayOfMonth"], time: time
      })
      this.setState(this.state)
    }
  }

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth", block: "end" });
  }
  
  componentDidMount() {
    this.scrollToBottom();
    
  }
  
  componentDidUpdate() {
    this.scrollToBottom();
  }

  onWebsocketConnect() {
    if (this.props.group && this.clientRef && this.clientRef.state.connected) {
      this.clientRef.sendMessage('/app/'+ this.props.user.uid + '/' + this.props.group.groupID + '/messageHistory', "");
    }
  }

  getWebsocket() {
    if (this.props.group) {
      return <SockJsClient url={`${url}/sockJS`} topics={['/group/'+ this.props.user.uid + '/' + this.props.group.groupID, 
    '/group/' + this.props.group.groupID] }
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
    var passingDate = "";

    if(!this.props.group){
      return  <div className="select-a-group-warning">
                <div ref={(el) => { this.messagesEnd = el; }}></div>
                <Card>
                  <CardImg src={Logo} width="90%"/>
                  <CardBody>
                    <CardTitle>
                      Ooops...
                    </CardTitle>
                    <CardText>
                      You need to select a group from the left 
                      or you need to create one using create a group.
                    </CardText>
                  </CardBody>
                </Card>
              </div>
    }

    return (
      <div className="Chat">
        <div className="message-container">
          {
            messages.map((c, index) => {
              var dateDiv = <div></div>
              if( c.date != passingDate){
                var dateDiv = <div className="date-div-center"><Badge >{c.date}</Badge></div>
                passingDate = c.date
              } 
              if( c.sender == "this-is-a-group-message"){
                return <div className="group-message-div" key={index}>
                <Badge className="group-message-badge" color="primary">{c.message}</Badge>
                </div>
              }
              if( c.id === this.props.user.uid ){
                return <div key={index}>{dateDiv}<Message align="sender-message-right" key={index} sender={c.sender} message={c.message} time={c.time}></Message></div>
              } else {
                return <div key={index}>{dateDiv}<Message align="sender-message-left" key={index} sender={c.sender} message={c.message} time={c.time}></Message></div>
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
