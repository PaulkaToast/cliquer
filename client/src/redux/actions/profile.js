import genericDispatch from './fetch'

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
    getProfileHasError, getProfileIsLoading, getProfileDataSuccess, 'GET'
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
    deleteProfileHasError, deleteProfileIsLoading, deleteProfileDataSuccess, 'POST'
)
