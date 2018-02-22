import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route } from 'react-router'
import { NavLink } from 'react-router-dom'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/Groups.css'
import { history } from '../../redux/store'
import Group from './Group'
import Chat from './Chat'
import GroupMembers from './GroupMembers'
import GroupSettings from './GroupSettings'

class Groups extends Component {
  constructor(props) {
    super(props)
    this.state = {
      members: 'active',
      settings: '',
    }
  }

  renderGroupsList = () => {
    const { groups } = this.props
    return (
      <div className="groups-list">
        <div className="header">
          <h3>Groups</h3>
        </div>
        <ul id="groups">
            {Object.keys(groups).map((gid, i) => {
              return <Group
                group={groups[gid]}
                key={i}
              />
            })}
        </ul>
      </div>
    )
  }

  toggle = (tab) => {
    if(tab === 'members' && this.state.members !== 'active' ) {
      this.setState({ members: 'active', settings: '' }, () => history.push('/groups/temp'))
    }
    if(tab === 'settings' && this.state.settings !== 'active' ) {
      this.setState({ members: '', settings: 'active' }, () => history.push('/groups/temp/settings'))
    }
  }

  render() {
    return (
      <div className="Groups">
        {this.renderGroupsList()}
        <Chat />
        <div className="right-panel">
          <ButtonGroup>
            <Button className={`${this.state.members} nav-button`} onClick={() => this.toggle('members')}><h3>Members</h3></Button>{' '}
            <Button className={`${this.state.settings} nav-button`} onClick={() => this.toggle('settings')}><h3>Settings</h3></Button>
          </ButtonGroup>
          <Switch>
            <Route exact path="/groups/:gid" render={(navProps) => <GroupMembers {...this.props} {...navProps}/>}/>
            <Route path="/groups/:gid/settings" render={(navProps) => <GroupSettings {...this.props} {...navProps}/>}/>
          </Switch>
        </div>
      </div>
    )
  }
}

const mapStateToProps = (state) => {
	return {
    user: state.user.data,
    /*groups: state.user.groups,*/
	}
}

export default connect(mapStateToProps )(Groups)