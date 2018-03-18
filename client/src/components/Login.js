import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import { Container, Row, Col, Button } from 'reactstrap'
import { connect } from 'react-redux'

import '../css/Login.css'
import Logo from '../img/cliquerLogo.png'
import { auth, facebookProvider } from '../firebase'
import { registerUser } from '../redux/actions'
import url from '../server.js'

export class Login extends Component {

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
      .then((response) => {
          const name = response.user.displayName.split(' ')
          response.user.getIdToken(true)
          .then((token) => {
            this.props.register(`${url}/register?first=${name[0]}&last=${name[1]}`, { 'X-Authorization-Firebase': token})
          })
      })
      .catch(error => {
        this.setState({ error })
      })
  }


  render() {
    return (
      <Container fluid>
        <Row>
          <Col className="logo" md={{ size: 4, offset:4}}>
            <img src={Logo} alt="" />
          </Col>
        </Row>

      <div className="Login form-signin">
          <form onSubmit={this.handleSubmit}>
          <label htmlFor="inputEmail" className="sr-only">Email address</label>
          <input id="inputEmail" className="form-control"
            required autoFocus
            name="email"
            type="email"
            placeholder="Email Address"
          />
          <label htmlFor="inputPassword" className="sr-only">Password</label>
          <input id="inputPassword" className="form-control"
            required
            name="password"
            type="password"
            placeholder="Password"
          />
          <Button type="submit" color="primary" size="lg" block>Log In</Button>

          <div className="fb-container">
            <button type="button" className="btn btn-lg btn-block btn-social btn-facebook"
              onClick={this.logInWithFacebook}>
              <i className="fab fa-facebook-f fa-fw"></i> Log in with Facebook
            </button>
          </div>

          { this.state.error && <p>{this.state.error.message}</p> }
        </form>
      </div>
      <Row>
          <Col className="sign-up-container" md={{ size: 4, offset: 4 }}>
            <Link to="/register">
                Create an Account
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
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Login)
