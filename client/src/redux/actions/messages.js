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

export const getMessages = genericDispatch(
    getMessagesHasError, getMessagesIsLoading, getMessagesDataSuccess, 'GET'
)