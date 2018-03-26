import React, { Component } from 'react'
import { NavLink } from 'react-router-dom'
import { ListGroup, ListGroupItem, Badge } from 'reactstrap'

class Group extends Component {

  render() {
    const { group } = this.props
    return (
        <ListGroupItem onClick={() => this.props.changeGroup(this.props.group.groupID)} className="d-flex justify-content-between align-items-center" action> 
          {this.props.group.groupName}
          <Badge color="primary" pill>14</Badge>
        </ListGroupItem>
    )
  }
}

export default Group
