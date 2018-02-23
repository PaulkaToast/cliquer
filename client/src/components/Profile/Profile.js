import React, { Component } from 'react'

import '../../css/Profile.css'
import SkillsPanel from './SkillsPanel'
import FriendsPanel from './FriendsPanel'
import UserInfo from './UserInfo'
import NotificationPanel from './NotificationPanel'

class Profile extends Component {

  render() {
    return (
      <div className="Profile">
        <NotificationPanel notifications={[{messageID: 111, type: 0}]}/>
       
        <UserInfo />

        <div class="navbar">
              <a href="#">Link</a>
              <a href="#">Link</a>
              <a href="#">Link</a>
              <a href="#" class="right">Link</a>
          </div>
                    

         <div className="header">
             <h1>My Profile</h1>
                 <p>A website created by me.</p>  
          </div>

     

          <div className="row">
            <div className="side">...</div>
            <div className="main">...</div>
          </div>

          <div className="skills">
            <div>
               <p>
                    <SkillsPanel />   
               </p>
            </div>
          </div>


          <footer className="footer">
           <p>Find me on social media.</p>
              <i className="fa fa-facebook-official w3-hover-opacity"></i>
              <i className="fa fa-instagram w3-hover-opacity"></i>
              <i className="fa fa-snapchat w3-hover-opacity"></i>
              <i className="fa fa-pinterest-p w3-hover-opacity"></i>
              <i className="fa fa-twitter w3-hover-opacity"></i>
              <i className="fa fa-linkedin w3-hover-opacity"></i>
          </footer>
      </div>

    )
  }
}
function getScrollPercent() {
  return (
    (document.documentElement.scrollTop || document.body.scrollTop) 
    / ( (document.documentElement.scrollHeight || document.body.scrollHeight) 
    - document.documentElement.clientHeight) 
    * 100);
}


export default Profile
