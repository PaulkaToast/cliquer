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

export function setSettingsHasError(hasError) {
    return {
        type: 'SET_SETTINGS_HAS_ERROR',
        hasError
    }  
}

export function setSettingsIsLoading(isLoading) {
    return {
        type: 'SET_SETTINGS_IS_LOADING',
        isLoading
    }
}

export function setSettingsDataSuccess(data) {
    return {
        type: 'SET_SETTINGS_DATA_SUCCESS',
        data
    }
}

export const setSettings = genericDispatch(
    setSettingsHasError, setSettingsIsLoading, setSettingsDataSuccess, 'POST'
)

export function clearProfile() {
    return {
        type: 'CLEAR_PROFILE'
    }
}

