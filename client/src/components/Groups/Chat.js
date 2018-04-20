import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Alert, Badge, Button, InputGroupAddon, Input, InputGroup,
        Card, CardImg, CardText, CardBody, CardTitle,
        ButtonGroup, UncontrolledTooltip, Modal,
        ModalHeader, ModalBody, InputGroupText } from 'reactstrap'
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
      modal: false
    }
    this.sendUpVote = this.sendUpVote.bind(this);
    this.sendDownVote = this.sendDownVote.bind(this);
    this.toggle = this.toggle.bind(this);
  } 
  toggle(){
    this.setState({
      modal: !this.state.modal
    });
  }
  handleReport = (ev) => {
    const reason = ev.currentTarget.reason.value
    const messageId = this.props.messageId
    this.props.sendReport(reason, messageId);
  }
  sendUpVote = (event) => {
    event.preventDefault();
    var newState = this.state;
    if(!this.state.reaction){
      newState.reaction = true
      newState.up = true
    }else if(this.state.reaction && this.state.up){
      newState.reaction = false
      newState.up = false
    }else if(this.state.reaction && this.state.down){
      newState.down = false
      newState.up = true
    }
    this.setState(newState);
    this.props.sendReaction(this.props.messageId, 0);
  }
  sendDownVote = (event) => {
    event.preventDefault();
    var newState = this.state;
    if(!this.state.reaction){
      newState.reaction = true
      newState.down = true
    }else if(this.state.reaction && this.state.down){
      newState.reaction = false
      newState.down = false
    }else if(this.state.reaction && this.state.up){
      newState.up = false
      newState.down = true
    }
    this.setState(newState);
    this.props.sendReaction(this.props.messageId, 1);
  }
  render() {
  var {message, sender, align, time, up, down, 
    upList, downList, votes, messageId} = this.props;

  var upToolTip = "";
  if (upList.length > 0) {
    upToolTip = <UncontrolledTooltip  placement="top" target={"up"+messageId}>
      {upList.join(", ")}
    </UncontrolledTooltip >
  }
  var downToolTip = "";
  if (downList.length > 0) {
    downToolTip = <UncontrolledTooltip  placement="bottom" target={"down"+messageId}>
      {downList.join(", ")}
    </UncontrolledTooltip >
  }
  var modalDiv = 
    <div>
      <Modal isOpen={this.state.modal} toggle={this.toggle}>
        <ModalHeader>Report Message</ModalHeader>
        <ModalBody>
          <InputGroup>
            <InputGroupAddon addonType="prepend" className="input-header">User</InputGroupAddon>
            <Input placeholder={sender} disabled="true"/>
          </InputGroup>
          <br/>
          <InputGroup>
            <InputGroupAddon addonType="prepend" className="input-header">Msg</InputGroupAddon>
            <Input type="textarea" className="message-box-report" disabled="true" placeholder={message}/>
          </InputGroup>
          <br/>
          <InputGroup>
            <InputGroupAddon addonType="prepend" className="input-header">Reason</InputGroupAddon>
            <Input type="textarea" className="message-box-report" 
              placeholder="Please include your reason for reporting."
              name="reason"/>
          </InputGroup>
          <br/>
          <ButtonGroup>
            <Button color="danger" type="submit" onSubmit={this.handleReport}>Report Message</Button>
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ButtonGroup>
        </ModalBody>
      </Modal>
    </div>

  if (!message) return <div></div>;
  if (align === "sender-message-left"){
    return  <div className={align}>
            <Badge className={align.concat("-badge")}>{sender}</Badge>
            <i class="fas fa-exclamation-triangle -warning" 
              onClick={this.toggle}></i>
            <br/>
              <Alert className={align.concat("-alert single-message")}> {message} </Alert>
              <ButtonGroup vertical className="up-vote-down-vote">
                <i className={up.concat("vote-up fas fa-thumbs-up")}
                  onClick={this.sendUpVote} id={"up"+messageId}></i>
                  {upToolTip}
                <span>{votes}</span>
                <div className="thumbs-down-flip">
                  <i className={down.concat("vote-down fas fa-thumbs-down")}
                    onClick={this.sendDownVote} id={"down"+messageId}></i>
                    {downToolTip}
                </div>
              </ButtonGroup>
              <span className="time-stamp-left">{time}</span>
              {modalDiv}
            </div>
  }
  return (
    <div className={align}>
      <Badge className={align.concat("-badge")}>{sender}</Badge>
      <br/>
        <span className="time-stamp-right">{time}</span>
        <ButtonGroup vertical className="up-vote-down-vote">
          <i className={up.concat("vote-up fas fa-thumbs-up")}
            onClick={this.sendUpVote} id={"up"+messageId}></i>
            {upToolTip}
          <span>{votes}</span>
          <div className="thumbs-down-flip">
            <i className={down.concat("vote-down fas fa-thumbs-down")}
              onClick={this.sendDownVote} id={"down"+messageId}></i>
              {downToolTip}
          </div>
        </ButtonGroup>
        <Alert className={align.concat("-alert single-message")}> {message} </Alert>
        {modalDiv}
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
    if(nextProps.group !== this.props.group){
      var newState = this.state;
      newState.messages = [];
      this.setState(newState);
      setTimeout(() => this.onWebsocketConnect(), 1)
    }
  }

  sendMessage = (event) => {
    event.preventDefault();
    if (this.state.msgInput === "") {
      return;
    }
    this.clientRef.sendMessage('/app/' + this.props.user.uid + '/' 
                               + this.props.group.groupID + '/sendMessage', this.state.msgInput)
    var newState = this.state;
    newState.msgInput = "";
    this.setState(newState);
    event.target.reset()
  }

  sendReaction = (messageId, reaction) => {
    this.clientRef.sendMessage('/app/reactChatMessage/' 
    + this.props.user.uid + '/' + messageId + '/' + this.props.group.groupID
    + '/' + reaction, "")
  }

  sendReport = (reason, messageId) => {
    return;
  }

  handleMessage = (data) => {
    var messages;
    if (data.length === 0) {
      return;
    } else if (data[0]){
      messages = data.map( (m) => {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(m.creationTime["hour"] > 12){
          hour = (m.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          if(m.creationTime["hour"] === 0){
            hour = 12 + ":"
          }else{
            hour = m.creationTime["hour"] + ":"
          }
          ampm = " AM"
        }
        if(Math.floor(m.creationTime["minute"] / 10) === 0){
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
        Object.keys(m.reactions).forEach((key) => {
          if (m.reactions[key] === 0){
            if(key === this.props.accountID){
              upMe = true
              votes = 1
            }else{
              upList.push(this.props.group.groupMemberIDs[key])
            }
          }else{
            if(key === this.props.accountID){
              downMe = true
              votes = -1
            }else{
              downList.push(this.props.group.groupMemberIDs[key])
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
      const newState = this.state;
      newState.shouldScroll = true;
      newState.messages = messages;
      this.setState(newState);
    } else {
        var hour = ""
        var minute = ""
        var time = ""
        var ampm = ""
        if(data.creationTime["hour"] > 12){
          hour = (data.creationTime["hour"] - 12) + ":" 
          ampm = " PM"
        }else{
          if(data.creationTime["hour"] === 0){
            hour = 12 + ":"
          }else{
            hour = data.creationTime["hour"] + ":"
          }
          ampm = " AM"
        }
        if(Math.floor(data.creationTime["minute"] / 10) === 0){
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
        Object.keys(data.reactions).forEach((key) => {
          if (data.reactions[key] === 0){
            if(key === this.props.accountID){
              upMe = true
              votes = 1
            }else{
              upList.push(this.props.group.groupMemberIDs[key])
            }
          }else{
            if(key === this.props.accountID){
              downMe = true
              votes = -1
            }else{
              downList.push(this.props.group.groupMemberIDs[key])
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
        messages = this.state.messages.map((m) => {
          if (m.messageId === data.messageID){
            updated = true;
            return newMsg;
          } else {
            return m;
          }
        });
        
        const newState = this.state;
        if (!updated){
          newState.shouldScroll = true;
          messages.push(newMsg); 
        }
        newState.messages = messages;
        this.setState(newState);
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
      const newState = this.state;
      newState.shouldScroll = false;
      this.setState(newState);
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
              if( c.date !== passingDate){
                dateDiv = <div className="date-div-center"><Badge >{c.date}</Badge></div>
                passingDate = c.date
              } 
              if( c.sender === "this-is-a-group-message"){
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
                sendReaction={this.sendReaction} sendReport={this.sendReport}
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
