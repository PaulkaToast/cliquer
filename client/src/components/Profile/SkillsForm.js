import React, { Component } from 'react'
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
import { connect } from 'react-redux'
import Autosuggest from 'react-autosuggest'
import AutosuggestHighlightMatch from 'autosuggest-highlight/match'
import AutosuggestHighlightParse from 'autosuggest-highlight/parse'

import '../../css/SkillsForm.css'
import { addSkills } from '../../redux/actions'

class SkillsForm extends Component {

  constructor(props) {
    super(props)
    this.state = {
      modal: false,
      newSkills: [],
      suggestions: [],
      value: '',
      animation: '',
      listClass: '',
    }
  }

  skills = ['Java', 'JavaScript', 'Basketball', 'Swimming', 'React']

  getSuggestions = (value) => {
    const escapedValue = value.trim().replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    
    if (escapedValue === '') {
      return []
    }
  
    const regex = new RegExp('\\b' + escapedValue, 'i')
    
    return this.skills.filter(skill => regex.test(this.getSuggestionValue(skill)))
  }

  getSuggestions = value => {
    const inputValue = value.trim().toLowerCase()
    const inputLength = inputValue.length
  
    return inputLength === 0 ? [] : this.skills.filter(skill =>
      skill.toLowerCase().slice(0, inputLength) === inputValue
    )
  }

  onChange = (event, { newValue }) => {
    this.setState({ value: newValue })
  }

  onSuggestionsFetchRequested = ({ value }) => {
    this.setState({ suggestions: this.getSuggestions(value) })
  }

  onSuggestionsClearRequested = () => {
    this.setState({ suggestions: [] })
  }

  toggle = () => {
    if(this.state.modal) {
      this.setState({ newSkills: [], modal: false, value: '', suggestions: [] })
    } else {
      this.setState({ modal: true })
    }
  }

  shakeInput = () => {
    let element = document.getElementById("skillInput")
    console.log
    element.classList.toggle("animated")
    element.classList.toggle("headShake")
    element.classList.toggle("invalid")

    setTimeout(() => {
      element.classList.toggle("animated")
      element.classList.toggle("headShake")
      element.classList.toggle("invalid")
    }, 750)
  }

  addSkills = () => {
    this.props.addSkills(this.state.newSkills)
    this.toggle()
  }

  contains = (skill) => {
    for(const i in this.skills) {
      if(this.skills[i].toLowerCase() === skill.toLowerCase()) return this.skills[i]
    }
    return false
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    let skill = ev.target.skill.value
    const newSkills = [...this.state.newSkills]

    if(document.querySelector('.new-skills').clientHeight < 350) {
      this.setState({ listClass: 'sliding'}, () => {
        setTimeout(() => {
          this.setState({ listClass: ''})
        }, 1000)
      })
    }

    if((skill = this.contains(skill)) && !newSkills.includes(skill)) {
      newSkills.push(skill)
      this.setState({ newSkills })
    } else {
      this.shakeInput()
    }
    this.setState({ value: '' })
  }
  
  deleteNewSkill = (skill) => {
    const newSkills = [...this.state.newSkills]
    newSkills.splice(newSkills.indexOf(skill), 1)
    this.setState({ newSkills })
  }

  renderSuggestion = (suggestion, { query }) => {
    const matches = AutosuggestHighlightMatch(suggestion, query)
    const parts = AutosuggestHighlightParse(suggestion, matches)
  
    return (
      <span className={'suggestion-content'}>
        <span className="name">
          {
            parts.map((part, index) => {
              const className = part.highlight ? 'highlight' : null
              return (
                <span className={className} key={index}>{part.text}</span>
              )
            })
          }
        </span>
      </span>
    )
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

  renderInputComponent = inputProps => {
    return (
      <input 
        className={`${this.state.animation} new-skill-input form-control`}
        id="skillInput"
        required
        autoFocus
        name="skill"
        type="text"
        {...inputProps}
      />
    )
  }

  renderSkillsForm = () => {
    const { value, suggestions } = this.state
    const inputProps = {
      placeholder: 'Type a skill',
      value,
      onChange: this.onChange
    }

    return (
      <form className="skill-form" onSubmit={this.handleSubmit}>
        <Autosuggest
          suggestions={suggestions}
          onSuggestionsFetchRequested={this.onSuggestionsFetchRequested}
          onSuggestionsClearRequested={this.onSuggestionsClearRequested}
          getSuggestionValue={suggestion => suggestion}
          renderSuggestion={this.renderSuggestion}
          renderInputComponent={this.renderInputComponent}
          inputProps={inputProps}
        />
      </form>
    )
  }
  render() {
    return (
      <div className="SkillsForm">
        <Button color="danger" onClick={this.toggle}>Add skills</Button>
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

export default connect(null, mapDispatchToProps)(SkillsForm)

