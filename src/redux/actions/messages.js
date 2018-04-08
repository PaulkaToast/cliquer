import genericDispatch from './fetch'

export function getMessagesHasError(hasError) {
    return {
        type: 'GET_MESSAGES_HAS_ERROR',
        hasError
    }
}

export function getMessagesIsLoading(isLoading) {
    return {
        type: 'GET_MESSAGES_IS_LOADING',
        isLoading
    }
}

export function getMessagesDataSuccess(data) {
    return {
        type: 'GET_MESSAGES_DATA_SUCCESS',
        data
    }
}

export function loadNotifications(notifications) {
    return {
        type: 'LOAD_NOTIFICATIONS',
        notifications
    }
}

export const getMessages = genericDispatch(
    getMessagesHasError, getMessagesIsLoading, getMessagesDataSuccess, 'GET'
)

export function deleteNotification(messageID) {
    return {
        type: 'DELETE_NOTIFICATION',
        messageID
    }
}

export function handleNotificationsHasError(hasError) {
    return {
        type: 'HANDLE_NOTIFICATIONS_HAS_ERROR',
        hasError
    }
}

export function handleNotificationsIsLoading(isLoading) {
    return {
        type: 'HANDLE_NOTIFICATIONS_IS_LOADING',
        isLoading
    }
}

export function handleNotificationsDataSuccess(data) {
    return {
        type: 'HANDLE_NOTIFICATIONS_DATA_SUCCESS',
        data
    }
}

export const handleNotifications = genericDispatch(
    handleNotificationsHasError, handleNotificationsIsLoading, handleNotificationsDataSuccess, 'POST'
)