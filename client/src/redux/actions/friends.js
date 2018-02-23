//TODO: make even more generic (for all actions)

function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method, mode: 'cors'})
                .then((response) => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    dispatch(isLoading(false));
                    return response;
                })
                .then((response) => response.json())
                .then((data) => {
                    dispatch(success(data))
                })
                .catch(() => dispatch(hasError(true)));
        }
    }
    return specificDispatch;
}

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