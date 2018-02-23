export function fetchHasError(hasError) {
    return {
        type: 'FETCH_HAS_ERROR',
        hasError
    }
}

export function fetchIsLoading(isLoading) {
    return {
        type: 'FETCH_IS_LOADING',
        isLoading
    }
}

export function fetchDataSuccess(data) {
    return {
        type: 'FETCH_DATA_SUCCESS',
        data
    }
}

export function errorAfterFiveSeconds() {
    // We return a function instead of an action object
    return (dispatch) => {
        setTimeout(() => {
            // This function is able to dispatch other action creators
            dispatch(fetchHasError(true))
        }, 5000)
    }
}

export function fetchData(url, headers) {
    return (dispatch) => {
        dispatch(fetchIsLoading(true))
        fetch(url, { headers, method: 'GET', mode: 'cors' })
            .then((response) => {
                console.log('hello')
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                console.log(response)
                dispatch(fetchIsLoading(false));

                return response;
            })
            .then((response) => response.json())
            .then((data) => {
                console.log(data)
                dispatch(fetchDataSuccess(data))
            })
            .catch(() => dispatch(fetchHasError(true)));
    }
}