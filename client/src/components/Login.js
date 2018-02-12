import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Container, Row, Col, Button } from 'reactstrap';

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
      <Container fluid>
        <Row>
          <Col className="sign-up-container" md={{ size: 4, offset: 8 }}>
            <Link to="/register">
              <Button outline color="secondary">
                Sign Up
              </Button>
            </Link>
          </Col>
        </Row>
      <div className="Login form-signin">
          <form onSubmit={this.handleSubmit}>
          <h2 className="form-signin-heading">Sign In </h2>
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
          <Button type="submit" color="primary" size="lg" block>Sign In</Button>
          { this.state.error && <p>{this.state.error.message}</p> }
          
          <Button type="submit" color="info" size="lg" block 
            onClick={this.logInWithFacebook}
          >
            Sign In with Facebook
          </Button>
        </form>
      </div>
      </Container>
    )
  }
}

export default Login

