import React, { Component } from 'react'
import { Alert, Badge, Button, InputGroupAddon, Input, 
  InputGroup, Navbar, NavbarToggler, Collapse, NavItem, Nav,
NavbarBrand} from 'reactstrap'
import '../css/PublicGroups.css'

/*const PublicGroup = ({groupName, groupPurpose, skillsReq, reputationReq}) => {
  if (!groupName) return <div></div>;
  return (
    
};*/

class PublicGroups extends Component {
  constructor(props) {
    super(props);

    this.state = {
      isOpen: false,
      maxDistance: 50,
      prefSkills: [],
      minRep: 0,
      results: [
        { groupName: "Hoops",
          groupPurpose: "To play basketball",
          skillsReq: ["sports", "basketball"],
          reputationReq: 0
        },
        { groupName: "Styxx",
          groupPurpose: "To make an app",
          skillsReq: ["Coding"],
          reputationReq: 10
        },
        { groupName: "Cliquer",
          groupPurpose: "To create a web app that facilitates the teaming of people who may have never met before",
          skillsReq: ["C++, Java, teamwork"],
          reputationReq: 50
        },
        { groupName: "Poop",
          groupPurpose: "No skills required",
          skillsReq: [],
          reputationReq: 2
        },
        { groupName: "The Incredibles",
          groupPurpose: "Hang on Honey!",
          skillsReq: ["SuperPowers"],
          reputationReq: 100
        },
        { groupName: "Nap Club",
          groupPurpose: "ZZZZZzzzzzzzzzzzzzzzzzzz",
          skilsReq: ["Sound Sleeper", "Alarm Snoozer", "Day Dreamer", "Tired"],
          reputationReq: 0
        }
      ]
    }
    this.toggle = this.toggle.bind(this);
  }

  toggle() {
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

      </div>
    )
  }
}

export default PublicGroups
