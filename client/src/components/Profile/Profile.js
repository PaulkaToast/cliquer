import React, { Component } from 'react'
import { connect } from 'react-redux'
import { TabContent, TabPane, Nav, NavItem, NavLink, 
  Button, ListGroup, ListGroupItem,
  Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap'
import classnames from 'classnames';
import Geocode from 'react-geocode'
import Dropzone from 'react-dropzone'

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import NotificationPanel from './NotificationPanel'
import { getSkills, getProfile, getGroups, flagUser, setLocation, setCity, reportUser, clearGroups, clearSkills, clearProfile, uploadFile } from '../../redux/actions'
import url from '../../server.js'
import nFlag from '../../img/newUser.png'

class Profile extends Component {
  constructor(props) {
    super(props)

    this.state = {
      activeTab: '1',
      modal: false,
      modalU: false,
      flagged: false,
      loading: false,
    }
  }

  componentWillMount = () => {
    this.props.clearProfile()
    this.props.clearSkills()
    this.props.clearGroups()
  }

  componentDidMount = () => {
    this.fetch(this.props)
  }

  componentWillReceiveProps = (nextProps) => {
    this.fetch(nextProps)
  }

  componentWillUnmount = () => {
    this.props.clearSkills()
    this.props.clearGroups()
    this.props.clearProfile()
  }

  fetch = (props) => {
    if(props.uid && props.accountID && props.token) {    
      const ownerID = props.match.params.ownerID
      const type = ownerID === props.accountID ? 'user' : 'public'

      // Get profile data
      if((!props.profile && !props.profileIsLoading) || (props.profile && props.profile.accountID !== ownerID)) {
        console.log('profile call')
        this.props.getProfile(`${url}/api/getProfile?userId=${ownerID}&type=${type}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get skills data
      if(props.postData !== props.postData || (!props.skills && !props.skillsIsLoading)) {
        this.props.getSkills(`${url}/api/getSkills?userId=${ownerID}`, { 'X-Authorization-Firebase': props.token})
      }

      // Get groups
      if(!props.groups && !this.isOwner(ownerID)) {
        this.props.getGroups(`${url}/api/getUserGroups?username=${props.uid}`, { 'X-Authorization-Firebase': props.token })
      }

      if(props.profile && !props.profileIsLoading && !props.city) {
        this.setCity(props.profile.latitude, props.profile.longitude)
      }
    }
  }

  toggle = (tab) => {
    if (this.state.activeTab !== tab) {
      this.setState({
        activeTab: tab
      })
    }
  }

  toggleM = () => {
    this.setState({ modal: !this.state.modal })
  }

  toggleU = () => {
    this.setState({ modalU: !this.state.modalU, dropped: false })
  }

  isOwner = (accountID) => {
    return accountID === this.props.accountID || !this.props.accountID
  } 

  inviteAndToggle = (gid, ownerID) => {
    this.props.inviteToGroup(gid, ownerID)
    this.toggleM()
  }

  reportUser = (ownerID) => {
    this.props.reportUser(`${url}/api/reportUser?userId=${this.props.accountID}&reporteeId=${ownerID}&reason=none`, { 'X-Authorization-Firebase': this.props.token})
  }

  setCity = (lat, long) => {
    if(lat && long) {
      Geocode.fromLatLng(lat, long).then(
        response => {
          const address = response.results[2].formatted_address
          this.props.setCity(address)
          this.setState({ loading: false })
        },
        error => {
          console.error(error)
        }
      )
    }
  }

  loadImage = (image) => {
    if (FileReader && image) {
      let fr = new FileReader()
      fr.onload = () => {
          document.querySelector('#profile-picture').src = fr.result
          this.props.uploadFile(`${url}/api/uploadFile?userId=${this.props.accountID}`, { 'X-Authorization-Firebase': this.props.token}, JSON.stringify(fr.result))
      }
      fr.readAsDataURL(image)
    }
  }

  onDrop = (accepted, rejected) => {
    this.setState({ dropped: true })
    if(accepted[0]) {
      this.loadImage(accepted[0])
    }
  }

  renderGroupList = () => {
    const groups = this.props.groups
    const ownerID = this.props.match.params.ownerID
    //TODO: check if user already exists in group
    return (
      <ListGroup>
        {groups && Object.keys(groups).length > 0
        && Object.keys(groups).map((gid, i) => {
          if (groups[gid].groupMemberIDs[this.props.profile.accountID]){
            return null;
          }else{
          return(<ListGroupItem key={gid}>
              {groups[gid].groupName} <Button className="invite-to-group-button" type="button" size="lg" 
              onClick={() => this.inviteAndToggle(gid, ownerID)}>Invite</Button>
            </ListGroupItem>)
          }
        })}
      </ListGroup>
    )  
  }
  
  render() {
    const { profile, skills, groups } = this.props
    const ownerID = this.props.match.params.ownerID

    if(!profile || profile.accountID !== ownerID){
      return (
        <div className="loader">Loading...</div>
      )
    }
    let flag = profile.newUser ? nFlag : "";
    return (
      <div>
        <Nav tabs>
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '1' })}
              onClick={() => { this.toggle('1'); }}
            >
              Profile
            </NavLink>
          </NavItem>
        {this.isOwner(ownerID) &&
          <NavItem>
            <NavLink
              className={classnames({ active: this.state.activeTab === '2' })}
              onClick={() => { this.toggle('2'); }}
            >
              Friends
            </NavLink>
          </NavItem>
        }
        {this.isOwner(ownerID) && 
            <NavItem>
              <NavLink
                className={classnames({ active: this.state.activeTab === '2' })}
                onClick={() => { this.toggle('3'); }}
              >
                Notifications
              </NavLink>
            </NavItem>
          }
        </Nav>
        <TabContent activeTab={this.state.activeTab}>
          <TabPane className="profile-tab" tabId="1">
            <hr/>
            <div className="main-info">
              <img id="profile-picture" onClick={this.toggleU} src={profile.picture} alt=""></img>
              <h1>
                {profile.moderator && <i className="fas fa-user-secret"></i>} {profile.fullName}<img className="profile-user-flag" src={flag} alt=""></img>
              </h1>
            </div>
            <hr/>
            <h4>
              Reputation: {profile.reputation}
            </h4>
            <h4>
              Location: {this.state.loading
                        ? 'Loading...'
                        : this.props.city
                        ? this.props.city
                        : 'Location not set'}
             &nbsp;&nbsp;{this.isOwner(ownerID) && <i className="fa fa-pencil-alt" onClick={() => {
                   if (navigator.geolocation) {
                        this.setState({ loading: true })
                        navigator.geolocation.getCurrentPosition(position => {
                          const lat = position.coords.latitude
                          const long = position.coords.longitude
                          this.props.setLocation(`${url}/api/setLocation?userId=${ownerID}&latitude=${lat}&longitude=${long}`, { 'X-Authorization-Firebase': this.props.token})
                          this.setCity(lat, long)
                        },
                        error => {
                          this.setState({ loading: false })
                          this.props.setCity('Could not determine location. Please try again later.')
                          console.log(error)
                        }, {
                          timeout: 7000
                        })
                    } else {
                      //TODO: Geolocation is not supported
                      alert('Geolocation is not supported in your browser. Please switch to a browser that does, such as Chrome or Firefox.')
                    } 
                }}></i> }
            </h4>
            <hr/>
            
            {!this.isOwner(ownerID) && !this.props.ownProfile.friendIDs[this.props.profile.accountID] && 
            <Button type="button" size="lg" onClick={() => this.props.sendFriendRequest(ownerID)}>Send Friend Request</Button>}
            {!this.isOwner(ownerID) && groups && Object.keys(groups).length > 0 && 
              <Button type="button" size="lg" onClick={this.toggleM}>Invite To Group</Button>}
            {!this.isOwner(ownerID) && 
              <Button type="button" color="warning" size="lg" onClick={() => this.reportUser(ownerID)}>Report User</Button>}
            <hr/>
            <h4>
              Skills:
            </h4>
              <SkillsPanel skills={skills} isOwner={this.isOwner(ownerID)}/>
          </TabPane>
          <TabPane tabId="2">
            <h4>
              Friends:
            </h4>
            <ListGroup>
            {this.props.profile && this.props.profile.friendIDs && Object.keys(this.props.profile.friendIDs).map((key) =>
            { return <ListGroupItem  key={key}>
              <a href={"/profile/" + key}>{profile.friendIDs[key]}</a>
              <Button className="friend-cancel-button" onClick={() => {}} color="link">x</Button>
            </ListGroupItem>})}
            </ListGroup>
          </TabPane>
          <TabPane tabId="3">
            <NotificationPanel 
              deleteNotification={this.props.deleteNotification} 
              markAsRead={this.props.markAsRead} 
              acceptNotification={this.props.acceptNotification}
              rejectNotification={this.props.rejectNotification}
            />
          </TabPane>
        </TabContent>

        <Modal isOpen={this.state.modalU} toggle={this.toggleU} className="upload-image-modal">
          <ModalHeader toggle={this.toggleU}>Upload Image</ModalHeader>
          <ModalBody>
          <Dropzone
            accept="image/jpeg, image/png"
            maxSize={5000000}
            className="picture-upload"
            acceptStyle={{borderColor: 'green'}}
            rejectStyle={{borderColor: 'red'}}
            multiple={false}
            onDrop={this.onDrop}
          >
           { !this.state.dropped ?
           <div>
              <p>Drop a picture here, or click to upload a picture.</p>
              <p>Only .jpeg and .png images less than 5 MB will be accepted</p>
            </div>
            : <p>Your picture has been uploaded!</p>}
          </Dropzone>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggleU}>Close</Button>
          </ModalFooter>
        </Modal>

        <Modal isOpen={this.state.modal} toggle={this.toggleM} className="invite-modal">
          <ModalHeader toggle={this.toggleM}>Invite To Group</ModalHeader>
          <ModalBody>
            {this.renderGroupList()}
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggleM}>Close</Button>
          </ModalFooter>
        </Modal>
      </div>
    )
  } 
}

const mapStateToProps = (state) => {
  return {
    user: state.user && state.user.data ? state.user.data : null,
    uid: state.user && state.user.data ? state.user.data.uid : null,
    city: state.user && state.user.city ? state.user.city : null,
    profileIsLoading: state.profile && state.profile.getIsLoading ? state.profile.getIsLoading : null,
    profile: state.profile && state.profile.getData ? state.profile.getData : null,
    skills: state.skills && state.skills.getData ? state.skills.getData : null,
    skillsIsLoading: state.skills && state.skills.getIsLoading ? state.skills.getIsLoading : null,
    postData: state.skills && state.skills.postData ? state.skills.postData : null,
    accountID: state.user.accountID,
    groups: state.groups ? state.groups.getGroupsData : [],
    token: state.auth.token,
    notifications: state.messages.data
  }
}

const mapDispatchToProps = (dispatch) => {
  return {
    getSkills: (url, headers) => dispatch(getSkills(url, headers)),
    getProfile: (url, headers) => dispatch(getProfile(url, headers)),
    getGroups: (url, headers) => dispatch(getGroups(url, headers)),
    flagUser: (url, headers) => dispatch(flagUser(url, headers)),
    setLocation: (url, headers) => dispatch(setLocation(url, headers)),
    reportUser: (url, headers) => dispatch(reportUser(url, headers)),
    setCity: (city) => dispatch(setCity(city)),
    uploadFile: (url, headers, body) => dispatch(uploadFile(url, headers, body)),
    clearSkills: () => dispatch(clearSkills()),
    clearGroups: () => dispatch(clearGroups()),
    clearProfile: () => dispatch(clearProfile()),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Profile)