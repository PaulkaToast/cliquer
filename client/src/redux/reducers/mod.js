function mod(state = {}, action) {
    switch(action.type) {
        case 'FLAG_USER_HAS_ERROR':
            return Object.assign({}, state, {
                flagUserHasError: action.hasError,
            })
        case 'FLAG_USER_DATA_SUCCESS':
            return Object.assign({}, state, {
                flagUserData: action.data,
            })
        case 'FLAG_USER_IS_LOADING':
            return Object.assign({}, state, {
                flagUserIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default mod