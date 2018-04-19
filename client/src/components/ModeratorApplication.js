import React, { Component } from 'react'
import '../css/ModeratorApplication.css'

    class ModeratorApplication extends Component{
        
        constructor(props) {
            super(props);
            this.state = {value: '',
            ModeratorApplication: false,
            memberID: ''
        };
        
            this.handleChange = this.handleChange.bind(this);
            this.handleSubmit = this.handleSubmit.bind(this);
        }

        toggle = () => {
            this.setState({ModeratorApplication : !this.state.ModeratorApplication})
        }
        
        getAppForm = (memberID) => {
            this.toggle()
            this.props.getRateForm(`${url}/api/applyForMod?userId=${this.props.accountID}&messageId=${data.messageID}`, { 'X-Authorization-Firebase': this.props.token})
            this.setState({ memberID })
          }

        handleChange(event) {
            this.setState({value: event.target.value});
        }
        
        handleSubmit(event) {
            alert('An application was submitted: ' + this.state.value);
            event.preventDefault();
        }

        render() {
        <form onSubmit={this.handleSubmit}>
        <h1> Moderator Application </h1>
        <label>
          Application Statement:
          <textarea value={this.state.value} onChange={this.handleChange} />
        </label>
        <input type="submit" value="Submit" />
      </form>
        }
}