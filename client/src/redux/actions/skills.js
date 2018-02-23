export function postSkillHasError(hasError) {
    return {
        type: 'POST_SKILL_HAS_ERROR',
        hasError
    }
}

export function postSkillIsLoading(isLoading) {
    return {
        type: 'POST_SKILL_IS_LOADING',
        isLoading
    }
}

export function postSkillDataSuccess(data) {
    return {
        type: 'POST_SKILL_DATA_SUCCESS',
        data
    }
}

export function postSkill(url, headers) {
    return (dispatch) => {
        dispatch(postSkillIsLoading(true))
        fetch(url, { headers, method: 'POST', mode: 'cors'})
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                dispatch(postSkillIsLoading(false));
                return response;
            })
            .then((response) => response.json())
            .then((data) => {
                dispatch(postSkillDataSuccess(data))
            })
            .catch(() => dispatch(postSkillHasError(true)));
    }
}

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

export function getSkills(url, headers) {
    return (dispatch) => {
        dispatch(getSkillsIsLoading(true))
        fetch(url, { headers, method: 'GET', mode: 'cors'})
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                dispatch(getSkillsIsLoading(false));
                return response;
            })
            .then((response) => response.json())
            .then((data) => {
                dispatch(getSkillsDataSuccess(data))
            })
            .catch(() => dispatch(getSkillsHasError(true)));
    }
}

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

export function removeSkillDataSuccess(data) {
    return {
        type: 'REMOVE_SKILL_DATA_SUCCESS',
        data
    }
}

export function removeSkill(url, headers) {
    return (dispatch) => {
        dispatch(removeSkillIsLoading(true))
        fetch(url, { headers, method: 'POST', mode: 'cors'})
            .then((response) => {
                if (!response.ok) {
                    throw Error(response.statusText);
                }
                dispatch(removeSkillIsLoading(false));
                return response;
            })
            .then((response) => response.json())
            .then((data) => {
                dispatch(removeSkillDataSuccess(data))
            })
            .catch(() => dispatch(removeSkillHasError(true)));
    }
}