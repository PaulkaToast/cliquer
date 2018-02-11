import React, { Component } from 'react'

import '../css/Navbar.css'
import { auth } from '../firebase'

class Navbar extends Component {


  render() {
    return (
      <div className="Navbar">
        <button
          type="button"
          onClick={auth.logOut}
        >
          Log Out
        </button>
      </div>
    );
  }
}

export default Navbar;
