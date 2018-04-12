import React, { Component } from 'react'
import { connect } from 'react-redux'
import { TabContent, TabPane, Nav, NavItem, NavLink, 
  Card, Button, CardTitle, CardText, Row, Col, ListGroup, ListGroupItem,
  Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import classnames from 'classnames';

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, getProfile, getGroups, flagUser } from '../../redux/actions'
import url from '../../server.js'
import nFlag from '../../img/newUser.png'

class Profile extends Component {
  constructor(props) {
    super(props)

    this.state = {
      activeTab: '1',
      modal: false,
      flagged: false,
    }
  }


  componentDidMount = () => {
    this.fetch(this.props)
  }

  componentWillReceiveProps = (nextProps) => {
    this.fetch(nextProps)
  }

  fetch = (props) => {
    if(props.uid && props.accountID && props.token) {    
      const ownerID = props.match.params.ownerID
      const type = ownerID === props.accountID ? 'user' : 'public'

      // Get profile data
      if((!props.profile && !props.profileIsLoading) || (props.profile && props.profile.accountID !== ownerID)) {
        this.props.getProfile(`${url}/api/getProfile?userId=${ownerID}&type=${type}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get skills data
      if(this.props.postData !== props.postData || (!props.skills && !props.skillsIsLoading)) {
        this.props.getSkills(`${url}/api/getSkills?userId=${ownerID}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get groups
      if(!props.groups && !this.isOwner(ownerID)) {
        this.props.getGroups(`${url}/api/getUserGroups?username=${props.uid}`, { 'X-Authorization-Firebase': props.token })
      }
    }
  }

  toggle = (tab) => {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      })
    }
  }

  toggleM = () => {
    if(this.state.modal) {
      this.setState({ modal: false })
    } else {
      this.setState({ modal: true })
    }
  }

  isOwner = (accountID) => {
    return accountID === this.props.accountID || !this.props.accountID
  } 

  inviteAndToggle = (gid, ownerID) => {
    this.props.inviteToGroup(gid, ownerID)
    this.toggleM()
  }

  flagUser = (ownerID) => {
    if(this.props.isMod()) {
      this.props.flagUser(`${url}/mod/flagUser?modId=${this.props.accountID}&userId=${ownerID}`, { 'X-Authorization-Firebase': this.props.token})
      this.setState({ flagged: true })
    }
  }

  renderGroupList = () => {
    const groups = this.props.groups
    const ownerID = this.props.match.params.ownerID
    //TODO: check if user already exists in group
    return (
      <ListGroup>
        {groups && Object.keys(groups).length > 0
        && Object.keys(groups).map((gid, i) => {
          return (<ListGroupItem>
            {groups[gid].groupName} <Button className="invite-to-group-button" type="button" size="lg" 
            onClick={() => this.inviteAndToggle(gid, ownerID)}>Invite</Button>
          </ListGroupItem>)
        })}
      </ListGroup>
    )  
  }
  
  render() {
    const { user, profile, skills, groups, token } = this.props
    const ownerID = this.props.match.params.ownerID

    if(!profile || profile.accountID !== ownerID){
      return (
        <div className="loader">Loading...</div>
      )
    }

    let flag = profile.newUser ? nFlag : "";
    return (
      <div>
        <Nav tabs>
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '1' })}
              onClick={() => { this.toggle('1'); }}
            >
              Profile
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '2' })}
              onClick={() => { this.toggle('2'); }}
            >
              Friends
            </NavLink>
          </NavItem>
          {this.isOwner(ownerID) && 
            <NavItem>
              <NavLink
                className={classnames({ active: this.state.activeTab === '2' })}
                onClick={() => { this.toggle('3'); }}
              >
                Notifications
              </NavLink>
            </NavItem>
          }
        </Nav>
        <TabContent activeTab={this.state.activeTab}>
          <TabPane className="profile-tab" tabId="1">
            <hr/>
            <div className="main-info">
              <div className="icon">
                <i className="fa fa-upload" style="font-size:10px"></i>
                <i className="fa fa-user-circle" style="font-size:32px"></i>
              </div>
              <h1>
                {profile.fullName}<img className="profile-user-flag" src={flag} alt=""></img>
              </h1>
            </div>
            <hr/>
            <h4>
              Reputation: {profile.reputation}
            </h4>
            <hr/>
            
            {!this.isOwner(ownerID) && <Button type="button" size="lg" onClick={() => this.props.sendFriendRequest(ownerID)}>Send Friend Request</Button>}
            {!this.isOwner(ownerID) && groups && Object.keys(groups).length > 0 && 
              <Button type="button" size="lg" onClick={this.toggleM}>Invite To Group</Button>}
            {!this.isOwner(ownerID) && this.props.isMod() && 
              <Button type="button" color="warning" size="lg" onClick={!this.state.flagged ? this.flagUser(ownerID) : null}>{!this.state.flagged ? 'Flag User' : 'Flagged'}</Button>}
            <hr/>
            <h4>
              Skills:
            </h4>
              <SkillsPanel skills={skills} isOwner={this.isOwner(ownerID)}/>
          </TabPane>
          <TabPane tabId="2">
            <h4>
              Friends:
            </h4>
            <ListGroup>
            {this.props.profile && this.props.profile.friendIDs && Object.keys(this.props.profile.friendIDs).map((key) =>
            { return <ListGroupItem onClick={() => this.props.goToProfile(key)} key={key}>
            {profile.friendIDs[key]}
            <Button className="friend-cancel-button" onClick={() => {}} color="link">x</Button>
            </ListGroupItem>})}
            </ListGroup>
          </TabPane>
          <TabPane tabId="3">
            <NotificationPanel deleteNotification={this.props.deleteNotification} />
          </TabPane>
        </TabContent>

        <Modal isOpen={this.state.modal} toggle={this.toggleM} className="update-settings-modal">
          <ModalHeader toggle={this.toggleM}>Invite To Group</ModalHeader>
          <ModalBody>
            {this.renderGroupList()}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggleM}>Close</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  } 
}

const mapStateToProps = (state) => {
  return {
    user: state.user && state.user.data ? state.user.data : null,
    uid: state.user && state.user.data ? state.user.data.uid : null,
    profileIsLoading: state.profile && state.profile.getIsLoading ? state.profile.getIsLoading : null,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
    skills: state.skills && state.skills.getData ? state.skills.getData : null,
    skillsIsLoading: state.skills && state.skills.getIsLoading ? state.skills.getIsLoading : null,
    postData: state.skills && state.skills.postData ? state.skills.postData : null,
    accountID: state.user.accountID,
    groups: state.groups ? state.groups.getGroupsData : [],
    token: state.auth.token,
    notifications: state.messages.data
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getSkills: (url, headers) => dispatch(getSkills(url, headers)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    getGroups: (url, headers) => dispatch(getGroups(url, headers)),
    flagUser: (url, headers) => dispatch(flagUser(url, headers)),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

