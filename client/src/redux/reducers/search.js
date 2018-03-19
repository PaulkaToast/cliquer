function search(state = {}, action) {
    switch(action.type) {
        case 'SEARCH_HAS_ERROR':
            return Object.assign({}, state, {
                hasError: action.hasError,
            })
        case 'SEARCH_DATA_SUCCESS':
            return Object.assign({}, state, {
                data: action.data,
            })
        case 'SEARCH_IS_LOADING':
            return Object.assign({}, state, {
                isLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default search