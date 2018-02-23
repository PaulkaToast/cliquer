import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import auth from './auth'
import user from './user'
import { fetchHasError, fetchIsLoading, data as skillList} from './fetch'
import skills from './skills'
import profile from './profile'
import groups from './groups'
import friends from './friends'

const rootReducer = combineReducers({ auth, user, skillList, fetchHasError, 
                                      fetchIsLoading, skills, profile, groups,
                                      friends, router: routerReducer })

export default rootReducer