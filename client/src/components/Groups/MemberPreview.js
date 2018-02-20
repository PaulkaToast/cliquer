import React, { Component } from 'react'

class MemberPreview extends Component {

  render() {
    const { member } = this.props
    return (
      <div className="MemberPreview">
        <div className="member-icon">ICON</div>
        <div className="member-info">
            <div className="member-name">NAME</div>
            <div className="member-role">ROLE</div>
        </div>
      </div>
    )
  }
}

export default MemberPreview
