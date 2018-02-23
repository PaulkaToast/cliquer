import React, { Component } from 'react'
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';
import { connect } from 'react-redux'

import '../css/CreateGroup.css'
import SkillsForm from './Profile/SkillsForm'
import { addSkills, clearNewSkills, createGroup } from '../redux/actions'

class CreateGroup extends Component {

  handleSubmit = (ev) => {
    ev.preventDefault()
    const preferredSkills = this.props.newSkills
    const groupName = ev.target.name.value
    const purpose = ev.target.purpose.value
    const reputation = ev.target.reputation.value

    this.props.clearSkills()
    ev.target.reset()

    this.props.createGroup(`https://10.0.0.222:17922/api/createGroup?username=${this.props.user.uid}&bio=${purpose}&groupName=${groupName}`, { 'X-Authorization-Firebase': this.props.token})
  }
  
  render() {
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
            <Input required type="number" name="reputation" id="repuation" min={0} defaultValue={0} />
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
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    clearSkills: () => dispatch(clearNewSkills()),
    addSkills: (skills) => dispatch(addSkills(skills)),
    createGroup: (url, header) => dispatch(createGroup(url, header))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateGroup)

