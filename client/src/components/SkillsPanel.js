import React, { Component } from 'react'
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/SkillsPanel.css'
import { addSkills } from '../redux/actions'

class SkillsPanel extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modal: false,
      newSkills: [],
      animation: '',
      listClass: '',
    }
  }

  toggle = () => {
    if(this.state.modal) {
      this.setState({ newSkills: [], modal: false })
    } else {
      this.setState({ modal: true })
    }
  }

  shakeInput = () => {
    this.setState({ animation: 'animated headShake invalid'}, () => {
      setTimeout(() => {
        this.setState({ animation: ''})
      }, 750)
    })
  }

  addSkills = () => {
    this.props.addSkills(this.state.newSkills)
    this.toggle()
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    const skill = ev.target.skill.value
    const newSkills = [...this.state.newSkills]

    if(document.querySelector('.new-skills').clientHeight < 350) {
      this.setState({ listClass: 'sliding'}, () => {
        setTimeout(() => {
          this.setState({ listClass: ''})
        }, 1000)
      })
    }

    if(!newSkills.includes(skill)) {
      newSkills.push(skill)
      this.setState({ newSkills })
    } else {
      this.shakeInput()
    }
    ev.target.reset()
  }
  
  deleteNewSkill = (skill) => {
    const newSkills = [...this.state.newSkills]
    newSkills.splice(newSkills.indexOf(skill), 1)
    this.setState({ newSkills })
  }

  renderNewSkillList = () => {
    return (
      <ul className={`${this.state.listClass} new-skills`}>
        {this.state.newSkills.map((skill, i) => {
          return (
            <div key={i} className="animated slideInUp new-skill">
              <i className="fa fa-times delete" onClick={() => this.deleteNewSkill(skill)}></i> 
              <span className="skill-text">{skill}</span>
            </div>
          )
        })}
      </ul>
    )
  }

  renderSkillsForm = () => {
    return (
      <form className="skill-form" onSubmit={this.handleSubmit}>
        <input 
            className={`${this.state.animation} new-skill-input form-control`}
            required
            autoFocus
            name="skill"
            type="text"
            placeholder="Search for skills"
          />
      </form>
    )
  }

  render() {
    return (
      <div className="SkillsPanel">
        <Button color="danger" onClick={this.toggle}>Modal</Button>
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="add-skills-modal">
          <ModalHeader toggle={this.toggle}>Add Skills</ModalHeader>
          <ModalBody>
            {this.renderNewSkillList()}
            {this.renderSkillsForm()}
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

const mapDispatchToProps = (dispatch) => {
	return {
    addSkills: (skills) => dispatch(addSkills(skills))
	}
}

export default connect(null, mapDispatchToProps)(SkillsPanel)
