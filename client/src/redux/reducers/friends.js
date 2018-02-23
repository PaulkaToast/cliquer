function friends(state = {}, action) {
    switch(action.type) {
        case 'ADD_FRIEND_HAS_ERROR':
            return Object.assign({}, state, {
                addFriendHasError: action.hasError,
            })
        case 'ADD_FRIEND_DATA_SUCCESS':
            return Object.assign({}, state, {
                addFriendData: action.data,
            })
        case 'ADD_FRIEND_IS_LOADING':
            return Object.assign({}, state, {
                addFriendIsLoading: action.isLoading,
            })
        case 'REMOVE_FRIEND_HAS_ERROR':
            return Object.assign({}, state, {
                removeFriendHasError: action.hasError,
            })
        case 'REMOVE_FRIEND_DATA_SUCCESS':
            return Object.assign({}, state, {
                removeFriendData: action.data,
            })
        case 'REMOVE_FRIEND_IS_LOADING':
            return Object.assign({}, state, {
                removeFriendIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default friends