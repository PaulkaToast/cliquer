import React, { Component } from 'react'
import { Alert, Badge, Button, InputGroupAddon, Input, 
  InputGroup, Navbar, NavbarToggler, Collapse, NavItem, Nav,
NavbarBrand} from 'reactstrap'

import '../css/PublicGroups.css'
import SearchResults from './SearchResults'

class PublicGroups extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isOpen: false,
      maxDistance: 50,
      prefSkills: [],
      minRep: 0,
    }
  }

  toggle = () => {
    this.setState({ isOpen: !this.state.isOpen });
  }

  render() {
    return (
      <div className="PublicGroups">
        <SearchResults category={'isPublic'} query={this.props.accountID} requestToJoin={this.props.requestToJoin} />
      </div>
    )
  }
}

export default PublicGroups
