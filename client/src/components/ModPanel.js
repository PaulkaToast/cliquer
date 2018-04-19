import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Redirect } from 'react-router'
import { Modal, ModalHeader, ModalBody, ModalFooter,
         Form, FormGroup, Label, Input, Button } from 'reactstrap'

import { submitSkill, flagUser, suspendUser } from '../redux/actions'
import '../css/ModPanel.css'
import url from '../server'

class ModPanel extends Component {
  constructor(props) {
    super(props)

    this.state = {
      modal: false,
      reports: {}
    }
  }

  componentWillReceiveProps = (nextProps) => {
    const reports = {}
    if(nextProps.messages) {
      Object.values(nextProps.messages).forEach((message) => {
        if(message.type === 14) {
          reports[message.messageID] = message
          reports[message.messageID].flagged = false
          //2 is the number of flags required to suspend someone
          reports[message.messageID].canSuspend = message.counter >= 2
        }
      })
    }

    this.setState({ reports })
  }

  addNewSkill = (ev) => {
    ev.preventDefault()
    const skill = ev.target.name.value
    this.props.submitSkill(`${url}/mod/submitSkill?modId=${this.props.accountID}&skillName=${skill}`, { 'X-Authorization-Firebase': this.props.token})
    this.toggle()
  }

  flagUser = (messageID) => {
      this.props.flagUser(`${url}/mod/flagUser?modId=${this.props.accountID}&messageId=${messageID}`, { 'X-Authorization-Firebase': this.props.token})
      
      //Update report state
      const reports = {...this.state.reports}
      reports[messageID].flagged = !reports[messageID].flagged
      if(reports[messageID].flagged) {
        reports[messageID].counter++
      } else {
        reports[messageID].counter--
      }
      //2 is the number of flags required to suspend someone
      reports[messageID].canSuspend = reports[messageID].counter >= 2
      this.setState({ reports })
  }

  suspendUser = (messageID) => {
    this.props.suspendUser(`${url}/mod/suspendUser?modId=${this.props.accountID}&messageId=${messageID}`, { 'X-Authorization-Firebase': this.props.token})

    //Update report state
    /*const reports = {...this.state.reports}
    reports[messageID].flagged = !reports[messageID].flagged
    if(reports[messageID].flagged) {
      reports[messageID].counter++
    } else {
      reports[messageID].counter--
    }
    //2 is the number of flags required to suspend someone
    reports[messageID].canSuspend = reports[messageID].counter >= 2
    this.setState({ reports })*/
  }

  toggle = () => {
    this.setState({ modal: !this.state.modal })
  }

  renderReport = (report) => {
    return (
      <div key={report.messageID} className="report">
        <div className="reporter">
        Reporter: <strong className="link" onClick={() => this.props.goToProfile(null, report.senderID)}>{report.senderName}</strong>
        </div>
        <div className="reporter">
        Reportee: <strong className="link" onClick={() => this.props.goToProfile(null, report.topicID)}>{report.topicName}</strong>
        </div>
        <div className="reason">
        Reason: {report.content}
        </div>
        <Button type="button" color="warning" size="lg" onClick={() => this.flagUser(report.messageID)}>{report.flagged ? 'Flagged' : 'Flag Reportee'}</Button>
        {report.canSuspend && <Button type="button" color="warning" size="lg" onClick={() => this.suspendUser(report.messageID)}>{report.suspended ? 'Suspended' : 'Suspend Reportee'}</Button>}
        <i className="fa fa-times delete" onClick={() => this.props.deleteNotification(report.messageID)}></i>  
      </div>
    )
  }

  renderReportList = () => {
    const { reports } = this.state
    return (
      <div className="report-list">
        { reports
          && <ul className="reports">
              {Object.values(reports).map((report) => {
                return this.renderReport(report)
              })}
            </ul>
        }
      </div>
    )
  }

  render() {

    if(!this.props.isModerator && !this.props.accountID) {
      return (
        <div className="loader">Loading...</div>
      )
    }

    if(!this.props.isModerator && this.props.accountID) {
      return <Redirect to='/groups'/>
    }

    return (
      <div className="ModPanel">
        {this.renderReportList()}
        <Button color="primary" onClick={this.toggle}>Submit New Skill</Button>
        <Modal isOpen={this.state.modal} toggle={this.toggle} className="add-skill-modal">
          <ModalHeader toggle={this.toggle}>Submit New Skill</ModalHeader>
          <ModalBody>
           <Form className="add-skill-form" id="add-skill-form" onSubmit={this.addNewSkill}>
              <FormGroup className="required">
                <Label for="name">Skill Name</Label>
                <Input type="text" name="name" id="name" />
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" form="add-skill-form">Submit</Button>
            <Button color="secondary" onClick={this.toggle}>Close</Button>
          </ModalFooter>
        </Modal>     
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    accountID: state.user.accountID,
    token: state.auth.token,
    messages: state.messages && state.messages.data ? state.messages.data : null,
    isModerator: state.user.isMod
	}
}

const mapDispatchToProps = (dispatch) => {
	return {
    submitSkill: (url, headers) => dispatch(submitSkill(url, headers)),
    flagUser: (url, headers) => dispatch(flagUser(url, headers)),
    suspendUser: (url, headers) => dispatch(suspendUser(url, headers)),
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ModPanel)

