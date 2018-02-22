import React, { Component } from 'react'
import { Button } from 'reactstrap'

import '../../css/GroupSettings.css'

class GroupSettings extends Component {

  leaveGroup = () => {

  }

  render() {
    return (
      <div className="GroupSettings">
        <Button type="submit" color="warning" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
      </div>
    )
  }
}

export default GroupSettings
