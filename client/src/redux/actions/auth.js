export function logIn(user) {
    return {
        type: 'LOG_IN',
        user
    }
}

export function logOut() {
    return {
        type: 'LOG_OUT'
    }
}

export function setToken(token) {
    return {
        type: 'SET_TOKEN',
        token
    }
}


export function registerHasError(hasError) {
    return {
        type: 'REGISTER_HAS_ERROR',
        hasError
    }
}

export function registerIsLoading(isLoading) {
    return {
        type: 'REGISTER_IS_LOADING',
        isLoading
    }
}

export function registerSuccess(data) {
    return {
        type: 'REGISTER_SUCCESS',
        data
    }
}

export function registerUser(url, headers) {
    return (dispatch) => {
        dispatch(registerIsLoading(true))
        fetch(url, { headers, method: 'POST', mode: 'cors' })
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText)
                }
                console.log(response)
                dispatch(registerIsLoading(false))

                return response
            })
            .then((response) => response.json())
            .then((data) => {
                dispatch(registerSuccess(data))
            })
            .catch(() => dispatch(registerHasError(true)))
    }
}