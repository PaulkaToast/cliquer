import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route } from 'react-router'
import { Button, ButtonGroup, Col, Row, Container, Navbar,
        NavbarBrand, Nav, NavItem, NavLink, Popover,
        PopoverHeader, PopoverBody, Badge, ListGroup,
        Modal, ModalHeader, ModalBody, ModalFooter,
        Form, FormGroup, Label, Input,
        ListGroupItem  } from 'reactstrap'
import Toggle from 'react-toggle'

import '../../css/Groups.css'
import { history } from '../../redux/store'
import Group from './Group'
import Chat from './Chat'
import SkillsForm from '../Profile/SkillsForm'
import { getGroups, setCurrentGroup,
         leaveGroup, deleteGroup, clearNewSkills, setGroupSettings, 
         getProfile, kick, getRateForm, postRateForm, inviteAll,
         createEvent } from '../../redux/actions'
import url from '../../server'

class Groups extends Component {
  constructor(props) {
    super(props)
    this.state = {
      members: 'active',
      settings: '',
      membersPopOver: false,
      settingsPopOver: false,
      modal: false,
      modalR: false,
      modalE: false,
      memberID: '',
    }
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.location && nextProps.location.pathname && nextProps.groups && !nextProps.currentGroup) {
      const groupID = nextProps.location.pathname.substring(8)
      if(groupID) {
        this.props.setGroup(nextProps.groups[groupID])
      }
    }

    if(nextProps.user && nextProps.token && nextProps.user.uid && !nextProps.groups) {
        this.props.getGroups(`${url}/api/getUserGroups?username=${nextProps.user.uid}`, { 'X-Authorization-Firebase': nextProps.token })
    }
  }
 
  // Detailed settings modal
  toggle = () => {
    if(this.state.modal) {
      this.props.clearSkills()
      this.setState({ modal: false })
    } else {
      this.setState({ settingsPopOver: false})
      this.setState({ modal: true })
    }
  }

  // Rate modal
  toggleR = () => {
    this.setState({ modalR: !this.state.modalR })
  }

  // Event modal
  toggleE = () => {
    this.setState({ modalE: !this.state.modalE })
  }

  // Members Popover
  toggleM = () => {
    this.setState({
      membersPopOver: !this.state.membersPopOver
    })
  }

  // Settings Popover
  toggleS = () => {
    this.setState({
      settingsPopOver: !this.state.settingsPopOver
    })
  }

  updateSettings = (ev) => {
    this.toggleS() 
    if(ev.preventDefault) ev.preventDefault()
    const skillsReq = this.props.newSkills
    const groupName = ev.target.name.value
    const groupPurpose = ev.target.purpose.value
    const reputationReq = ev.target.reputation.value
    const proximityReq = ev.target.proximity.value
    const isPublic = ev.target.isPublic.checked

    this.props.setSettings(`${url}/api/setGroupSettings?username=${this.props.user.uid}&groupId=${this.props.currentGroup.groupID}`, { 'X-Authorization-Firebase': this.props.token}, 
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

  createEvent = (ev) => {
    this.toggleS() 
    if(ev.preventDefault) ev.preventDefault()
    const skillsReq = this.props.newSkills
    const name = ev.target.name.value
    const purpose = ev.target.purpose.value
    const proximity = ev.target.proximity.value

    this.props.createEvent(`${url}/api/createEvent?userId=${this.props.accountID}&groupId=${this.props.currentGroup.groupID}`, { 'X-Authorization-Firebase': this.props.token}, 
                          JSON.stringify({
                            name,
                            purpose,
                            proximity,
                            skillsReq
                          }))
    this.toggleE()
  }

  isOwner = (group) => {
    return group && this.props.accountID === group.groupLeaderID
  }

  canRate = (accountID, memberID) => {
    let canRate = false
    
    if(this.props.currentGroup.ratingsToGive[accountID] && Object.keys(this.props.currentGroup.ratingsToGive).length > 0) {
      this.props.currentGroup.ratingsToGive[accountID].forEach((element) => {
        if(memberID === element) {
          canRate = true
        }
      })
    }
    return canRate
  }

  getRateForm = (group, memberID) => {
    this.toggleM()
    this.props.getRateForm(`${url}/api/getRateForm?userId=${this.props.accountID}&rateeId=${memberID}&groupId=${group.groupID}`, { 'X-Authorization-Firebase': this.props.token})
    this.toggleR()
    this.setState({ memberID })
  }

  sendRating = (ev) => {
    if(ev.preventDefault) ev.preventDefault()
    const length = this.props.rateForm ? Object.keys(this.props.rateForm).length : 0
    const endorse = ev.target.endorse.checked
    let skills = {}
    Object.keys(this.props.rateForm).map((key, i) => {
      skills[key] = ev.target[`skill${i}`].value
    })
    
    this.props.postRateForm(`${url}/api/rateUser?userId=${this.props.accountID}&rateeId=${this.state.memberID}&groupId=${this.props.currentGroup.groupID}&endorse=${endorse}`, { 'X-Authorization-Firebase': this.props.token}, JSON.stringify(skills))
    this.setState({ memberID: '' })
    this.toggleR()
  }

  kickUser = (group, memberID) => {
    this.toggleM()
    this.props.kick(`${url}/api/kick?userId=${this.props.accountID}&kickedId=${memberID}&groupId=${group.groupID}`, { 'X-Authorization-Firebase': this.props.token}, { gid: group.groupID, memberID})
  }

  clearGroup = () => {
    this.props.setGroup(null)
    history.push('/groups')
  }

  changeGroup = (groupID) => {
    this.props.setGroup(this.props.groups[groupID])
    history.push(`/groups/${groupID}`)
  }

  inviteAll = () => {
    this.props.inviteAll(`${url}/api/inviteAll?userId=${this.props.accountID}&groupId=${this.props.currentGroup.groupID}`, { 'X-Authorization-Firebase': this.props.token})
  }

  disbandGroup = () => {
    this.props.deleteGroup(`${url}/api/deleteGroup?username=${this.props.user.uid}&groupId=${this.props.currentGroup.groupID}`, { 'X-Authorization-Firebase': this.props.token}, null, this.props.currentGroup.groupID)
    this.clearGroup()
  }
  
  leaveGroup = () => {
    this.props.leaveGroup(`${url}/api/leaveGroup?username=${this.props.user.uid}&groupId=${this.props.currentGroup.groupID}`, { 'X-Authorization-Firebase': this.props.token}, null, this.props.currentGroup.groupID)
    this.clearGroup()
  }

  renderMemberList = (group) => {
    return (
      <ListGroup>
          {this.props.currentGroup
          && Object.keys(this.props.currentGroup.groupMemberIDs).map((memberID, i) => {
            if(memberID !== this.props.accountID) {
              return ( 
                <ListGroupItem onClick={(ev) => this.props.goToProfile(ev, memberID, document.querySelector('.kick-button'), document.querySelector('.rate-button'))} key={memberID} className="d-flex justify-content-between align-items-center" action> 
                  {this.props.currentGroup.groupMemberIDs[memberID]}
                  {this.isOwner(this.props.currentGroup) && <Button type="button" className="kick-button" size="lg" onClick={() => this.kickUser(this.props.currentGroup, memberID)}>Kick</Button>}
                  {this.canRate(this.props.accountID, memberID) && <Button type="button" className="rate-button" size="lg" onClick={() => this.getRateForm(this.props.currentGroup, memberID)}>Rate</Button>}
                </ListGroupItem>
              )
            }
            return 
          })}
      </ListGroup>
    )
  }

  renderGroupsList = () => {
    const { groups } = this.props
    return (
      <ListGroup>
          {groups 
          && Object.keys(groups).map((gid, i) => {
            return <Group
                changeGroup={this.changeGroup}
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
    const isPublic = this.props.currentGroup ? this.props.currentGroup.public : null
    const minRep = this.props.currentGroup ? this.props.currentGroup.reputationReq : null
    const reputation = this.props.profile ? this.props.profile.reputation : null
    const skills = this.props.currentGroup ? this.props.currentGroup.skillsReq : null
    const proximity = this.props.currentGroup ? this.props.currentGroup.proximityReq : null
    const rateForm = this.props.rateForm
    
    return (
        <Container fluid className="Groups h-100">
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
            <Chat group={this.props.currentGroup} handleNotification={this.props.handleNotification}/>
          </Col>
        </Row>
        { this.props.currentGroup &&
          <div>
          <Popover placement="left" isOpen={this.state.membersPopOver} target="PopoverM" toggle={this.toggleM}>
              <PopoverHeader>Group Members</PopoverHeader>
              <PopoverBody>
                {this.renderMemberList()}
              </PopoverBody>
          </Popover>

          <Popover placement="left" isOpen={this.state.settingsPopOver} target="PopoverS" toggle={this.toggleS}>
              <PopoverHeader>Settings</PopoverHeader>
              <PopoverBody>
              <ButtonGroup vertical>
                {this.isOwner(this.props.currentGroup) && <Button color="primary" type="button" size="lg" onClick={() => this.props.allowRating(this.props.currentGroup.groupID)}>Allow Rating</Button>}
                {this.isOwner(this.props.currentGroup) && <Button color="primary" type="button" size="lg" onClick={this.toggle}>Update Settings</Button>}
                {this.isOwner(this.props.currentGroup) && <Button color="primary" type="button" size="lg" onClick={this.inviteAll}>Invite Eligible Members</Button>}
                {this.isOwner(this.props.currentGroup) && <Button color="primary" type="button" size="lg" onClick={this.toggleE}>Create An Event</Button>}
                <Button color="danger" type="button" size="lg" onClick={this.leaveGroup}>Leave Group</Button>
                {this.isOwner(this.props.currentGroup) && <Button color="danger" type="button" size="lg" onClick={this.disbandGroup}>Disband Group</Button>}
              </ButtonGroup>
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
                <Input type="number" name="proximity" id="proximity" min={0} max={12450} defaultValue={proximity} />
              </FormGroup>     
              <FormGroup>
                <Label check>
                  <Toggle defaultChecked={isPublic} name="isPublic" />
                  <span> Make Group Public </span>
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

        {/* TODO: add name to modal */}
        <Modal isOpen={this.state.modalR} toggle={this.toggleR} className="update-settings-modal">
          <ModalHeader toggle={this.toggleR}>Rate User</ModalHeader>
          <ModalBody>
            <Form className="rate-group-form" id="rate-form" onSubmit={this.sendRating}>
              <FormGroup>
                <Label check>
                  {/*<Input type="checkbox" name="endorse"/>{' '}Boost Reputation?*/}
                  <Toggle defaultChecked={false} name="endorse"/>
                  <span> Boost Reputation?</span>
                </Label>
              </FormGroup>
              {this.props.rateForm && Object.keys(this.props.rateForm).map((key, i) => {
                return (
                  <FormGroup key={key} className="required">
                    <Label for="reputation">{key}</Label>
                    <Input type="number" name={`skill${i}`} id="repuation" min={0} max={10} defaultValue={0}/>
                  </FormGroup>
                )
              })}
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" type="button" onClick={() => this.sendRating({ target: document.querySelector('#rate-form')})}>Submit</Button>{' '}
            <Button color="secondary" onClick={this.toggleR}>Cancel</Button>
          </ModalFooter>
        </Modal>

        <Modal isOpen={this.state.modalE} toggle={this.toggleE} className="create-event-modal">
          <ModalHeader toggle={this.toggleE}>Create a Group Event</ModalHeader>
          <ModalBody>
            <Form className="create-event-form" id="event-form" onSubmit={this.createEvent}>
              <FormGroup className="required">
                <Label for="name">Event Name</Label>
                <Input required type="text" name="name" id="name" />
              </FormGroup>
              <FormGroup>
              <Label for="purpose">Purpose</Label>
              <Input type="textarea" name="purpose" id="purpose"/>
            </FormGroup>
              <FormGroup className="required">
                <Label for="proximity">Maximum Proximity (Miles)</Label>
                <Input required type="number" name="proximity" id="proximity" min={0} max={12450} defaultValue={0} />
              </FormGroup>     
            </Form>
            <div className="skills-form">
              <Label for="skills">Preferred Skills</Label>
              <SkillsForm id="skills" autoFocus={false} />
            </div> 
          </ModalBody>
          <ModalFooter>
            <Button color="primary" type="button" onClick={() => this.createEvent({ target: document.querySelector('#event-form')})}>Create Event</Button>{' '}
            <Button color="secondary" onClick={this.toggleE}>Cancel</Button>
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
    accountID: state.user ? state.user.accountID : null,
    rateForm: state.groups ? state.groups.getRateFormData : null,
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
    kick: (url, headers, extra) => dispatch(kick(url, headers, null, extra)),
    getRateForm: (url, headers) => dispatch(getRateForm(url, headers)),
    postRateForm: (url, headers, body) => dispatch(postRateForm(url, headers, body)),
    inviteAll: (url, headers) => dispatch(inviteAll(url, headers)),
    createEvent: (url, headers, body) => dispatch(createEvent(url, headers, body)),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Groups)