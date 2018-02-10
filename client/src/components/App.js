import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router'
import {connect} from 'react-redux';

import '../css/App.css';
import Login from './Login'
import Register from './Register'
import Main from './Main'

class App extends Component {

  loggedIn = () => {
    return this.props.loggedIn
  }
  
  render() {
    return (
      <div className="App">
        <Switch>
          <Route path="/login" render={(navProps) => 
            !this.loggedIn() 
            ? <Login {...navProps} />
            : <Redirect to="/groups"/>
          }/>
          <Route path="/register" render={(navProps) =>
            !this.loggedIn() 
            ? <Register {...navProps} />
            : <Redirect to="/groups"/>
          }/>
          <Route path="/" render={(navProps) =>
            this.loggedIn() 
            ? <Main 
                {...this.props}
              />
            : <Redirect to="/login" />
          }/>
        </Switch>
      </div>
    );
  }
}


const mapStateToProps = (state) => {
	return {
    user: state.auth.user,
    loggedIn: state.auth.loggedIn
	};
};

export default connect(mapStateToProps)(App);

