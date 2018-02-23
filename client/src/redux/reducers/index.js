import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import auth from './auth'
import user from './user'
import { fetchHasError, fetchIsLoading, data} from './fetch'

const rootReducer = combineReducers({ auth, user, data, fetchHasError, fetchIsLoading, router: routerReducer })

export default rootReducer