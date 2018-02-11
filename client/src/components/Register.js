import React, { Component } from 'react'

import '../css/Register.css'
import { auth } from '../firebase'

class Register extends Component {

  constructor(props) {
    super(props)
    this.state = {
      error: ''
    }

  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    if(ev.target.passwordOne.value !== ev.target.passwordTwo.value) {
      this.setState({ error: { message: 'Passwords do not match!' } })
    } else {
      const username = ev.target.username.value
      const email = ev.target.email.value
      const passwordOne = ev.target.passwordOne.value

      auth.createUserWithEmailAndPassword(email, passwordOne)
        .catch(error => {
          this.setState({ error })
        })
    }
  }

  render() {
    return (
      <div className="Register">
        <form onSubmit={this.handleSubmit}>
          <input
            required
            name="username"
            type="text"
            placeholder="Full Name"
          />
          <input
            required
            name="email"
            type="text"
            placeholder="Email Address"
          />
          <input
            required
            name="passwordOne"
            type="password"
            placeholder="Password"
          />
          <input
            required
            name="passwordTwo"
            type="password"
            placeholder="Confirm Password"
          />
          <button type="submit">
            Register
          </button>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
      </div>
    )
  }
}

export default Register