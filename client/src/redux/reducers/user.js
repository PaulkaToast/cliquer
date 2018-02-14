function user(state = {}, action) {
    switch(action.type) {
        case 'LOG_IN': 
            return Object.assign({}, state, {
                data: action.user,
            })
        case 'LOG_OUT':
            return Object.assign({}, state, {
                data: null,
            })
        case 'UPDATE_USER':
            return Object.assign({}, state, {
                data: action.data,
            })
        default:
            return state
    }
}

export default user