import React, { Component } from 'react'
import '../../css/UserInfo.css'

class UserInfo extends Component {
  render() {
    const { user } = this.props
    return (
      <div className="UserInfo">
           <div className ="img">
            </div> 
           <div className = "UN"> 
                   {user ? user.displayname : ''}  
           </div>   
      </div>  
    )
  }
}

export default UserInfo
