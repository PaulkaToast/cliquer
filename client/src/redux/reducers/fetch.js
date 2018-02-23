export function fetchHasError(state = false, action) {
    switch (action.type) {
        case 'FETCH_HAS_ERROR':
            return action.hasError
        default:
            return state
    }
}

export function fetchIsLoading(state = false, action) {
    switch (action.type) {
        case 'FETCH_IS_LOADING':
            return action.isLoading
        default:
            return state
    }
}

export function data(state = [], action) {
    switch (action.type) {
        case 'FETCH_DATA_SUCCESS':
            return action.data;
        default:
            return state;
    }
}