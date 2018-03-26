import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, getProfile } from '../../redux/actions'
import url from '../../server.js'

class Profile extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.accountID && nextProps.token) {    
      const ownerID = nextProps.match.params.ownerID
      const type = ownerID === nextProps.accountID ? 'user' : 'public'

      // Get profile data
      if(!nextProps.profile && !nextProps.profileIsLoading) {
        this.props.getProfile(`${url}/api/getProfile?userId=${ownerID}&type=${type}`, { 'X-Authorization-Firebase': nextProps.token})
      }

      // Get skills data
      if(this.props.postData !== nextProps.postData || (!nextProps.skills && !nextProps.skillsIsLoading)) {
        this.props.getSkills(`${url}/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token})
      }
    }
  }

  isOwner = (accountID) => {
    return accountID === this.props.accountID
  } 
  
  render() {
    const { user, profile, skills } = this.props
    const ownerID = this.props.match.params.ownerID
    
    return (
      <div>
        <SkillsPanel skills={skills}/>
        {/* Page Container */}
        <div className="pk-container pk-content" style={{maxWidth: 1400, marginTop: 80}}>    
          {/* The Grid */}
          <div className="pk-row">
            {/* Left Column */}
            <div className="pk-col m3">
              {/* Profile */}
              <div className="pk-card-2 pk-round pk-white">
                <div className="pk-container">
                  <h4 className="pk-center">My Profile</h4>
                  <p className="pk-center"><img src="img_avatar3.png" className="pk-circle" style={{height: 106, width: 106}} alt="Avatar" /></p>
                  <hr />
                  <p><i className="fa fa-pencil fa-fw pk-margin-right pk-text-theme" /> Designer, UI</p>
                  <p><i className="fa fa-home fa-fw pk-margin-right pk-text-theme" /> London, UK</p>
                  <p><i className="fa fa-birthday-cake fa-fw pk-margin-right pk-text-theme" /> April 1, 1988</p>
                </div>
              </div>
              <br />
              {/* Accordion */}
              <div className="pk-card-2 pk-round">
                <div className="pk-accordion pk-white">
                  <button className="pk-btn-block pk-theme-l1 pk-left-align"><i className="fa fa-circle-o-notch fa-fw pk-margin-right" /> My Groups</button>
                  <div id="Demo1" className="pk-accordion-content pk-container">
                    <p>Some text..</p>
                  </div>
                  <button className="pk-btn-block pk-theme-l1 pk-left-align"><i className="fa fa-calendar-check-o fa-fw pk-margin-right" /> My Events</button>
                  <div id="Demo2" className="pk-accordion-content pk-container">
                    <p>Some other text..</p>
                  </div>
                  <button className="pk-btn-block pk-theme-l1 pk-left-align"><i className="fa fa-users fa-fw pk-margin-right" /> My Photos</button>
                  <div id="Demo3" className="pk-accordion-content pk-container">
                    <div className="pk-row-padding">
                      <br />
                      <div className="pk-half">
                        <img src="img_lights.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                      <div className="pk-half">
                        <img src="img_nature.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                      <div className="pk-half">
                        <img src="img_mountains.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                      <div className="pk-half">
                        <img src="img_forest.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                      <div className="pk-half">
                        <img src="img_nature.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                      <div className="pk-half">
                        <img src="img_fjords.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                      </div>
                    </div>
                  </div>
                </div>      
              </div>
              <br />
              {/* Interests */} 
              <div className="pk-card-2 pk-round pk-white pk-hide-small">
                <div className="pk-container">
                  <p>Interests</p>
                  <p>
                    <span className="pk-tag pk-small pk-theme-d5">News</span>
                    <span className="pk-tag pk-small pk-theme-d4">pkSchools</span>
                    <span className="pk-tag pk-small pk-theme-d3">Labels</span>
                    <span className="pk-tag pk-small pk-theme-d2">Games</span>
                    <span className="pk-tag pk-small pk-theme-d1">Friends</span>
                    <span className="pk-tag pk-small pk-theme">Games</span>
                    <span className="pk-tag pk-small pk-theme-l1">Friends</span>
                    <span className="pk-tag pk-small pk-theme-l2">Food</span>
                    <span className="pk-tag pk-small pk-theme-l3">Design</span>
                    <span className="pk-tag pk-small pk-theme-l4">Art</span>
                    <span className="pk-tag pk-small pk-theme-l5">Photos</span>
                  </p>
                </div>
              </div>
              <br />
              {/* Alert Box */}
              <div className="pk-container pk-round pk-theme-l4 pk-border pk-theme-border pk-margin-bottom pk-hide-small">
                <span className="pk-hover-text-grey pk-closebtn">
                  <i className="fa fa-remove" />
                </span>
                <p><strong>Hey!</strong></p>
                <p>People are looking at your profile. Find out who.</p>
              </div>
              {/* End Left Column */}
            </div>
            {/* Middle Column */}
            <div className="pk-col m7">
              <div className="pk-row-padding">
                <div className="pk-col m12">
                  <div className="pk-card-2 pk-round pk-white">
                    <div className="pk-container pk-padding">Social 
                      <h6 className="pk-opacity">comd sail away p.css</h6>
                      <p className="pk-border pk-padding">Status: Feeling Blue</p>
                      <button type="button" className="pk-btn pk-theme"><i className="fa fa-pencil" /> &nbsp;Post</button> 
                    </div>
                  </div>
                </div>
              </div>
              <div className="pk-container pk-card-2 pk-white pk-round pk-margin"><br />
                <img src="img_avatar2.png" alt="Avatar" className="pk-left pk-circle pk-margin-right" style={{width: 60}} />
                <span className="pk-right pk-opacity">1 min</span>
                <h4>John Doe</h4><br />
                <hr className="pk-clear" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
                <div className="pk-row-padding" style={{margin: '0 -16px'}}>
                  <div className="pk-half">
                    <img src="img_lights.jpg" style={{width: '100%'}} alt="Northern Lights" className="pk-margin-bottom" />
                  </div>
                  <div className="pk-half">
                    <img src="img_nature.jpg" style={{width: '100%'}} alt="Nature" className="pk-margin-bottom" />
                  </div>
                </div>
                <button type="button" className="pk-btn pk-theme-d1 pk-margin-bottom"><i className="fa fa-thumbs-up" /> &nbsp;Like</button> 
                <button type="button" className="pk-btn pk-theme-d2 pk-margin-bottom"><i className="fa fa-comment" /> &nbsp;Comment</button> 
              </div>
              <div className="pk-container pk-card-2 pk-white pk-round pk-margin"><br />
                <img src="img_avatar5.png" alt="Avatar" className="pk-left pk-circle pk-margin-right" style={{width: 60}} />
                <span className="pk-right pk-opacity">16 min</span>
                <h4>Jane Doe</h4><br />
                <hr className="pk-clear" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
                <button type="button" className="pk-btn pk-theme-d1 pk-margin-bottom"><i className="fa fa-thumbs-up" /> &nbsp;Like</button> 
                <button type="button" className="pk-btn pk-theme-d2 pk-margin-bottom"><i className="fa fa-comment" /> &nbsp;Comment</button> 
              </div>  
              <div className="pk-container pk-card-2 pk-white pk-round pk-margin"><br />
                <img src="img_avatar6.png" alt="Avatar" className="pk-left pk-circle pk-margin-right" style={{width: 60}} />
                <span className="pk-right pk-opacity">32 min</span>
                <h4>Angie Jane</h4><br />
                <hr className="pk-clear" />
                <p>Have you seen this?</p>
                <img src="img_nature.jpg" style={{width: '100%'}} className="pk-margin-bottom" />
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
                <button type="button" className="pk-btn pk-theme-d1 pk-margin-bottom"><i className="fa fa-thumbs-up" /> &nbsp;Like</button> 
                <button type="button" className="pk-btn pk-theme-d2 pk-margin-bottom"><i className="fa fa-comment" /> &nbsp;Comment</button> 
              </div> 
              {/* End Middle Column */}
            </div>
            {/* Right Column */}
            <div className="pk-col m2">
              <div className="pk-card-2 pk-round pk-white pk-center">
                <div className="pk-container">
                  <p>Upcoming Events:</p>
                  <img src="img_forest.jpg" alt="Forest" style={{width: '100%'}} />
                  <p><strong>Holiday</strong></p>
                  <p>Friday 15:00</p>
                  <p><button className="pk-btn pk-btn-block pk-theme-l4">Info</button></p>
                </div>
              </div>
              <br />
              <div className="pk-card-2 pk-round pk-white pk-center">
                <div className="pk-container">
                  <p>Friend Request</p>
                  <img src="img_avatar6.png" alt="Avatar" style={{width: '50%'}} /><br />
                  <span>Jane Doe</span>
                  <div className="pk-row pk-opacity">
                    <div className="pk-half">
                      <button className="pk-btn pk-green pk-btn-block pk-section" title="Accept"><i className="fa fa-check" /></button>
                    </div>
                    <div className="pk-half">
                      <button className="pk-btn pk-red pk-btn-block pk-section" title="Decline"><i className="fa fa-remove" /></button>
                    </div>
                  </div>
                </div>
              </div>
              <br />
              <div className="pk-card-2 pk-round pk-white pk-padding-hor-16 pk-center">
                <p>ADS</p>
              </div>
              <br />
              <div className="pk-card-2 pk-round pk-white pk-padding-hor-32 pk-center">
                <p><i className="fa fa-bug pk-xxlarge" /></p>
              </div>
              {/* End Right Column */}
            </div>
            {/* End Grid */}
          </div>
          {/* End Page Container */}
        </div>
        <br />
        {/* Footer */}
        <footer className="pk-container pk-theme-d3 pk-padding-hor-16">
          <h5>Footer</h5>
        </footer>
        <footer className="pk-container pk-theme-d5">
          <p>Powered by <a href="default.asp.html" target="_blank">kevin's brain.css</a></p>
        </footer>
      </div>
    )
}
  
  //  <NotificationPanel />

  //   <UserInfo />

/*
  render() {
    
   const { user } = this.props
    return ( 
      <div className="Profile">
        <div className="header">
          <div className="User">
            <UserInfo user={user}/>
          </div> 
        </div>  
        <div className="Friends">
          <h1> How do i get the current user info from redux </h1>
        </div>
        <div className="sidebar">
            <div className="content">
          <NotificationPanel user={user} />
          </div>
        </div>
        <div className="skills">
          <p> This is where our skills can go. </p>
        </div>  
          <div className="footer">
            <p>Find me on social media.</p>
            <i className="fa fa-facebook-official pk-hover-opacity"></i>
            <i className="fa fa-instagram pk-hover-opacity"></i>
            <i className="fa fa-snapchat pk-hover-opacity"></i>
            <i className="fa fa-pinterest-p pk-hover-opacity"></i>
            <i className="fa fa-twitter pk-hover-opacity"></i>
            <i className="fa fa-linkedin pk-hover-opacity"></i>
        </div>
      </div>  
    )
  }  
}

*/
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

