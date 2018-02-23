import React, { Component } from 'react'
import { ButtonGroup, Button } from 'reactstrap'

import '../../css/GroupMembers.css'

class GroupMembers extends Component {

  rate = (member, group) => {
    //SPRINT 2
  }

  kick = (member, group) => {
    if(this.props.isOwner(group)) {
      //SPRINT 2
    }
  }

  renderMemberPreview = (member, i) => {
    const group = this.props.groups[this.props.match.params.gid]
    return (
      <div className="member-preview" key={i}>
        <div className="member-icon">ICON</div>
        <div className="member-info">
          <div className="member-name">{member.name}</div>
          <div className="member-role">{member.role}</div>
        </div>
        <ButtonGroup vertical className="buttons">
            {this.props.isOwner(group) && <Button color="primary" onClick={() => this.kick(member, group)}>Kick</Button>}{' '}
            {group.rating && <Button color="primary" onClick={() => this.rate(member, group)}>Rate</Button>}
        </ButtonGroup>
      </div>)
  }

  render() {
    const { groups } = this.props
    const gid = this.props.match.params.gid
    const group = groups[gid]
    return (
      <div className="GroupMembers">
        {group && group.members &&
        <ul className="members">
            {group.members.map((member, i) => {
              return this.renderMemberPreview(member, i)
            })}
        </ul>}
      </div>
    )
  }
}

export default GroupMembers
