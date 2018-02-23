function skills(state = {}, action) {
    switch(action.type) {
        case 'GET_PROFILE_HAS_ERROR':
            return Object.assign({}, state, {
                getHasError: action.hasError,
            })
        case 'GET_PROFILE_DATA_SUCCESS':
            return Object.assign({}, state, {
                getData: action.data,
            })
        case 'GET_PROFILE_IS_LOADING':
            return Object.assign({}, state, {
                getIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default skills