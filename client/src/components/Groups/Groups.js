import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route } from 'react-router'
import { Button, ButtonGroup, Col, Row, Container, Navbar,
        NavbarBrand, Nav, NavItem, NavLink, Popover,
        PopoverHeader, PopoverBody, Badge, ListGroup,
        ListGroupItem  } from 'reactstrap'

import '../../css/Groups.css'
import { history } from '../../redux/store'
import Group from './Group'
import Chat from './Chat'
import GroupMembers from './GroupMembers'
import GroupSettings from './GroupSettings'
import { getGroups, setCurrentGroup, leaveGroup } from '../../redux/actions'
import url from '../../server.js'

class Groups extends Component {
  constructor(props) {
    super(props)
    this.toggleM = this.toggleM.bind(this);
    this.toggleS= this.toggleS.bind(this);
    this.state = {
      members: 'active',
      settings: '',
      membersPopOver: false,
      settingsPopOver: false,
    }
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.user && nextProps.token && nextProps.user.uid && !nextProps.groups) {
      this.props.getGroups(`${url}/api/getUserGroups?username=${nextProps.user.uid}`, { 'X-Authorization-Firebase': nextProps.token })
    }
  }

  toggleM() {
    this.setState({
      membersPopOver: !this.state.membersPopOver
    });
  }

  toggleS() {
    this.setState({
      settingsPopOver: !this.state.settingsPopOver
    });
  }

  isOwner = (group) => {
    return group.owner && this.props.user.uid === group.owner.uid
  }

  allowUserRating = (group) => {
    if(this.isOwner(group)) {
      //TODO: REDUX action to change groups.rating to true, sprint 2
    }
  }

  renderGroupsList = () => {
    const { groups } = this.props
    return (
      <ListGroup>
          {groups 
          && Object.keys(groups).map((gid, i) => {
            return <Group
                group={groups[gid]}
                key={i}
              />
          })}
      </ListGroup>
    )
  }

  leaveGroup = () => {
    this.props.leaveGroup(`${url}/api/leaveGroup?username=${this.props.user.uid}&groupId=${this.props.currentGroup.gid}`, { 'X-Authorization-Firebase': this.props.token})
  }

  render() {
    return (
      
      <Container fluid className="Groups h-100">
      <Navbar className="group-nav" color="primary" dark expand="md">
      <Route path="/groups/:gid" render={(navProps) => {
        if(this.props.groups) this.props.setGroup(this.props.groups[navProps.match.params.gid])
        return <div></div>
      }}/>
        <NavbarBrand> Groups </NavbarBrand>
        <Nav className="ml-auto" navbar>
          <NavItem>
            <NavLink href="#" id="PopoverM" onClick={this.toggleM}>
              <i className="fas fa-users"></i>
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink href="#" id="PopoverS" onClick={this.toggleS}>
              <i className="fas fa-cog"></i>
            </NavLink>
          </NavItem>
          {/*<Switch>
            <Route exact path="/groups/:gid" render={(navProps) => <GroupMembers {...this.props} {...navProps} isOwner={this.isOwner}/>}/>
            
          </Switch>*/}
        </Nav>
      </Navbar>        

      <Row className="h-100">
      <Col className="group-list-panel h-100" xs="3">
        {this.renderGroupsList()}
      </Col>
      <Col xs="9">
        <Chat group={this.props.currentGroup}/>
      </Col>
      </Row>

      <Popover placement="left" isOpen={this.state.membersPopOver} target="PopoverM" toggle={this.toggleM}>
          <PopoverHeader>Group Members</PopoverHeader>
          <PopoverBody>
          <ListGroup>
              <ListGroupItem className="d-flex justify-content-between align-items-center" action> 
                Kevin &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <Badge color="success" pill>active</Badge>
              </ListGroupItem>

              <ListGroupItem className="d-flex justify-content-between align-items-center" action> 
                Paula
                <Badge color="warning" pill>away</Badge>
              </ListGroupItem>

              <ListGroupItem className="d-flex justify-content-between align-items-center" action> 
                Shawn
                <Badge color="danger" pill>offline</Badge>
              </ListGroupItem>

              <ListGroupItem className="d-flex justify-content-between align-items-center" action> 
                Jordan
                <Badge color="success" pill>active</Badge>
              </ListGroupItem>
            </ListGroup>
          </PopoverBody>
      </Popover>

      <Popover placement="left" isOpen={this.state.settingsPopOver} target="PopoverS" toggle={this.toggleS}>
          <PopoverHeader>Settings</PopoverHeader>
          <PopoverBody>
            {this.isOwner({}) && <Button type="button" size="lg" onClick={() => this.allowUserRating(/*group*/)}>Allow Rating</Button>}
            <Button type="button" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
            {this.isOwner({/*placeholder*/}) && <Button type="button" size="lg" onClick={this.disbandGroup}>Disband Group</Button>}
          </PopoverBody>
      </Popover>

      </Container>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    groups: state.groups ? state.groups.getGroupsData : [],
    token: state.auth.token,
    currentGroup: state.groups.currentGroup
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getGroups: (url, header) => dispatch(getGroups(url, header)),
    setGroup: (group) => dispatch(setCurrentGroup(group)),
    leaveGroup: (url, header) => dispatch(leaveGroup(url, header)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Groups)