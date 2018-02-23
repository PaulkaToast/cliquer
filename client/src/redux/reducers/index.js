import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import auth from './auth'
import user from './user'
import { fetchHasError, fetchIsLoading, data} from './fetch'
import skills from './skills'

const rootReducer = combineReducers({ auth, user, data, fetchHasError, skills, fetchIsLoading, router: routerReducer })

export default rootReducer