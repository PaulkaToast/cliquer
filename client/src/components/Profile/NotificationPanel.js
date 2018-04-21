import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/NotificationPanel.css'
import Notification from './Notification'
import { getMessages } from '../../redux/actions'

class NotificationPanel extends Component {
  constructor(props){
    super(props);

    this.state = {
      notifications: {},
    };
  }

  componentDidMount() {
    this.setState({
      notifications: this.props.notifications
    });
  }

  componentWillReceiveProps(nextProps) {
    if(this.props != nextProps) {
      this.setState({
        notifications: nextProps.notifications
      });
    }
  }

  deleteAndRender(messageId) {
    this.props.deleteNotification(messageId);
    var newState = this.state;
    delete newState.notifications[messageId]
    this.setState(newState);
  }

  render() {
    const notifications = this.state.notifications
    return (
      <div className="NotificationPanel">
        <h4 className="notification-header"> Notifications </h4>
        <hr/>
        { notifications 
          && <div>
              {Object.keys(notifications).map((key, i) => {
                return <Notification
                  notification={notifications[key]}
                  i={i}
                  deleteNotification={this.deleteAndRender.bind(this)}
                  acceptNotification={this.props.acceptNotification}
                  rejectNotification={this.props.rejectNotification}
                  markAsRead={this.props.markAsRead}
                  key={notifications[key].messageID}
                />
              })}
            </div>
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
