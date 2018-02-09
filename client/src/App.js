import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router-dom'
import '../css/App.css';

import Login from './Login'
import Main from './Main'

class App extends Component {

  loggedIn = () => {
    return true
  }
  
  render() {
    return (
      <div className="App">
        <Switch>
          <Route path="/groups" render={() =>
            this.loggedIn() 
            ? <Main/>
            : <Redirect to="/login" />
          }/>
          <Route path="/login" render={() => 
            !this.loggedIn() 
            ? <LogIn />
            : <Redirect to="/groups"/>
          }/>
          <Route path="/" render={() => 
            !this.loggedIn() 
            ? <LogIn />
            : <Redirect to="/groups"/>
          }/>
        </Switch>
      </div>
    );
  }
}

export default App;
