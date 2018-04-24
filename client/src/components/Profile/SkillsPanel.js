import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Button, Modal, ModalHeader, ModalBody, ModalFooter,
        ListGroup, ListGroupItem, Badge } from 'reactstrap'

import '../../css/SkillsPanel.css'
import SkillsForm from './SkillsForm'
import { clearNewSkills, postSkills, removeSkill } from '../../redux/actions'
import url from '../../server.js'

class SkillsPanel extends Component {

  constructor(props) {
    super(props)
    this.state = {
      modal: false,
      skills: []
    }
  }

  componentDidMount = () => {
    this.setState({skills: this.props.skills})
  }

  componentWillReceiveProps = (nextProps) => {
    if (nextProps !== this.props){
      this.setState({skills: nextProps.skills})
    }
  }

  addSkills = () => {
    this.props.postSkills(`${url}/api/addSkills?username=${this.props.uid}`, { 'X-Authorization-Firebase': this.props.token }, JSON.stringify(this.props.newSkills ? this.props.newSkills : []))
    var newState = this.state
    newState.skills.push({skillLevel: 0, skillName: this.props.newSkills})
    this.toggle()
  }

  removeSkill = (skill, index) => {
    if(this.props.uid && this.props.token) {
      this.props.removeSkill(`${url}/api/removeSkill?username=${this.props.uid}&name=${skill}`, { 'X-Authorization-Firebase': this.props.token}, null, index)
    }
  }

  toggle = (setState) => {
    if(this.state.modal) {
      this.props.clearSkills()
      this.setState({ modal: false })
    } else {
      this.setState({ modal: true })
    }
  }

  render() {
    return (
      <div className="SkillsPanel">
        <ListGroup>
        { this.props.skills && this.props.skills.map((skill, i) => {
          return <div key={skill.skillName} >
          <ListGroupItem className="justify-content-between">
            {skill.skillName + " "}
            <Badge pill>{skill.skillLevel} </Badge>
            <Button className="skill-cancel-button" onClick={() => {this.removeSkill(skill.skillName, i)}} color="link">x</Button>
          </ListGroupItem>
          </div>
        })}
        </ListGroup>
        <div className="add-skills-button">
          {this.props.isOwner && <Button color="primary" onClick={this.toggle}>Add skills</Button>}
        </div>
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="add-skills-modal">
          <ModalHeader toggle={this.toggle}>Add Skills and Interests</ModalHeader>
          <ModalBody>
            <SkillsForm toggle={this.toggle} addSkills={this.addSkills} autoFocus={true}/>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={this.addSkills}>Add Skills</Button>{' '}
            <Button color="secondary" onClick={this.toggle}>Cancel</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    newSkills: state.user.newSkills,
    uid: state.user.data ? state.user.data.uid : null,
    token: state.auth.token
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    clearSkills: () => dispatch(clearNewSkills()),
    postSkills: (url, headers, body) => dispatch(postSkills(url, headers, body)),
    removeSkill: (url, headers, body, index) => dispatch(removeSkill(url, headers, body, index)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SkillsPanel)
