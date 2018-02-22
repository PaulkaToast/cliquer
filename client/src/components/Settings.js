import React, { Component } from 'react'
import { Container, Row, Col, Button } from 'reactstrap'
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
    const confirmPassword = ev.target.confirmPassword.value

    const user = firebase.currentUser

    const cred = credential(user.email, oldPassword)

    user.reauthenticateWithCredential(cred).then(function() {
      if(newPassword !== confirmPassword) {
        this.setState({ error: { message: "Passwords do not match!" } })
      } else {
        auth.doPasswordUpdate(newPassword).then(function(){
          window.alert("Password Updated Successfully")
        }).catch(error => {
          console.log(error)
          this.setState({ error })
        })}
    }).catch(error => {
        this.setState({ error })
      })
  }

  render() {
    return (
      <Container>
      <Row>
          <Col className="change-password-container" md={{ size: 4, offset: 4 }}>
                Change Password
          </Col>
        </Row>
      <div className="Settings form-password" md={{ size: 4, offset: 4}}>
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
          <Button type="submit" color="primary" size="lg" block>Submit</Button>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
      </div>
      </Container>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
	}
}

export default connect(mapStateToProps)(Settings)
