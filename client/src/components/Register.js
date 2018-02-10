import React, { Component } from 'react'
import { auth } from '../firebase'
import '../css/Register.css'
import { history } from '../redux/store'
import { logIn } from '../redux/actions/actionCreator'
import { connect } from 'react-redux'

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
      alert('Passwords do not match!')
    } else {
      const username = ev.target.username.value
      const email = ev.target.email.value
      const passwordOne = ev.target.passwordOne.value

      auth.createUserWithEmailAndPassword(email, passwordOne)
        .then(authUser => {
          this.props.logIn(authUser)
          history.push('/groups');
        })
        .catch(error => {
          this.setState({ error });
        });
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
    );
  }
}

const mapDispatchToProps = (dispatch) => {
	return {
		logIn: (user) => dispatch(logIn(user))
	}
}

export default connect(null, mapDispatchToProps)(Register);
