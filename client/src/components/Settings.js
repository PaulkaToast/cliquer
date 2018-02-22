import React, { Component } from 'react'
import { Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/Settings.css'
import { auth, firebase, credential } from '../firebase'

class Settings extends Component {

  constructor(props) {
    super(props)

    this.state = {
      error: '',
    }
  }

  changePassword = (ev) => {
    ev.preventDefault()

    const oldPassword = ev.target.oldPassword.value
    const newPassword = ev.target.newPassword.value
    const confirmPassword = ev.target.newPassword.value

    const user = firebase.currentUser

    const cred = credential(user.email, oldPassword)

    user.reauthenticateWithCredential(cred)
      .catch(error => {
        this.setState({ error })
      })

    if(newPassword !== confirmPassword) {
      this.setState({ error: { message: "Passwords do not match!" } })
    } else {
      auth.doPasswordUpdate(newPassword)
      .catch(error => {
        console.log(error)
        this.setState({ error })
      })}
  }

  render() {
    return (
      <div className="Settings">
        <form onSubmit={this.changePassword}>
          <label htmlFor="inputOldPassword" className="sr-only">Old Password</label>
          <input id="inputOldPassword" className="form-control"
            required
            name="oldPassword"
            type="password"
            placeholder="Old Password"
          />
          <label htmlFor="inputNewPassword" className="sr-only">New Password</label>
          <input id="inputNewPassword" className="form-control"
            required
            name="newPassword"
            type="password"
            placeholder="New Password"
          />
          <label htmlFor="confirmNewPassword" className="sr-only">Confirm Password</label>
          <input id="confirmNewPassword" className="form-control"
            required
            name="confirmPassword"
            type="password"
            placeholder="Confirm Password"
          />
          <Button type="submit" color="primary" size="lg" block>Change Password</Button>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
	}
}

export default connect(mapStateToProps)(Settings)
