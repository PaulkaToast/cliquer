import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/NotificationPanel.css'
import Notification from './Notification'
import { getMessages } from '../../redux/actions'

class NotificationPanel extends Component {

  deleteNotification = (i) => {
    //redux call for delete notification
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.user && nextProps.user.uid && nextProps.token) {
      this.props.getMessages(`https://localhost:17922/api/getMessages?username=${this.props.user.uid}`, { 'X-Authorization-Firebase': this.props.token})
    }
  }

  render() {
    const { notifications } = this.props
    return (
      <div className="NotificationPanel">
        { notifications 
          && <ul className="notifications">
              {notifications.map((notification, i) => {
                return <Notification
                  notification={notification}
                  i={i}
                  deleteNotification={this.deleteNotification}
                  key={notification.messageID}
                />
              })}
            </ul>
        }
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    messages: state.messages ? state.messages.data : [],
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getMessages: (url, header) => dispatch(getMessages(url, header)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(NotificationPanel)
