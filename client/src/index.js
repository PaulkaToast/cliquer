import React from 'react'
import ReactDOM from 'react-dom'

import { Route } from 'react-router'
import { ConnectedRouter as Router } from 'react-router-redux'
import { Provider } from 'react-redux'

import './css/dist/bootstrap/bootstrap.css';
import './css/index.css'
import App from './components/App'
import registerServiceWorker from './registerServiceWorker'

import store, { history } from './redux/store'

ReactDOM.render(
<Provider store={store}>
    <Router history={history}>
        <Route component={App} />
    </Router>
</Provider>, 
document.getElementById('root'))
registerServiceWorker()
