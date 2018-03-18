function profile(state = {}, action) {
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
        case 'DELETE_PROFILE_HAS_ERROR':
            return Object.assign({}, state, {
                deleteHasError: action.hasError,
            })
        case 'DELETE_PROFILE_DATA_SUCCESS':
            return Object.assign({}, state, {
                deleteData: action.data,
            })
        case 'DELETE_PROFILE_IS_LOADING':
            return Object.assign({}, state, {
                deleteIsLoading: action.isLoading,
            })
        case 'SET_SETTINGS_HAS_ERROR':
            return Object.assign({}, state, {
                setSettingsHasError: action.hasError,
            })
        case 'SET_SETTINGS_DATA_SUCCESS':
            return Object.assign({}, state, {
                setSettingsData: action.data,
            })
        case 'SET_SETTINGS_IS_LOADING':
            return Object.assign({}, state, {
                setSettingsIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default profile