import React, { Component } from 'react'
import { connect } from 'react-redux'

//import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, removeSkill, getProfile } from '../../redux/actions'

class Profile extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {
      this.props.getSkills(`https://10.0.0.222:17922/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token})
     
      const type = nextProps.ownerUID === nextProps.uid ? 'user' : 'public'
      const uid = nextProps.ownerUID ? nextProps.ownerUID : nextProps.uid //TODO: remove this later and just use ownerid
      this.props.getProfile(`https://10.0.0.222:17922/api/getProfile?identifier=${uid}&type=${type}`, { 'X-Authorization-Firebase': nextProps.token})
    }
  }

  removeSkill = (skill) => {
    if(this.props.uid && this.props.token) {
      this.props.removeSkill(`https://10.0.0.222:17922/api/removeSkill?username=${this.props.uid}&name=${skill}`, { 'X-Authorization-Firebase': this.props.token})
    } else {
      console.log('UID or Token does not exist!')
    }
  }

  render() {
    return (
      <div className="Profile">
        <SkillsPanel />
        <NotificationPanel notifications={[{messageID: 111, type: 0}]}/>
      </div>
    )
  }
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
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

