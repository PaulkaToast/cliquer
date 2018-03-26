import React, { Component } from 'react'
import { connect } from 'react-redux'
import { TabContent, TabPane, Nav, NavItem, NavLink, 
  Card, Button, CardTitle, CardText, Row, Col } from 'reactstrap';
  import classnames from 'classnames';

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, getProfile } from '../../redux/actions'
import url from '../../server.js'
import nFlag from '../../img/newUser.png'

class Profile extends Component {
  constructor(props) {
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
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {    
      const type = nextProps.ownerUID === nextProps.uid ? 'user' : 'public'
      const uid = nextProps.uid //TODO: remove this later and just use ownerid

      // Get profile data
      if(!nextProps.profile && !nextProps.profileIsLoading) {
        this.props.getProfile(`${url}/api/getProfile?username=${uid}&type=${type}`, { 'X-Authorization-Firebase': nextProps.token})
      }

      // Get skills data
      if(this.props.postData !== nextProps.postData || (!nextProps.skills && !nextProps.skillsIsLoading)) {
        this.props.getSkills(`${url}/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token})
      }
    }
  }

  
  render() {
    const { user, profile, skills } = this.props
    if(!profile){
      return (
        <div>
          is loading
        </div>
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
        </TabContent>
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
    token: state.auth.token
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getSkills: (url, headers) => dispatch(getSkills(url, headers)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

