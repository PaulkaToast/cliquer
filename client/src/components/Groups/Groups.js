import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route } from 'react-router'
import { Button, ButtonGroup, Col, Row, Container, Navbar,
        NavbarBrand, Nav, NavItem, NavLink, Popover,
        PopoverHeader, PopoverBody, Badge, ListGroup,
        Modal, ModalHeader, ModalBody, ModalFooter,
        Form, FormGroup, Label, Input,
        ListGroupItem  } from 'reactstrap'

import '../../css/Groups.css'
import { history } from '../../redux/store'
import Group from './Group'
import Chat from './Chat'
import GroupMembers from './GroupMembers'
import GroupSettings from './GroupSettings'
import SkillsForm from '../Profile/SkillsForm'
import { getGroups, setCurrentGroup, leaveGroup, deleteGroup, clearNewSkills, setGroupSettings, getProfile } from '../../redux/actions'
import url from '../../server.js'

class Groups extends Component {
  constructor(props) {
    super(props)
    this.state = {
      members: 'active',
      settings: '',
      membersPopOver: false,
      settingsPopOver: false,
      modal: false,
    }
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.user && nextProps.token && nextProps.user.uid && !nextProps.groups) {
      this.props.getGroups(`${url}/api/getUserGroups?username=${nextProps.user.uid}`, { 'X-Authorization-Firebase': nextProps.token })
      if(!nextProps.profile && !nextProps.profileIsLoading) {
        this.props.getProfile(`${url}/api/getProfile?username=${nextProps.user.uid}&type=user`, { 'X-Authorization-Firebase': nextProps.token})
      }
    }
  }

  toggle = () => {
    if(this.state.modal) {
      this.props.clearSkills()
      this.setState({ modal: false })
    } else {
      this.setState({ modal: true })
    }
  }

  toggleM = () => {
    this.setState({
      membersPopOver: !this.state.membersPopOver
    })
  }

  toggleS = () => {
    this.setState({
      settingsPopOver: !this.state.settingsPopOver
    })
  }

  updateSettings = (ev) => {
    //TODO: Fix skill list default values, fix public checkbox default value
    if(ev.preventDefault) ev.preventDefault()
    const skillsReq = this.props.newSkills
    const groupName = ev.target.name.value
    const groupPurpose = ev.target.purpose.value
    const reputationReq = ev.target.reputation.value
    const proximityReq = ev.target.proximity.value
    const isPublic = ev.target.isPublic.checked

    this.props.setSettings(`${url}/api/setGroupSettings?username=${this.props.user.uid}&groupId=${this.props.currentGroup.gid}`, { 'X-Authorization-Firebase': this.props.token}, 
                          JSON.stringify({
                            groupName,
                            groupPurpose,
                            isPublic,
                            reputationReq,
                            proximityReq,
                            skillsReq
                          }))
    this.toggle()
  }

  isOwner = (group) => {
    return this.props.user && group && this.props.user.uid === group.ownerUID
  }

  allowUserRating = (group) => {
    if(this.isOwner(group)) {
      //TODO: REDUX action to change groups.rating to true, sprint 2
    }
  }

  clearGroup = () => {
    this.props.setGroup(null)
    history.push('/groups')
  }

  disbandGroup = () => {
    this.props.deleteGroup(`${url}/api/deleteGroup?username=${this.props.user.uid}&groupId=${this.props.currentGroup.gid}`, { 'X-Authorization-Firebase': this.props.token}, null, this.props.currentGroup.gid)
    this.clearGroup()
  }
  
  leaveGroup = () => {
    this.props.leaveGroup(`${url}/api/leaveGroup?username=${this.props.user.uid}&groupId=${this.props.currentGroup.gid}`, { 'X-Authorization-Firebase': this.props.token}, null, this.props.currentGroup.gid)
    this.clearGroup()
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

  render() {
    const name = this.props.currentGroup ? this.props.currentGroup.groupName : null
    const purpose = this.props.currentGroup ? this.props.currentGroup.groupPurpose : null
    const isPublic = this.props.currentGroup ? this.props.currentGroup.isPublic : null
    const minRep = this.props.currentGroup ? this.props.currentGroup.reputationReq : null
    const reputation = this.props.profile ? this.props.profile.reputation : null
    const skills = this.props.currentGroup ? this.props.currentGroup.skillsReq : null
    const proximity = this.props.currentGroup ? this.props.currentGroup.proximityReq : null

    return (
        <Container fluid className="Groups h-100">
          <Route path="/groups/:gid" render={(navProps) => {
              if(this.props.groups) this.props.setGroup(this.props.groups[navProps.match.params.gid])
              return <div></div>
            }}/>
          <Navbar className="group-nav" color="primary" dark expand="md">
            <NavbarBrand> Groups </NavbarBrand>
              <Nav hidden={!this.props.currentGroup} className="ml-auto" navbar>
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
            
        { this.props.currentGroup &&
          <div>
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
                {this.isOwner(this.props.currentGroup) && <Button type="button" size="lg" onClick={() => this.allowUserRating(/*group*/)}>Allow Rating</Button>}
                {this.isOwner(this.props.currentGroup) && <Button type="button" size="lg" onClick={this.toggle}>Update Settings</Button>}
                <Button type="button" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
                {this.isOwner(this.props.currentGroup) && <Button type="button" size="lg" onClick={this.disbandGroup}>Disband Group</Button>}
              </PopoverBody>
          </Popover>
        </div>
        }
        
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="update-settings-modal">
          <ModalHeader toggle={this.toggle}>Update Settings for {name}</ModalHeader>
          <ModalBody>
            {/*TDOD: Please make this look good Paula*/}
            <Form className="create-group-form" id="settings-form" onSubmit={this.updateSettings}>
              <FormGroup className="required">
                <Label for="name">Group Name</Label>
                <Input type="text" name="name" id="name" defaultValue={name} />
              </FormGroup>
              <FormGroup>
              <Label for="purpose">Purpose</Label>
              <Input type="textarea" name="purpose" id="purpose" defaultValue={purpose} />
            </FormGroup>
              <FormGroup className="required">
                <Label for="reputation">Minimum Reputation</Label>
                <Input type="number" name="reputation" id="repuation" min={0} max={reputation} defaultValue={minRep} />
              </FormGroup>
              <FormGroup className="required">
                <Label for="proximity">Maximum Proximity (Miles)</Label>
                <Input type="number" name="proximity" id="proximity" min={0} defaultValue={proximity} />
              </FormGroup>     
              <FormGroup>
                <Label check>
                  <Input type="checkbox" name="isPublic" defaultValue={isPublic}/>{' '}Public
                </Label>
              </FormGroup>
            </Form>
            <div className="skills-form">
              <Label for="skills">Preferred Skills</Label>
              <SkillsForm id="skills" autoFocus={false} defaultSkills={skills}/>
            </div>
       
          </ModalBody>
          <ModalFooter>
            <Button color="primary" type="button" onClick={() => this.updateSettings({ target: document.querySelector('#settings-form')})}>Submit</Button>{' '}
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </Container>
    )
  }
}

const mapStateToProps = (state) => {
  return {
    newSkills: state.user.newSkills,
    user: state.user.data,
    groups: state.groups ? state.groups.getGroupsData : [],
    token: state.auth.token,
    currentGroup: state.groups.currentGroup,
    profileIsLoading: state.profile && state.profile.getIsLoading ? state.profile.getIsLoading : null,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    clearSkills: () => dispatch(clearNewSkills()),
    getGroups: (url, header) => dispatch(getGroups(url, header)),
    setGroup: (group) => dispatch(setCurrentGroup(group)),
    leaveGroup: (url, header, body, gid) => dispatch(leaveGroup(url, header, body, gid)),
    deleteGroup: (url, header, body, gid) => dispatch(deleteGroup(url, header, body, gid)),
    setSettings: (url, header, body) => dispatch(setGroupSettings(url, header, body)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Groups)