import React, { Component } from 'react'
import { NavLink } from 'react-router-dom'
import { ListGroup, ListGroupItem, Badge } from 'reactstrap';

class Group extends Component {

  render() {
    return (
        /*<NavLink to={`/groups/${this.props.group.id}`}>
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
      </NavLink>*/
      <div>
        <ListGroup>
          <ListGroupItem className="d-flex justify-content-between align-items-center" href={`/groups/${this.props.group.id}`} action> 
            {this.props.group.name}
            <Badge color="primary" pill>14</Badge>
          </ListGroupItem>
        </ListGroup>
      </div>
    )
  }
}

export default Group
