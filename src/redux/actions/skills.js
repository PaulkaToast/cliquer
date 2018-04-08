import genericDispatch from './fetch'

export function postSkillsHasError(hasError) {
    return {
        type: 'POST_SKILLS_HAS_ERROR',
        hasError
    }
}

export function postSkillsIsLoading(isLoading) {
    return {
        type: 'POST_SKILLS_IS_LOADING',
        isLoading
    }
}

export function postSkillsDataSuccess(data) {
    return {
        type: 'POST_SKILLS_DATA_SUCCESS',
        data
    }
}

export const postSkills = genericDispatch(
    postSkillsHasError, postSkillsIsLoading, postSkillsDataSuccess, 'POST'
)

export function getSkillsHasError(hasError) {
    return {
        type: 'GET_SKILLS_HAS_ERROR',
        hasError
    }
}

export function getSkillsIsLoading(isLoading) {
    return {
        type: 'GET_SKILLS_IS_LOADING',
        isLoading
    }
}

export function getSkillsDataSuccess(data) {
    return {
        type: 'GET_SKILLS_DATA_SUCCESS',
        data
    }
}

export const getSkills = genericDispatch(
    getSkillsHasError, getSkillsIsLoading, getSkillsDataSuccess, 'GET'
)

export function removeSkillHasError(hasError) {
    return {
        type: 'REMOVE_SKILL_HAS_ERROR',
        hasError
    }
}

export function removeSkillIsLoading(isLoading) {
    return {
        type: 'REMOVE_SKILL_IS_LOADING',
        isLoading
    }
}

export function removeSkillDataSuccess(data, index) {
    return {
        type: 'REMOVE_SKILL_DATA_SUCCESS',
        data,
        index
    }
}

export const removeSkill = genericDispatch(
    removeSkillHasError, removeSkillIsLoading, removeSkillDataSuccess, 'POST'
)

export function getSkillsListHasError(hasError) {
    return {
        type: 'GET_SKILLS_LIST_HAS_ERROR',
        hasError
    }
}

export function getSkillsListIsLoading(isLoading) {
    return {
        type: 'GET_SKILLS_LIST_IS_LOADING',
        isLoading
    }
}

export function getSkillsListDataSuccess(data) {
    return {
        type: 'GET_SKILLS_LIST_DATA_SUCCESS',
        data
    }
}

export const getSkillsList = genericDispatch(
    getSkillsListHasError, getSkillsListIsLoading, getSkillsListDataSuccess, 'GET'
)
