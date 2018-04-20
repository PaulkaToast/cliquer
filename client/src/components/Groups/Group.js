import React, { Component } from 'react'
import { ListGroupItem } from 'reactstrap'

class Group extends Component {

  render() {
    return (
        <ListGroupItem onClick={() => this.props.changeGroup(this.props.group.groupID)} className="group-item d-flex justify-content-between align-items-center" action> 
          {this.props.group.groupName}
        </ListGroupItem>
    )
  }
}

export default Group
