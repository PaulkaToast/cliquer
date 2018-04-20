import React, { Component } from 'react'
import { Container, Row, Col, Button, Modal, ModalHeader, ModalBody,
         Input, Label, FormGroup,
         Form } from 'reactstrap'
import { connect } from 'react-redux'
import Toggle from 'react-toggle'

import '../css/Settings.css'
import { auth, firebase, credential } from '../firebase'
import { deleteProfile, logOut, getProfile, setSettings } from '../redux/actions'
import url from '../server.js'

class Settings extends Component {

  constructor(props) {
    super(props)

    this.state = {
      error: '',
      modal:false,
      proximity: 0,
      reupationReq: 0,
    }
  }

  componentWillReceiveProps = (nextProps) => {
    if(nextProps.uid && nextProps.token) {    
      const uid = nextProps.uid 
      // Get profile data
      if(!nextProps.profile && !nextProps.profileIsLoading) {
        console.log('settings call')
        this.props.getProfile(`${url}/api/getProfile?username=${uid}&type=user`, { 'X-Authorization-Firebase': nextProps.token})
      } else if(nextProps.profile) {
        this.setState({ proximity: nextProps.profile.proximityReq, reputation: Math.round(nextProps.profile.reputationReq * nextProps.profile.reputation)})
      }
    }
  }

  toggle = () => {
    this.setState({ modal: !this.state.modal });
  }

  handleSubmit = (ev) => {
    ev.preventDefault()
    const isPublic = ev.currentTarget.isPublic.checked
    const isOptedOut = ev.currentTarget.optOut.checked
    const proximityReq = ev.currentTarget.proximity.value
    const reputationReq = ev.currentTarget.reputation.value

    this.props.setSettings(`${url}/api/setSettings?username=${this.props.uid}`, { 'X-Authorization-Firebase': this.props.token}, 
                          JSON.stringify({
                            isPublic,
                            isOptedOut,
                            proximityReq,
                            reputationReq,
                          }))
  }

  deleteAccount = (ev) => {
    ev.preventDefault()

    const password = ev.target.deletePassword.value
    const user = firebase.currentUser
    const cred = credential(user.email, password)

    user.reauthenticateWithCredential(cred).then(func => {
      this.props.deleteProfile(`${url}/api/deleteProfile?username=${this.props.user.uid}`, { 'X-Authorization-Firebase': this.props.token })
      this.props.logOut()
    }).catch(error => {
        this.setState({ error })
      })

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
          this.setState({ error })
        })}
    }).catch(error => {
        this.setState({ error })
      })
  }

  render() {
    //TODO: default value on optOut/isPublic
    //TODO: controlled vs uncontrolled input
    const reputation = this.props.profile ? this.props.profile.reputation : 0
    //const minRep = this.props.profile ? this.props.profile.reputationReq : 0
    //const proximity = this.props.profile ? this.props.profile.proximityReq : 0
    const optOut = this.props.profile ? this.props.profile.optedOut : false
    const isPublic = this.props.profile ? this.props.profile.public : false
    if(!this.props.profile){
      return (
        <div className="loader">Loading...</div>
      )
    }
    return (
      <Container>
        <h2 className="account-settings-label">Account Settings</h2>
        <hr />        
      <Label className="search-settings-label" md={{ size: 4, offset: 4}}>
        General Settings
      </Label>
      <div className="search-settings-section" md={{ size: 4, offset: 4}}>
      <Form onSubmit={this.handleSubmit}>
        <FormGroup className="search-settings">
          <Toggle defaultChecked={optOut} name="optOut" />
          <span> Opt out of search results </span>
        </FormGroup>
        <FormGroup>
          <Toggle defaultChecked={isPublic} name="isPublic" />
          <span> Make your profile public</span>
        </FormGroup>
        <FormGroup>
          <Label for="reputation">Minimum Reputation</Label>
          <Input type="number" name="reputation" id="reputation" min={0} max={reputation} value={this.state.reputation} onChange={(ev) => {
            this.setState({ reputation: ev.target.value})
          }} />
        </FormGroup>
        <FormGroup>
          <Label for="proximity">Maximum Proximity (Miles)</Label>
          <Input type="number" name="proximity" id="proximity" min={0} value={this.state.proximity} onChange={(ev) => {
            this.setState({ proximity: ev.target.value})
          }} />
        </FormGroup> 
        <Button color="primary" type="submit" onSubmit={this.handleSubmit} block>Submit</Button>
      </Form>  
      </div>
        <hr/>
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
      <Modal isOpen={this.state.modal} toggle={this.toggle} className="delete-account-modal">
          <ModalHeader toggle={this.toggle}>Delete your Account?</ModalHeader>
          <ModalBody>
          <div className="Settings form-delete" md={{ size: 4, offset: 4}}>
            <form onSubmit={this.deleteAccount}>
              <label htmlFor="inputPassword" className="sr-only">Password</label>
              <input id="inputPassword" className="form-control"
                required
                name="deletePassword"
                type="password"
                placeholder="Enter Password"
              />
              <Button type="submit" color="danger">Delete</Button>
              <Button color="secondary" onClick={this.toggle}>Cancel</Button>
              { this.state.error && <p>{this.state.error.message}</p> }
            </form>
          </div>
          </ModalBody>
        </Modal>
        <hr />
      <div className="delete_account_section" md={{ size: 4, offset: 4}}>
        <a href="mailto:cliquer307@gmail.com">
          <Button color="primary" size="lg" block>Send Feedback</Button>
        </a>
        <Button color="danger" className="delete-button" size="lg" onClick={this.toggle} block>Delete Account</Button>
      </div>
      </Container>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user && state.user.data ? state.user.data : null,
    uid: state.user && state.user.data ? state.user.data.uid : null,
    token: state.auth.token,
    profileIsLoading: state.profile && state.profile.getIsLoading ? state.profile.getIsLoading : null,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    deleteProfile: (url, headers) => dispatch(deleteProfile(url, headers)),
    logOut: () => dispatch(logOut()),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    setSettings: (url, headers, body) => dispatch(setSettings(url, headers, body)),
	}
}

export default connect(mapStateToProps, mapDispatchToProps)(Settings)
