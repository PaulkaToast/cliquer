import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import auth from './auth'
import user from './user'
import skills from './skills'
import profile from './profile'
import groups from './groups'
import friends from './friends'
import messages from './messages'
import search from './search'
import mod from './mod'

const rootReducer = combineReducers({ auth, user, skills, profile, groups,
                                      friends, messages, search, mod,
                                      router: routerReducer })

export default rootReducer