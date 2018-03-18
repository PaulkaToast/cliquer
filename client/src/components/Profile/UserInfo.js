import React, { Component } from 'react'
import '../../css/UserInfo.css'
import jmbuck from '../../img/avatar.png'

class UserInfo extends Component {
  render() {
    const { user } = this.props
    return (
      <div className="UserInfo">
           <div className ="img">
                <img src={jmbuck} alt="AvatarX"></img>  
            </div> 
           <div className = "UN"> 
                   {user ? user.displayname : ''}  
           </div>   
      </div>  
    )
  }
}

export default UserInfo
