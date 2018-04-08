function auth(state = {}, action) {
    switch(action.type) {
        case 'LOG_IN': 
            return Object.assign({}, state, {
                loggedIn: true,
            })
        case 'LOG_OUT':
            return Object.assign({}, state, {
                loggedIn: false,
            })
        case 'SET_TOKEN':
            return Object.assign({}, state, {
                token: action.token,
            })
        case 'REGISTER_HAS_ERROR':
            return Object.assign({}, state, {
                hasError: action.hasError,
            })
        case 'REGISTER_SUCCESS':
            return Object.assign({}, state, {
                data: action.data,
            })
        case 'REGISTER_IS_LOADING':
            return Object.assign({}, state, {
                isLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default auth