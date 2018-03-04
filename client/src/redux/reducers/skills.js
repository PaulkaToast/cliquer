function skills(state = {}, action) {
    switch(action.type) {
        case 'POST_SKILL_HAS_ERROR':
            return Object.assign({}, state, {
                postHasError: action.hasError,
            })
        case 'POST_SKILL_DATA_SUCCESS':
            return Object.assign({}, state, {
                postData: action.data,
            })
        case 'POST_SKILL_IS_LOADING':
            return Object.assign({}, state, {
                getIsLoading: action.isLoading,
            })
        case 'GET_SKILLS_HAS_ERROR':
            return Object.assign({}, state, {
                getHasError: action.hasError,
            })
        case 'GET_SKILLS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getData: action.data,
            })
        case 'GET_SKILLS_IS_LOADING':
            return Object.assign({}, state, {
                getIsLoading: action.isLoading,
            })
        case 'REMOVE_SKILL_HAS_ERROR':
            return Object.assign({}, state, {
                removeHasError: action.hasError,
            })
        case 'REMOVE_SKILL_DATA_SUCCESS':
            return Object.assign({}, state, {
                removeData: action.data,
            })
        case 'REMOVE_SKILL_IS_LOADING':
            return Object.assign({}, state, {
                removeIsLoading: action.isLoading,
            })
        case 'GET_SKILLS_LIST_HAS_ERROR':
            return Object.assign({}, state, {
                getListHasError: action.hasError,
            })
        case 'GET_SKILLS_LIST_DATA_SUCCESS':
            return Object.assign({}, state, {
                getListData: action.data,
            })
        case 'GET_SKILLS_LIST_IS_LOADING':
            return Object.assign({}, state, {
                getListIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default skills