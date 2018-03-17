import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'

import '../../css/SkillsPanel.css'
import SkillsForm from './SkillsForm'
import { addSkills, clearNewSkills, postSkill } from '../../redux/actions'
import url from '../../server.js'

class SkillsPanel extends Component {

  constructor(props) {
    super(props)
    this.state = {
      modal: false,
    }
  }
  
  addSkills = () => {
    this.props.addSkills(this.props.newSkills)
    this.props.newSkills.forEach(skill => {
      this.props.postSkill(`${url}/api/addSkill?username=${this.props.uid}&name=${skill}&level=0`, { 'X-Authorization-Firebase': this.props.token})
    })
    this.toggle()
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
        <Button color="primary" onClick={this.toggle}>Add skills</Button>
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="add-skills-modal">
          <ModalHeader toggle={this.toggle}>Add Skills</ModalHeader>
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
    addSkills: (skills) => dispatch(addSkills(skills)),
    postSkill: (url, headers) => dispatch(postSkill(url, headers)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SkillsPanel)
