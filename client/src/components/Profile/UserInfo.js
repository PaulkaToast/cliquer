import React, { Component } from 'react'
import '../../css/UserInfo.css'

class UserInfo extends Component {
  render() {
    return (
      <div className="UserInfo">
           <img src="avatar_hat.jpg" alt="Avatar"></img>
             <div class="avatar">
                <h2>Jane Doe</h2>
             </div>

      </div>  
    )
  }
}

export default UserInfo
