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
      messages: [{
        sender: "Jordan Reed",
        message: "Hi ):"
      },
      {
        sender: "Paula Toth",
        message: "Paint the man cut the lines, paint the man cut the lines, ease the man's cry, hush hush now you sleep"
      },
      {
        sender: "Kevin Nagar",
        message : "Look at you, flying through the air majestically. Like an eagle... piloting a blimp."
      },
      {
        sender: "Ammar Askar",
        message : "Please be advised that a noticeable taste of blood is not part of any test protocol but is an unintended side effect of the Aperture Science Material Emancipation Grill, which may, in semi-rare cases, emancipate dental fillings, crowns, tooth enamel, and teeth."
      },
      {
        sender: "GlaDOS",
        message : "Remember when the platform was sliding into the fire pit and I said 'Goodbye' and you were like 'no way' and then I was all 'I was just pretending to murder you'? That was great!"
      },
      {
        sender: "Cave Johnson",
        message: "When life gives you lemons, don't make lemonade. Make life take the lemons back! Get mad! I don't want your D*** lemons, what the h*** am I supposed to do with these? Demand to see life's manager! Make life rue the day it thought it could give Cave Johnson lemons! Do you know who I am? I'm the man who's gonna burn your house down! With the lemons! I'm gonna get my engineers to invent a combustible lemon that burns your house down!"
      },
      {
        sender: "Announcer",
        message: "Good morning. You have been in suspension for -nine nine nine nine nine... nine ni (continues repeating behind the following:)- This courtesy call is to inform you that all test subjects should vacate the Enrichment Center immediately. Any test subject not emerging from suspension at this time will be assumed to have exercised his or her right to remain in extended relaxation, for the duration of the destruction of this facility. If you have questions or concerns regarding this policy, or if you require a Spanish-language version of this message, feel free to take a complimentary piece of stationery from the desk drawer in front of you, and write us a letter. Good luck."
      },
      ]
    }
  }

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
    const messages = this.state.messages;
    return (
      <div className="Chat">
        <div className="message-container">
          {
            messages.map((c) => 
              <Message sender={c.sender} message={c.message}></Message> 
            )
          }
        </div>
        <div className="send-message-container">
          <InputGroup>
            <Input/>
            <InputGroupAddon addonType="append">
              <Button onClick={() => this.sendMessage()} color="success">Send Message</Button>
            </InputGroupAddon>
          </InputGroup>
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
