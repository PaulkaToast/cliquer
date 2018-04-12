import genericDispatch from './fetch'

export function flagUserHasError(hasError) {
    return {
        type: 'FLAG_USER_HAS_ERROR',
        hasError
    }
}

export function flagUserIsLoading(isLoading) {
    return {
        type: 'FLAG_USER_IS_LOADING',
        isLoading
    }
}

export function flagUserDataSuccess(data) {
    return {
        type: 'FLAG_USER_DATA_SUCCESS',
        data
    }
}

export const flagUser = genericDispatch(
    flagUserHasError, flagUserIsLoading, flagUserDataSuccess, 'POST'
)