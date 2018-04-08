import React, { Component } from 'react'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/GroupSettings.css'

class GroupSettings extends Component {

  leaveGroup = () => {

  }

  disbandGroup = () => {

  }

  render() {
    const { groups, isOwner } = this.props
    const group = groups[this.props.match.params.gid]
    return (
      <div className="GroupSettings">
        <ButtonGroup vertical>
          {isOwner(group) && <Button type="button" size="lg" onClick={() => this.props.allowUserRating(group)}>Allow Rating</Button>}
          <Button type="button" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
          {isOwner(group) && <Button type="button" size="lg" onClick={this.disbandGroup}>Disband Group</Button>}
        </ButtonGroup>
      </div>
    )
  }
}

export default GroupSettings
