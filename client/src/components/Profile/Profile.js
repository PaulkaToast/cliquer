import React, { Component } from 'react'
import { connect } from 'react-redux'
<<<<<<< HEAD
import { TabContent, TabPane, Nav, NavItem, NavLink, 
  Card, Button, CardTitle, CardText, Row, Col } from 'reactstrap';
  import classnames from 'classnames';
=======
import { Button, ListGroup, ListGroupItem,
         Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
>>>>>>> feature/groupServices

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, getProfile, getGroups } from '../../redux/actions'
import url from '../../server.js'
import nFlag from '../../img/newUser.png'

class Profile extends Component {
  constructor(props) {
<<<<<<< HEAD
    super(props);

    this.toggle = this.toggle.bind(this);
    this.state = {
      activeTab: '1'
    };
  }

  toggle(tab) {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      });
    }
=======
    super(props)
    this.state = {
      modal: false,
    }
  }

  componentDidMount = () => {
    this.fetch(this.props)
>>>>>>> feature/groupServices
  }

  componentWillReceiveProps = (nextProps) => {
    this.fetch(nextProps)
  }

  fetch = (props) => {
    if(props.uid && props.accountID && props.token) {    
      const ownerID = props.match.params.ownerID
      const type = ownerID === props.accountID ? 'user' : 'public'

      // Get profile data
      if(!props.profile && !props.profileIsLoading) {
        this.props.getProfile(`${url}/api/getProfile?userId=${ownerID}&type=${type}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get skills data
      if(this.props.postData !== props.postData || (!props.skills && !props.skillsIsLoading)) {
        this.props.getSkills(`${url}/api/getSkills?username=${props.uid}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get groups
      if(!props.groups && !this.isOwner(ownerID)) {
        this.props.getGroups(`${url}/api/getUserGroups?username=${props.uid}`, { 'X-Authorization-Firebase': props.token })
      }
    }
  }

<<<<<<< HEAD
  
  render() {
    const { user, profile, skills } = this.props
    if(!profile){
      return (
        <div class="loader">Loading...</div>
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
              My Profile
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '2' })}
              onClick={() => { this.toggle('2'); }}
            >
              My Friends
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '2' })}
              onClick={() => { this.toggle('3'); }}
            >
              Notifications
            </NavLink>
          </NavItem>
        </Nav>
        <TabContent activeTab={this.state.activeTab}>
          <TabPane className="profile-tab" tabId="1">
            <hr/>
            <h1>
              {profile.fullName}<img className="profile-user-flag" src={flag} alt=""></img>
            </h1>
            <hr/>
            <h4>
              Reputation: {profile.adjustedReputation}
            </h4>
            <hr/>
            <h4>
              Skills:
            </h4>

            
              <SkillsPanel skills={skills}/>
          </TabPane>
          <TabPane tabId="2">
          </TabPane>
          <TabPane tabId="3">
          </TabPane>
        </TabContent>
      </div>
    )
}
  
 
=======
  isOwner = (accountID) => {
    return accountID === this.props.accountID || !this.props.accountID
  } 


  toggle = () => {
    if(this.state.modal) {
      this.setState({ modal: false })
    } else {
      this.setState({ modal: true })
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
            {groups[gid].groupName} <Button type="button" size="lg" onClick={() => this.props.inviteToGroup(gid, ownerID)}>Invite</Button>
          </ListGroupItem>)
        })}
      </ListGroup>
    )  
  }
  
  render() {
    const { user, profile, skills, groups } = this.props
    const ownerID = this.props.match.params.ownerID
    
    return (
      <div>
        <SkillsPanel skills={skills}/>
        {!this.isOwner(ownerID) && <Button type="button" size="lg" onClick={() => this.props.sendFriendRequest(ownerID)}>Send Friend Request</Button>}
        {!this.isOwner(ownerID) && groups && Object.keys(groups).length > 0 && 
          <Button type="button" size="lg" onClick={this.toggle}>Invite To Group</Button>}
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="update-settings-modal">
          <ModalHeader toggle={this.toggle}>Invite To Group</ModalHeader>
          <ModalBody>
            {this.renderGroupList()}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggle}>Close</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  }
>>>>>>> feature/groupServices
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
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

