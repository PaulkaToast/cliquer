//TODO: rename to notifications
function messages(state = {}, action) {
    switch(action.type) {
        case 'GET_MESSAGES_HAS_ERROR':
            return Object.assign({}, state, {
                getMessagesHasError: action.hasError,
            })
        case 'GET_MESSAGES_DATA_SUCCESS':
            return Object.assign({}, state, {
                getMessagesData: action.data,
            })
        case 'GET_MESSAGES_IS_LOADING':
            return Object.assign({}, state, {
                getMessagesIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default messages