import React, { Component } from 'react'
import { Button, Navbar, NavbarBrand, Nav, NavItem, NavLink, Collapse } from 'reactstrap'
import '../css/Navbar.css'
import Logo from '../img/cliquerLogo-sm.png'
import { auth } from '../firebase'

class Navigationbar extends Component {
//

  render() {
    return (
      <div>
        <Navbar color="primary" dark expand="md">
        <NavbarBrand className="cliquer-brand"><img src={Logo} alt="" /></NavbarBrand>
          <Nav className="mr-auto" navbar>
             <NavItem>
              <NavLink href="/groups">Groups</NavLink>
            </NavItem>
            <NavItem>
              <NavLink href="/profile">Profile</NavLink>
            </NavItem>
            <NavItem>
              <NavLink href="/settings">Settings</NavLink>
            </NavItem> 
            </Nav>
            <Nav className="ml-auto" navbar>
            <NavItem>
              <Button color="secondary" onClick={auth.logOut}>Log Out</Button>
            </NavItem>
            </Nav>
        </Navbar>
      </div>
    )
  }
}

export default Navigationbar
