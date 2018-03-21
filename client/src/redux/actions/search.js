import genericDispatch from './fetch'

export function searchHasError(hasError) {
    return {
        type: 'SEARCH_HAS_ERROR',
        hasError
    }
}

export function searchIsLoading(isLoading) {
    return {
        type: 'SEARCH_IS_LOADING',
        isLoading
    }
}

export function searchDataSuccess(data) {
    return {
        type: 'SEARCH_DATA_SUCCESS',
        data
    }
}

export const search = genericDispatch(
    searchHasError, searchIsLoading, searchDataSuccess, 'GET'
)
