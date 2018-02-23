import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import { Container, Row, Col, Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/Register.css'
import Logo from '../img/cliquerLogo.png'
import { auth, firebase } from '../firebase'
import { registerUser, setToken } from '../redux/actions'

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
            }).then(() => {
                this.props.register(`https://10.0.0.222:17922/register?first=${firstName}&last=${lastName}`, { 'X-Authorization-Firebase': this.props.token})
            })
            .catch((error) => {
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
      <Container fluid>
        <Row>
          <Col className="logo" md={{ size: 4, offset:4}}>
            <img src={Logo} alt="" />
          </Col>
        </Row>
      <div className="form-register">
        <form onSubmit={this.handleSubmit}>
          <input id="input-first-name" className="form-control"
            required autoFocus
            name="firstName"
            type="text"
            placeholder="First Name"
          />
          <input id="input-last-name" className="form-control"
            required
            autoFocus
            name="lastName"
            type="text"
            placeholder="Last Name"
          />
          <input id="input-email" className="form-control"
            required
            name="email"
            type="text"
            placeholder="Email Address"
          />
          <input id="input-password" className="form-control"
            required
            name="passwordOne"
            type="password"
            placeholder="Password"
          />
          <input id="confirm-password" className="form-control"
            required
            name="passwordTwo"
            type="password"
            placeholder="Confirm Password"
          />
          <Button type="submit" color="primary" size="lg" block>Register</Button>
          
          <div className="fb-container">
            <button type="button" className="btn btn-lg btn-block btn-social btn-facebook" 
              onClick={this.logInWithFacebook}>
              <i className="fab fa-facebook-f"></i> Register with Facebook
            </button>
          </div>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
      </div>
      <Row>
          <Col className="login-container" md={{ size: 4, offset: 4 }}>
            <Link to="/login">
                Already have an account? Log In
            </Link>
          </Col>
        </Row>
      </Container>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    token: state.auth.token,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    register: (url, headers) => dispatch(registerUser(url, headers)),
    setToken: (token) => dispatch(setToken(token))
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Register)