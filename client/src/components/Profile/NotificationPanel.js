import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/NotificationPanel.css'
import Notification from './Notification'

class NotificationPanel extends Component {

  deleteNotification = (i) => {
    //redux call for delete notification
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
    /*notifications: state.user.notifications,*/
	}
}

export default connect(mapStateToProps)(NotificationPanel)
