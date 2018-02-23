import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, removeSkill, getProfile, fetchData } from '../../redux/actions'

class Profile extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {
      this.props.getSkills(`https://localhost:17922/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token})
     
      const type = nextProps.ownerUID === nextProps.uid ? 'user' : 'public'
      const uid = nextProps.uid //TODO: remove this later and just use ownerid
      this.props.fetchData('https://localhost:17922/api/getSkillList', { 'X-Authorization-Firebase': nextProps.token})
      this.props.getProfile(`https://localhost:17922/api/getProfile?identifier=${uid}&type=${type}`, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  removeSkill = (skill) => {
    if(this.props.uid && this.props.token) {
      this.props.removeSkill(`https://localhost:17922/api/removeSkill?username=${this.props.uid}&name=${skill}`, { 'X-Authorization-Firebase': this.props.token})
    } else {
      console.log('UID or Token does not exist!')
    }
  }

  render() {
    return (
      <div className="Profile">
        <NotificationPanel />
       
        <UserInfo />

        <div class="navbar">
              <a href="#">Link</a>
              <a href="#">Link</a>
              <a href="#">Link</a>
              <a href="#" class="right">Link</a>
          </div>
                    

         <div className="header">
             <h1>My Profile</h1>
                 <p>A website created by me.</p>  
          </div>

     

          <div className="row">
            <div className="side">...</div>
            <div className="main">...</div>
          </div>

          <div className="skills">
            <div>
               <p>
                    <SkillsPanel />   
               </p>
            </div>
          </div>


          <footer className="footer">
           <p>Find me on social media.</p>
              <i className="fa fa-facebook-official w3-hover-opacity"></i>
              <i className="fa fa-instagram w3-hover-opacity"></i>
              <i className="fa fa-snapchat w3-hover-opacity"></i>
              <i className="fa fa-pinterest-p w3-hover-opacity"></i>
              <i className="fa fa-twitter w3-hover-opacity"></i>
              <i className="fa fa-linkedin w3-hover-opacity"></i>
          </footer>
      </div>

    )
  }
}
function getScrollPercent() {
  return (
    (document.documentElement.scrollTop || document.body.scrollTop) 
    / ( (document.documentElement.scrollHeight || document.body.scrollHeight) 
    - document.documentElement.clientHeight) 
    * 100);
}


const mapStateToProps = (state) => {
	return {
    uid: state.user.data ? state.user.data.uid : null,
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    getSkills: (url, headers) => dispatch(getSkills(url, headers)),
    removeSkill: (url, headers) => dispatch(removeSkill(url, headers)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    fetchData: (url, headers) => dispatch(fetchData(url, headers)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

