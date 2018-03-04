import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import auth from './auth'
import user from './user'
import skills from './skills'
import profile from './profile'
import groups from './groups'
import friends from './friends'
import messages from './messages'

const rootReducer = combineReducers({ auth, user, skills, profile, groups,
                                      friends, messages, router: routerReducer })

export default rootReducer