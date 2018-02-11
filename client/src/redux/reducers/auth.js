function auth(state = {}, action) {
    switch(action.type) {
        case 'LOG_IN': 
            return Object.assign({}, state, {
                user: action.user,
                loggedIn: true,
            })
        case 'LOG_OUT':
            return Object.assign({}, state, {
                user: null,
                loggedIn: false,
            })
        default:
            return state
    }
}

export default auth