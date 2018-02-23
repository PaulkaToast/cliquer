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

export function getProfile(url, headers) {
    return (dispatch) => {
        dispatch(getProfileIsLoading(true))
        fetch(url, { headers, method: 'GET', mode: 'cors'})
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                dispatch(getProfileIsLoading(false));
                return response;
            })
            .then((response) => response.json())
            .then((data) => {
                dispatch(getProfileDataSuccess(data))
            })
            .catch(() => dispatch(getProfileHasError(true)));
    }
}