import React, { Component } from 'react'
import { NavLink } from 'react-router-dom'

class Group extends Component {

  render() {
    return (
        <NavLink to={`/groups/${this.props.group.id}`}>
        <li>
            <div className="group">
              <div className="group-icon">
                ICON
              </div>
              <div className="group-name">
                {this.props.group.name}
              </div>
            </div>
        </li>
      </NavLink>
    )
  }
}

export default Group
