import React, { Component } from 'react'
import { Button } from 'reactstrap'

import '../../css/GroupSettings.css'

class GroupSettings extends Component {

  leaveGroup = () => {

  }

  disbandGroup = () => {

  }

  render() {
    const { groups, isOwner } = this.props
    console.log(groups)
    console.log(this.props.match.params.gid)
    console.log(groups['test'])
    const group = groups[this.props.match.params.gid]
    return (
      <div className="GroupSettings">
        {isOwner(group) && <Button type="button" size="lg" onClick={() => this.props.allowUserRating(group)}>Allow Rating</Button>}
        <Button type="button" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
        {isOwner(group) && <Button type="button" size="lg" onClick={this.disbandGroup}>Disband Group</Button>}
      </div>
    )
  }
}

export default GroupSettings
