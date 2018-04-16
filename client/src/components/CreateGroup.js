import React, { Component } from 'react'
import { Button, Form, FormGroup, Label, Input } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/CreateGroup.css'
import SkillsForm from './Profile/SkillsForm'
import { addSkills, clearNewSkills, createGroup, getProfile } from '../redux/actions'
import url from '../server.js'

class CreateGroup extends Component {

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {    
      const uid = nextProps.uid 
      // Get profile data
      if(!nextProps.profile && !nextProps.profileIsLoading) {
        this.props.getProfile(`${url}/api/getProfile?username=${uid}&type=user`, { 'X-Authorization-Firebase': nextProps.token})
      }
    }
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    const skillsReq = this.props.newSkills
    const groupName = ev.target.name.value
    const groupPurpose = ev.target.purpose.value
    const reputationReq = ev.target.reputation.value
    const proximityReq = ev.target.proximity.value

    this.props.clearSkills()
    ev.target.reset()

    this.props.createGroup(`${url}/api/createGroup?username=${this.props.user.uid}`, { 'X-Authorization-Firebase': this.props.token}, 
                          JSON.stringify({
                            groupName,
                            groupPurpose,
                            isPublic: false,
                            reputationReq,
                            proximityReq,
                            skillsReq
                          }))
    window.location="/groups"
  }
  
  render() {
    const reputation = this.props.profile ? this.props.profile.reputation : 0
    return (
      <div className="CreateGroup">
        <h2>Create A Group</h2>
        <Form className="create-group-form" id="form" onSubmit={this.handleSubmit}>
          <FormGroup className="required">
            <Label for="name">Group Name</Label>
            <Input autoFocus required type="text" name="name" id="name" />
          </FormGroup>
          <FormGroup>
          <Label for="purpose">Purpose</Label>
          <Input type="textarea" name="purpose" id="purpose" />
        </FormGroup>
          <FormGroup className="required">
            <Label for="reputation">Minimum Reputation</Label>
            <Input required type="number" name="reputation" id="reputation" min={0} max={reputation} defaultValue={0} />
          </FormGroup>
          <FormGroup className="required">
            <Label for="reputation">Maximum Proximity (Miles)</Label>
            <Input required type="number" name="proximity" id="proximity" min={0} defaultValue={10} />
          </FormGroup>
        </Form>
        <div className="skills-form">
          <Label for="skills">Preferred Skills</Label>
          <SkillsForm id="skills" autoFocus={false}/>
        </div>
       
        <Button className="create-group-submit-button" color="success" type="submit" onSubmit={this.handleSubmit} form="form" block>Submit</Button>
    
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    newSkills: state.user.newSkills,
    user: state.user.data,
    token: state.auth.token,
    profileIsLoading: state.profile && state.profile.getIsLoading ? state.profile.getIsLoading : null,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    clearSkills: () => dispatch(clearNewSkills()),
    addSkills: (skills) => dispatch(addSkills(skills)),
    createGroup: (url, header, body) => dispatch(createGroup(url, header, body)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateGroup)

