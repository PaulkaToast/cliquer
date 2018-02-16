import React, { Component } from 'react'

import '../css/Profile.css'
import SkillsPanel from './SkillsPanel'

class Profile extends Component {

  render() {
    return (
      <div className="Profile">
        <SkillsPanel />
      </div>
    )
  }
}

export default Profile
