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
      <Navbar className="filer-navbar" dark expand="md">
      <NavbarBrand>Filter Settings</NavbarBrand>
      <NavbarToggler onClick={this.toggle} />
      <Collapse isOpen={this.state.isOpen} navbar>
      <form className="filter-public-groups" onSubmit={this.sendMessage}>
      <Nav className="ml-auto" navbar>
          <NavItem className="min-rep-container">
          <InputGroup>
            <InputGroupAddon addonType="prepend">
              Minimum Reputation
            </InputGroupAddon>
            <Input minRep={this.state.minRep} type="number" />
          </InputGroup>
          </NavItem>

          <NavItem className="max-dis-container">
          <InputGroup>
            <InputGroupAddon addonType="prepend">
              Maximum Distance
            </InputGroupAddon>
            <Input maxDistance={this.state.maxDistance} type="number" />
            <InputGroupAddon addonType="append">
              mile(s)
            </InputGroupAddon>
          </InputGroup>
          </NavItem>

          <NavItem className="pref-skills-container">
          <InputGroup>
            <InputGroupAddon addonType="prepend">
              Prefered Skills
            </InputGroupAddon>
            <Input placeholder="** Seperate with commas **"/>
          </InputGroup>
          </NavItem>

          <NavItem>
            <Button color="primary">Filter</Button>
          </NavItem>
        </Nav>
        </form>
        </Collapse>
        </Navbar>
        <SearchResults category={'isPublic'} query={this.props.accountID} />
      </div>
    )
  }
}

export default PublicGroups
