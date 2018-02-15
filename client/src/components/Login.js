import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Container, Row, Col, Button } from 'reactstrap';

import '../css/Login.css'
import Logo from '../images/cliquerLogo.png';
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
              <i className="fa fa-facebook fa-fw"></i> Log in with Facebook
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

export default Login

