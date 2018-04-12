import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../css/ModPanel.css'
import url from '../server'

class ModPanel extends Component {
  
  render() {
    return (
      <div className="ModPanel">
        MODERATOR PANEL
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    accountID: state.user.accountID,
    token: state.auth.token,
	}
}

const mapDispatchToProps = (dispatch) => {
	return 
}

export default connect(mapStateToProps, mapDispatchToProps)(ModPanel)

