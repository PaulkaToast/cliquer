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

class Message extends Component{
  constructor(props){
    super(props);

    this.state = {
      reaction: false,
      up: false,
      down: false,
    }
    this.sendUpVote = this.sendUpVote.bind(this);
    this.sendDownVote = this.sendDownVote.bind(this);
  } 
  sendUpVote = (event) => {
    event.preventDefault(); 
    if(!this.state.reaction){
      this.state.reaction = true
      this.state.up = true
    }else if(this.state.reaction && this.state.up){
      this.state.reaction = false
      this.state.up = false
    }else if(this.state.reaction && this.state.down){
      this.state.down = false
      this.state.up = true
    }
    this.props.sendReaction(this.props.messageId, 0);
  }
  sendDownVote = (event) => {
    event.preventDefault(); 
    if(!this.state.reaction){
      this.state.reaction = true
      this.state.down = true
    }else if(this.state.reaction && this.state.down){
      this.state.reaction = false
      this.state.down = false
    }else if(this.state.reaction && this.state.up){
      this.state.up = false
      this.state.down = true
    }
    this.props.sendReaction(this.props.messageId, 1);
  }
  render() {
  var {message, sender, align, time, up, down, 
    upList, downList, votes, messageId} = this.props;

  if (!message) return <div></div>;
  if (align == "sender-message-left"){
    return  <div className={align}>
            <Badge className={align.concat("-badge")}>{sender}</Badge>
            <br/>
              <Alert className="single-message" className={align.concat("-alert")}> {message} </Alert>
              <ButtonGroup vertical className="up-vote-down-vote">
                <i className={up.concat("vote fas fa-thumbs-up")}
                  onClick={this.sendUpVote}></i>
                <span>{votes}</span>
                <div className="thumbs-down-flip">
                  <i className={down.concat("vote fas fa-thumbs-down")}
                    onClick={this.sendDownVote}></i>
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
          <i className={up.concat("vote fas fa-thumbs-up")}
            onClick={this.sendUpVote}></i>
          <span>{votes}</span>
          <div className="thumbs-down-flip">
            <i className={down.concat("vote fas fa-thumbs-down")}
              onClick={this.sendDownVote}></i>
          </div>
        </ButtonGroup>
        <Alert className="single-message" className={align.concat("-alert")}> {message} </Alert>
    </div>);
  };
}

class Chat extends Component {

  constructor(props) {
    super(props);

    this.state = {
      messagesEnd: "",
      messages: [],
      msgInput: "",
      shouldScroll: false
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

  sendReaction = (messageId, reaction) => {
    this.clientRef.sendMessage('/app/reactChatMessage/' 
    + this.props.user.uid + '/' + messageId + '/' + this.props.group.groupID
    + '/' + reaction, "")
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

        var upMe = false
        var downMe = false
        var downList = []
        var upList = []
        var votes = 0
        Object.keys(m.reactions).map((key) => {
          if (m.reactions[key] == 0){
            if(key == this.props.user.uid){
              upMe = true
              votes = 1
            }else{
              upList.push(key)
            }
          }else{
            if(key == this.props.user.uid){
              downMe = true
              votes = -1
            }else{
              downList.push(key)
            }
          }
        })
        votes = votes + upList.length - downList.length
        return {sender: m.senderName, message: m.content, id: m.senderID, 
          date: m.creationDate["dayOfWeek"] + " " + m.creationDate["month"] + 
          " " + m.creationDate["dayOfMonth"], time: time, upMe: upMe,
          downMe: downMe, downList: downList, upList: upList, votes: votes, 
          messageId: m.messageID}
      })
      this.state.shouldScroll = true;
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

        var upMe = false
        var downMe = false
        var downList = []
        var upList = []
        var votes = 0
        Object.keys(data.reactions).map((key) => {
          if (data.reactions[key] == 0){
            if(key == this.props.user.uid){
              upMe = true
              votes = 1
            }else{
              upList.push(key)
            }
          }else{
            if(key == this.props.user.uid){
              downMe = true
              votes = -1
            }else{
              downList.push(key)
            }
          }
        })
        votes = votes + upList.length - downList.length;
        const newMsg = {
          sender: data.senderName, message: data.content, id: data.senderID, 
          date: data.creationDate["dayOfWeek"] + " " + data.creationDate["month"] + 
          " " + data.creationDate["dayOfMonth"], time: time, upMe: upMe,
          downMe: downMe, downList: downList, upList: upList, votes: votes,
          messageId: data.messageID
        };

        var updated = false
        this.state.messages = this.state.messages.map((m) => {
          if (m.messageId == data.messageID){
            updated = true;
            return newMsg;
          } else {
            return m;
          }
        });
        
        if (!updated){
          this.state.messages.push(newMsg); 
        }
        this.setState(this.state);
    }
  }

  scrollToBottom = () => {
    this.messagesEnd.scrollIntoView({ behavior: "smooth", block: "end" });
  }
  
  componentDidMount() {
    this.scrollToBottom();
    
  }
  
  componentDidUpdate() {
    if (this.state.shouldScroll){
      this.scrollToBottom();
      this.state.shouldScroll = false
    }
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
              var upS = "-"
              var downS = "-"
              if( c.upMe ){
                upS = "up-"
              }
              if( c.downMe ){
                downS = "down-"
              }
              if( c.id === this.props.user.uid ){
                return <div key={index}>{dateDiv}<Message align="sender-message-right" 
                key={index} sender={c.sender} message={c.message} time={c.time}
                up={upS} down={downS} upList={c.upList} downList={c.downList}
                votes={c.votes} messageId={c.messageId}
                sendReaction={this.sendReaction}></Message></div>
              } else {
                return <div key={index}>{dateDiv}<Message align="sender-message-left" 
                key={index} sender={c.sender} message={c.message} time={c.time}
                up={upS} down={downS} upList={c.upList} downList={c.downList}
                votes={c.votes} messageId={c.messageId}
                sendReaction={this.sendReaction}
                ></Message></div>
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
