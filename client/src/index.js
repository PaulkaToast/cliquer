import React from 'react';
import ReactDOM from 'react-dom';
import { HashRouter as Router, Route } from 'react-router-dom'
import './css/index.css';
import App from './js/App';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(
<Router>
    <Route component={App} />
</Router>, 
document.getElementById('root'));
registerServiceWorker();
