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
import { getGroups } from '../../redux/actions'

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
    if(nextProps.user && nextProps.token && nextProps.user.uid) {
      this.props.getGroups(`https://10.0.0.222:17922/api/getUserGroups?username=${nextProps.user.uid}`, { 'X-Authorization-Firebase': nextProps.token, 'Origin': 'https://10.0.0.222:17922'})
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
    console.log(groups)
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

  toggle = (tab) => {
    if(tab === 'members' && this.state.members !== 'active' ) {
      this.setState({ members: 'active', settings: '' }, () => history.push('/groups/test'))
    }
    if(tab === 'settings' && this.state.settings !== 'active' ) {
      this.setState({ members: '', settings: 'active' }, () => history.push('/groups/test/settings'))
    }
  }

  render() {
    console.log(this.props.groups)
    return (
      
      <Container fluid className="Groups h-100">
   
      <Navbar className="group-nav" color="primary" dark expand="md">
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
            <Route path="/groups/:gid/settings" render={(navProps) => <GroupSettings {...this.props} {...navProps} isOwner={this.isOwner} allowUserRating={this.allowUserRating}/>}/>
          </Switch>*/}
        </Nav>
      </Navbar>        

      <Row className="h-100">
      <Col className="group-list-panel h-100" xs="3">
        {this.renderGroupsList()}
      </Col>
      <Col xs="9">
        <Chat />
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
   /* groups: state.groups ? state.groups.data : [],*/
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getGroups: (url, header) => dispatch(getGroups(url, header)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(Groups)