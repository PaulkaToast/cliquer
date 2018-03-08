import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, removeSkill, getProfile, getSkillsList } from '../../redux/actions'

class Profile extends Component {

  componentWillReceiveProps = (nextProps) => {
    if (nextProps.uid && nextProps.token) {
      this.props.getSkills(`https://localhost:17922/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token })

      const type = nextProps.ownerUID === nextProps.uid ? 'user' : 'public'
      const uid = nextProps.uid //TODO: remove this later and just use ownerid
      this.props.getSkillsList('https://localhost:17922/api/getSkillList', { 'X-Authorization-Firebase': nextProps.token })
      this.props.getProfile(`https://localhost:17922/api/getProfile?identifier=${uid}&type=${type}`, { 'X-Authorization-Firebase': nextProps.token })
    }
  }

  removeSkill = (skill) => {
    if (this.props.uid && this.props.token) {
      this.props.removeSkill(`https://localhost:17922/api/removeSkill?username=${this.props.uid}&name=${skill}`, { 'X-Authorization-Firebase': this.props.token })
    } else {
      console.log('UID or Token does not exist!')
    }
  }

  //  <NotificationPanel />

  //   <UserInfo />


  render() {
    return (
      <div className="Profile">
        <div className="header">
          <h1>Until I learn redux</h1>
          <p>A website created by me!</p>
        </div>
        <div className="User">
          <h1> How do i get the current user info from redux </h1>
        </div>
        <div className="Friends">
          <h1> How do i get the current user info from redux </h1>
        </div>
        <div className="skillRatings">
          <h1> How do I get the current user info from redux </h1>
        </div>
        <div className="sidebar">
          <p> This is where our skills can go. </p>
          <div className="skills">
            <SkillsPanel />
          </div>
        </div>
        <div className="">
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
    getSkillsList: (url, headers) => dispatch(getSkillsList(url, headers)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

