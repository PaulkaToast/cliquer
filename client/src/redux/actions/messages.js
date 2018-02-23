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