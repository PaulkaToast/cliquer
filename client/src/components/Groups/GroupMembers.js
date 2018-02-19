import React, { Component } from 'react'

import '../../css/GroupMembers.css'
import MemberPreview from './MemberPreview'

class GroupMembers extends Component {

  render() {
    const { groups } = this.props
    const gid = this.props.match.params.gid
    const group = groups[gid]
    return (
      <div className="GroupMembers">
        <ul className="members">
            {group.members.map((member, i) => {
              return <MemberPreview
                member={member}
                key={i}
              />
            })}
        </ul>
      </div>
    )
  }
}

export default GroupMembers
