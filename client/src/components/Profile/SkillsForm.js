import React, { Component } from 'react'
import { connect } from 'react-redux'
import Autosuggest from 'react-autosuggest'
import AutosuggestHighlightMatch from 'autosuggest-highlight/match'
import AutosuggestHighlightParse from 'autosuggest-highlight/parse'

import '../../css/SkillsForm.css'
import { addNewSkill, deleteNewSkill, fetchData } from '../../redux/actions'

class SkillsForm extends Component {

  constructor(props) {
    super(props)
    this.state = {
      modal: false,
      suggestions: [],
      value: '',
      animation: '',
      listClass: '',
    }
  }

  skills = ['Java', 'JavaScript', 'Basketball', 'Swimming', 'React']
  componentDidMount = () => {
    console.log(this.props.token)
    this.props.fetchData('https://10.0.0.222:17922/api/getSkillList', { 'X-Authorization-Firebase': this.props.token})
  }

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

  shakeInput = () => {
    let element = document.getElementById("skillInput")
    element.classList.toggle("animated")
    element.classList.toggle("headShake")
    element.classList.toggle("invalid")

    setTimeout(() => {
      element.classList.toggle("animated")
      element.classList.toggle("headShake")
      element.classList.toggle("invalid")
    }, 750)
  }

  contains = (skill) => {
    for(const i in this.skills) {
      if(this.skills[i].toLowerCase() === skill.trim().toLowerCase()) return this.skills[i]
    }
    return false
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    let skill = ev.target.skill.value

    if(document.querySelector('.new-skills').clientHeight < 350) {
      this.setState({ listClass: 'sliding'}, () => {
        setTimeout(() => {
          this.setState({ listClass: ''})
        }, 1000)
      })
    }

    if((skill = this.contains(skill)) && !this.props.newSkills.includes(skill)) {
      this.props.addSkill(skill)
    } else {
      this.shakeInput()
    }
    this.setState({ value: '' })
  }
  
  deleteNewSkill = (skill) => {
    this.props.deleteSkill(skill)
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
        {this.props.newSkills.map((skill, i) => {
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
        autoFocus={this.props.autoFocus}
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
        {this.renderNewSkillList()}
        {this.renderSkillsForm()}
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    newSkills: state.user.newSkills ? state.user.newSkills : [],
    token: state.auth.token,
    skills: state.data,
    isLoading: state.fetchIsLoading,
    hasError: state.fetchHasError,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    addSkill: (skill) => dispatch(addNewSkill(skill)),
    deleteSkill: (skill) => dispatch(deleteNewSkill(skill)),
    fetchData: (url, header) => dispatch(fetchData(url))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(SkillsForm)

