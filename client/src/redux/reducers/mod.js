function mod(state = {}, action) {
    switch(action.type) {
        case 'FLAG_USER_HAS_ERROR':
            return Object.assign({}, state, {
                flagUserHasError: action.hasError,
            })
        case 'FLAG_USER_DATA_SUCCESS':
            return Object.assign({}, state, {
                flagUserData: action.data,
            })
        case 'FLAG_USER_IS_LOADING':
            return Object.assign({}, state, {
                flagUserIsLoading: action.isLoading,
            })
        case 'REPORT_MEMBER_HAS_ERROR':
            return Object.assign({}, state, {
                reportMemberHasError: action.hasError,
            })
        case 'REPORT_MEMBER_DATA_SUCCESS':
            return Object.assign({}, state, {
                reportMemberData: action.data,
            })
        case 'REPORT_MEMBER_IS_LOADING':
            return Object.assign({}, state, {
                reportMemberIsLoading: action.isLoading,
            })
        case 'REPORT_USER_HAS_ERROR':
            return Object.assign({}, state, {
                reportUserHasError: action.hasError,
            })
        case 'REPORT_USER_DATA_SUCCESS':
            return Object.assign({}, state, {
                reportUserData: action.data,
            })
        case 'REPORT_USER_IS_LOADING':
            return Object.assign({}, state, {
                reportUserIsLoading: action.isLoading,
            })
        case 'SUBMIT_SKILL_HAS_ERROR':
            return Object.assign({}, state, {
                submitSkillHasError: action.hasError,
            })
        case 'SUBMIT_SKILL_DATA_SUCCESS':
            return Object.assign({}, state, {
                submitSkillData: action.data,
            })
        case 'SUBMIT_SKILL_IS_LOADING':
            return Object.assign({}, state, {
                submitSkillIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default mod