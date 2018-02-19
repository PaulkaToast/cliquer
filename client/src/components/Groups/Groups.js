import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route } from 'react-router'
import { NavLink } from 'react-router-dom'
import { Button, ButtonGroup } from 'reactstrap'

import '../../css/Groups.css'
import Group from './Group'
import Chat from './Chat'
import GroupMembers from './GroupMembers'
import GroupSettings from './GroupSettings'

class Groups extends Component {
  componentDidMount = () => {

  }

  renderGroupsList = () => {
    return (
      <div className="groups-list">
        <h3>Groups</h3>
        <ul id="groups">
            {this.props.groups.map((group, i) => {
              return <Group
                group={group}
                key={i}
              />
            })}
        </ul>
      </div>
    )
  }

  render() {
    return (
      <div className="Groups">
        {this.renderGroupsList()}
        <Chat />
        <div className="right-panel">
          <ButtonGroup>
            <Button onClick={() => this.props.history.push('/groups/temp')}>Members</Button>{' '}
            <Button onClick={() => this.props.history.push('/groups/temp/settings')}>Settings</Button>
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