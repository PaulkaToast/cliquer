import React, { Component } from 'react'

//import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'

class Profile extends Component {

  render() {
    return (
      <div className="Profile">
        <SkillsPanel />
        <NotificationPanel notifications={[{messageID: 111, type: 0}]}/>
      </div>
    )
  }
}

export default Profile
