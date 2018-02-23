import React, { Component } from 'react'
import { Switch, Route, Redirect } from 'react-router'
import '../css/Main.css'

import Navbar from './Navbar'
import CreateGroup from './CreateGroup'
import Groups from './Groups/Groups'
import PublicGroups from './PublicGroups'
import Profile from './Profile/Profile'
import Settings from './Settings'
import SearchResults from './SearchResults'

class Main extends Component {

  render() {
    return (
      <div className="Main h-100">
        <Navbar {...this.props} />
        <Switch>
            <Route path="/create" render={(navProps) => <CreateGroup {...navProps} />}/>
            <Route path="/groups" render={(navProps) => <Groups {...navProps} />}/>
            <Route path="/public" render={(navProps) => <PublicGroups {...navProps} />}/>
            <Route path="/profile" render={(navProps) => <Profile {...navProps} />}/>
            <Route path="/settings" render={(navProps) => <Settings {...navProps} />}/>
            <Route path="/search/:category/:query" render={(navProps) => <SearchResults {...navProps} {...this.props} results={[{name: 'Jordan', reputation: '42'}, {name: 'Shawn', reputation: '42'}, {name: 'Max', reputation: '42'}]} />}/>
            <Route path='/' render={(navProps) => <Redirect to="/groups" />}/>
        </Switch>
      </div>
    )
  }
}

export default Main
