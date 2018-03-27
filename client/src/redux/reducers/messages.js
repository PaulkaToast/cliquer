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
        case 'LOAD_NOTIFICATIONS': 
            return Object.assign({}, state, {
                data: action.notifications
            })
        case 'HANDLE_NOTIFICATIONS_HAS_ERROR':
            return Object.assign({}, state, {
                handleNotificationsHasError: action.hasError,
            })
        case 'HANDLE_NOTIFICATIONS_DATA_SUCCESS':
            return Object.assign({}, state, {
                handleNotificationsData: action.data,
            })
        case 'HANDLE_NOTIFICATIONS_IS_LOADING':
            return Object.assign({}, state, {
                handleNotificationsIsLoading: action.isLoading,
            })
        case 'DELETE_NOTIFICATION': 
            let messagesCopy = state.data ? {...state.data} : null
            if(messagesCopy) {
                delete messagesCopy[action.messageID]
            }
            return Object.assign({}, state, {
                data: messagesCopy
            })
        default:
            return state
    }
}

export default messages