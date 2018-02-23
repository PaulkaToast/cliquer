import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Button } from 'reactstrap'

//import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'
import { getSkills, removeSkill } from '../../redux/actions'

class Profile extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {
      this.props.getSkills(`https://10.0.0.222:17922/api/getSkills?username=${nextProps.uid}`, { 'X-Authorization-Firebase': nextProps.token})
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
        <Button type="button" color="primary" size="lg" onClick={() => this.removeSkill('Verilog')}>Remove Verilog</Button>
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
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)

