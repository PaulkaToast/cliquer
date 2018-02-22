import React, { Component } from 'react'
import { Button, Form, FormGroup, Label, Input } from 'reactstrap';

import '../css/CreateGroup.css'
import SkillsForm from './Profile/SkillsForm'

class CreateGroup extends Component {

  handleSubmit = (ev) => {
    ev.preventDefault()
    console.log(ev)
  }
  
  render() {
    return (
      <div className="CreateGroup">
        <Form className="create-group-form" id="form" onSubmit={this.handleSubmit}>
          <FormGroup className="required">
            <Label for="name">Group Name</Label>
            <Input required type="text" name="name" id="name" />
          </FormGroup>
          <FormGroup>
            <Label for="purpose">Purpose</Label>
            <Input type="text" name="purpose" id="purpose" />
          </FormGroup>
          <FormGroup className="required">
            <Label for="reputation">Minimum Reputation</Label>
            <Input required type="number" name="reputation" id="repuation" min={0} defaultValue={0} />
          </FormGroup>
        </Form>
        <Label for="skills">Preferred Skills</Label>
        <SkillsForm id="skills" />
        <Button type="submit" onSubmit={this.handleSubmit} form="form">Submit</Button>
      </div>
    )
  }
}

export default CreateGroup
