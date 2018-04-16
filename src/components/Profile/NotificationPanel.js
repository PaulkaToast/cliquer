import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/NotificationPanel.css'
import Notification from './Notification'
import { getMessages } from '../../redux/actions'
import url from '../../server.js'

class NotificationPanel extends Component {


  render() {
    const { notifications } = this.props
    console.log(notifications)
    return (
      <div className="NotificationPanel">
        { notifications 
          && <ul className="notifications">
              {Object.keys(notifications).map((key, i) => {
                return <Notification
                  notification={notifications[key]}
                  i={i}
                  deleteNotification={this.props.deleteNotification}
                  key={notifications[key].messageID}
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
    notifications: state.messages ? state.messages.data : null,
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getMessages: (url, header) => dispatch(getMessages(url, header)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(NotificationPanel)