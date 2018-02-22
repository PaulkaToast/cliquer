import React, { Component } from 'react'

class MemberPreview extends Component {

  render() {
    const { member } = this.props
    return (
      <div className="MemberPreview">
        <div className="member-icon">ICON</div>
        <div className="member-info">
            <div className="member-name">{member.name}</div>
            <div className="member-role">{member.role}</div>
        </div>
      </div>
    )
  }
}

export default MemberPreview
