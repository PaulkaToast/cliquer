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
        case 'SUSPEND_USER_HAS_ERROR':
            return Object.assign({}, state, {
                suspendUserHasError: action.hasError,
            })
        case 'SUSPEND_USER_DATA_SUCCESS':
            return Object.assign({}, state, {
                suspendUserData: action.data,
            })
        case 'SUSPEND_USER_IS_LOADING':
            return Object.assign({}, state, {
                suspendUserIsLoading: action.isLoading,
            })
        case 'APPLY_FOR_MOD_HAS_ERROR':
            return Object.assign({}, state, {
                applyForModHasError: action.hasError,
            })
        case 'APPLY_FOR_MOD_DATA_SUCCESS':
            return Object.assign({}, state, {
                applyForModData: action.data,
            })
        case 'APPLY_FOR_MOD_IS_LOADING':
            return Object.assign({}, state, {
                applyForModIsLoading: action.isLoading,
            })    
        default:
            return state
    }
}

export default mod