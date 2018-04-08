import genericDispatch from './fetch'

export function addFriendHasError(hasError) {
    return {
        type: 'ADD_FRIEND_HAS_ERROR',
        hasError
    }
}

export function addFriendIsLoading(isLoading) {
    return {
        type: 'ADD_FRIEND_IS_LOADING',
        isLoading
    }
}

export function addFriendDataSuccess(data) {
    return {
        type: 'ADD_FRIEND_DATA_SUCCESS',
        data
    }
}

export const addFriend = genericDispatch(
    addFriendHasError, addFriendIsLoading, addFriendDataSuccess, 'POST'
)

export function removeFriendHasError(hasError) {
    return {
        type: 'REMOVE_FRIEND_HAS_ERROR',
        hasError
    }  
}

export function removeFriendIsLoading(isLoading) {
    return {
        type: 'REMOVE_FRIEND_IS_LOADING',
        isLoading
    }
}

export function removeFriendDataSuccess(data) {
    return {
        type: 'REMOVE_FRIEND_DATA_SUCCESS',
        data
    }
}

export const removeFriend = genericDispatch(
    removeFriendHasError, removeFriendIsLoading, removeFriendDataSuccess, 'POST'
)

export function requestFriendHasError(hasError) {
    return {
        type: 'REQUEST_FRIEND_HAS_ERROR',
        hasError
    }
}

export function requestFriendIsLoading(isLoading) {
    return {
        type: 'REQUEST_FRIEND_IS_LOADING',
        isLoading
    }
}

export function requestFriendDataSuccess(data) {
    return {
        type: 'REQUEST_FRIEND_DATA_SUCCESS',
        data
    }
}

export const requestFriend = genericDispatch(
    requestFriendHasError, requestFriendIsLoading, requestFriendDataSuccess, 'POST'
)