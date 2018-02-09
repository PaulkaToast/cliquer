import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router-dom'
import '../css/Main.css'

import Navbar from './Navbar'
import CreateGroup from './CreateGroup'
import Groups from './Groups'
import PublicGroups from './PublicGroups'
import Profile from './Profile'
import Settings from './Settings'

class Main extends Component {

  render() {
    return (
      <div className="Main">
        <Navbar {...this.props} />
        <Switch>
            <Route path="/create" render={(navProps) => <CreateGroup {...navProps} />}/>
            <Route path="/groups" render={(navProps) => <Groups {...navProps} />}/>
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} />}/>
            <Route path="/profile" render={(navProps) => <Profile {...navProps} />}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>
      </div>
    );
  }
}

export default Main;
