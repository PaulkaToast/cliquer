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
      const firstName = ev.target.firstName.value
      const lastName = ev.target.lastName.value
      const email = ev.target.email.value
      const passwordOne = ev.target.passwordOne.value

      auth.createUserWithEmailAndPassword(email, passwordOne)
        .then(authUser => {
          authUser.updateProfile({
            displayName: `${firstName} ${lastName}`,
          }).catch((error) => {
            console.log(error)
          })
        })
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
            name="firstName"
            type="text"
            placeholder="First Name"
          />
          <input
            required
            autoFocus
            name="lastName"
            type="text"
            placeholder="Last Name"
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