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
        <ListGroup>
    
          <ListGroupItem className="d-flex justify-content-between align-items-center" href={`/groups/${this.props.group.id}`} action> 
            {this.props.group.name}
            <Badge color="primary" pill>14</Badge>
          </ListGroupItem>

          <ListGroupItem className="d-flex justify-content-between align-items-center" href={`/groups/${this.props.group.id}`} action> 
            This
            <Badge color="primary" pill>1</Badge>
          </ListGroupItem>

          <ListGroupItem className="active d-flex justify-content-between align-items-center" href={`/groups/${this.props.group.id}`} action> 
            Is An
            <Badge color="danger" pill>2</Badge>
          </ListGroupItem>

          <ListGroupItem className="d-flex justify-content-between align-items-center" href={`/groups/${this.props.group.id}`} action> 
            Example
            <Badge color="primary" pill>3</Badge>
          </ListGroupItem>

        </ListGroup>
    )
  }
}

export default Group
