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

export function reportUserHasError(hasError) {
    return {
        type: 'REPORT_USER_HAS_ERROR',
        hasError
    }
}

export function reportUserIsLoading(isLoading) {
    return {
        type: 'REPORT_USER_IS_LOADING',
        isLoading
    }
}

export function reportUserDataSuccess(data) {
    return {
        type: 'REPORT_USER_DATA_SUCCESS',
        data
    }
}

export const reportUser = genericDispatch(
    reportUserHasError, reportUserIsLoading, reportUserDataSuccess, 'POST'
)

export function reportMemberHasError(hasError) {
    return {
        type: 'REPORT_MEMBER_HAS_ERROR',
        hasError
    }
}

export function reportMemberIsLoading(isLoading) {
    return {
        type: 'REPORT_MEMBER_IS_LOADING',
        isLoading
    }
}

export function reportMemberDataSuccess(data) {
    return {
        type: 'REPORT_MEMBER_DATA_SUCCESS',
        data
    }
}

export const reportMember = genericDispatch(
    reportMemberHasError, reportMemberIsLoading, reportMemberDataSuccess, 'POST'
)

export function submitSkillHasError(hasError) {
    return {
        type: 'SUBMIT_SKILL_HAS_ERROR',
        hasError
    }
}

export function submitSkillIsLoading(isLoading) {
    return {
        type: 'SUBMIT_SKILL_IS_LOADING',
        isLoading
    }
}

export function submitSkillDataSuccess(data) {
    return {
        type: 'SUBMIT_SKILL_DATA_SUCCESS',
        data
    }
}

export const submitSkill = genericDispatch(
    submitSkillHasError, submitSkillIsLoading, submitSkillDataSuccess, 'POST'
)