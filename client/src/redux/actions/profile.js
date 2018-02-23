function genericDispatch(hasError, isLoading, success) {
    function specificDispatch(url, headers) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method: 'GET', mode: 'cors'})
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

export function getProfileHasError(hasError) {
    return {
        type: 'GET_PROFILE_HAS_ERROR',
        hasError
    }
}

export function getProfileIsLoading(isLoading) {
    return {
        type: 'GET_PROFILE_IS_LOADING',
        isLoading
    }
}

export function getProfileDataSuccess(data) {
    return {
        type: 'GET_PROFILE_DATA_SUCCESS',
        data
    }
}

export const getProfile = genericDispatch(
    getProfileHasError, getProfileIsLoading, getProfileDataSuccess
)

export function deleteProfileHasError(hasError) {
    return {
        type: 'DELETE_PROFILE_HAS_ERROR',
        hasError
    }  
}

export function deleteProfileIsLoading(isLoading) {
    return {
        type: 'DELETE_PROFILE_IS_LOADING',
        isLoading
    }
}

export function deleteProfileDataSuccess(data) {
    return {
        type: 'DELETE_PROFILE_DATA_SUCCESS',
        data
    }
}

export const deleteProfile = genericDispatch(
    deleteProfileHasError, deleteProfileIsLoading, deleteProfileDataSuccess
)
