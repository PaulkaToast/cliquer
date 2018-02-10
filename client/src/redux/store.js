import { createStore, compose, applyMiddleware } from 'redux'
import createHistory from 'history/createBrowserHistory'
import { routerMiddleware } from 'react-router-redux'
import thunk from 'redux-thunk'
import rootReducer from './reducers/index';

// Create a history of your choosing (we're using a browser history in this case)
export const history = createHistory()

const middleware = [routerMiddleware(history), thunk]
const enhancers = compose(
    window.devToolsExtension ? window.devToolsExtension() : f => f,
    applyMiddleware(...middleware)
)

const store = createStore(rootReducer, {}, enhancers);

if(module.hot) {
    module.hot.accept('./reducers/', () => {
        const nextRootReducer = require('./reducers/index').default;
        store.replaceReducer(nextRootReducer);
    })
}

export default store;