import React, { Component } from 'react'
import { connect } from 'react-redux'

import '../css/ModeratorApplication.css'
import { applyForMod } from '../redux/actions'
import url from '../server.js'

class ModeratorApplication extends Component { 
    
    constructor(props) {
        super(props);
        this.state = {
            value: '',
        }
    }
    

    handleChange = (event) => {
        this.setState({value: event.target.value})
    }
    
    handleSubmit = (event) => {
        event.preventDefault()
        console.log('submitted')
        this.props.applyForMod(`${url}/api/applyForMod?userId=${this.props.accountID}&messageId=${this.props.notification.messageID}`, { 'X-Authorization-Firebase': this.props.token}, JSON.stringify(this.state.value))
        this.acceptNotification(this.props.notification.messageID)
        this.setState({ value: '' })
        this.props.setState({ modNotification: null }, () => {
            this.props.toggle()
        })
    }

    render() {
        return (
        <div className="ModeratorApplication">
            <form id="mod-application-form" onSubmit={this.handleSubmit}>
                <label>
                <textarea required placeholder="Why do you want to be a moderator?" value={this.state.value} onChange={this.handleChange} />
                </label>
            </form>
        </div>
        )
    }
}

const mapStateToProps = (state) => {
	return {
        user: state.user.data,
        token: state.auth.token,
        accountID: state.user.accountID,
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
        applyForMod: (url, headers, body) => dispatch(applyForMod(url, headers, body)),
	}
}


export default connect(mapStateToProps, mapDispatchToProps)(ModeratorApplication)