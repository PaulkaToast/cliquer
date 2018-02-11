import React, { Component } from 'react'

import '../css/Login.css'
import { auth, facebookProvider } from '../firebase'

class Login extends Component {

  constructor(props) {
    super(props)

    this.state = { 
      error: '',
    }
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
   
    const email = ev.target.email.value
    const password = ev.target.password.value

    auth.logInWithEmailAndPassword(email, password)
      .catch(error => {
        this.setState({ error })
      })
  }

  logInWithFacebook = () => {
    auth.signInWithFacebook(facebookProvider)
      .catch(function(error) {
        this.setState({ error })
      })
  }


  render() {
    return (
      <div className="Login">
          <form onSubmit={this.handleSubmit}>
          <input
            required
            name="email"
            type="text"
            placeholder="Email Address"
          />
          <input
            required
            name="password"
            type="password"
            placeholder="Password"
          />
          <button type="submit">
            Log In
          </button>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
          <button 
            type="button"
            onClick={this.logInWithFacebook}
          >
            Log In with Facebook
          </button>
      </div>
    )
  }
}

export default Login

