function genericDispatch(hasError, isLoading, success, method) {
    function specificDispatch(url, headers) {
        return (dispatch) => {
            dispatch(isLoading(true))
            fetch(url, { headers, method, mode: 'cors'})
                .then((response) => {
                    if (!response.ok) {
                        throw Error(response.statusText);
                    }
                    dispatch(isLoading(false));
                    return response;
                })
                .then((response) => response.json())
                .then((data) => {
                    dispatch(success(data))
                })
                .catch(() => dispatch(hasError(true)));
        }
    }
    return specificDispatch;
}

export function getGroupsHasError(hasError) {
    return {
        type: 'GET_GROUPS_HAS_ERROR',
        hasError
    }
}

export function getGroupsIsLoading(isLoading) {
    return {
        type: 'GET_GROUPS_IS_LOADING',
        isLoading
    }
}

export function getGroupsDataSuccess(data) {
    return {
        type: 'GET_GROUPS_DATA_SUCCESS',
        data
    }
}

export const getGroups = genericDispatch(
    getGroupsHasError, getGroupsIsLoading, getGroupsDataSuccess, 'GET'
)

export function groupInviteHasError(hasError) {
    return {
        type: 'GROUP_INVITE_HAS_ERROR',
        hasError
    }  
}

export function groupInviteIsLoading(isLoading) {
    return {
        type: 'GROUP_INVITE_IS_LOADING',
        isLoading
    }
}

export function groupInviteDataSuccess(data) {
    return {
        type: 'GROUP_INVITE_DATA_SUCCESS',
        data
    }
}

export const groupInvite = genericDispatch(
    groupInviteHasError, groupInviteIsLoading, groupInviteDataSuccess, 'POST'
)

export function joinGroupHasError(hasError) {
    return {
        type: 'JOIN_GROUP_HAS_ERROR',
        hasError
    }  
}

export function joinGroupIsLoading(isLoading) {
    return {
        type: 'JOIN_GROUP_IS_LOADING',
        isLoading
    }
}

export function joinGroupDataSuccess(data) {
    return {
        type: 'JOIN_GROUP_DATA_SUCCESS',
        data
    }
}

export const joinGroup = genericDispatch(
    joinGroupHasError, joinGroupIsLoading, joinGroupDataSuccess, 'POST'
)

export function deleteGroupHasError(hasError) {
    return {
        type: 'DELETE_GROUP_HAS_ERROR',
        hasError
    }  
}

export function deleteGroupIsLoading(isLoading) {
    return {
        type: 'DELETE_GROUP_IS_LOADING',
        isLoading
    }
}

export function deleteGroupDataSuccess(data) {
    return {
        type: 'DELETE_GROUP_DATA_SUCCESS',
        data
    }
}

export const deleteGroup = genericDispatch(
    deleteGroupHasError, deleteGroupIsLoading, deleteGroupDataSuccess, 'POST'
)

export function leaveGroupHasError(hasError) {
    return {
        type: 'LEAVE_GROUP_HAS_ERROR',
        hasError
    }  
}

export function leaveGroupIsLoading(isLoading) {
    return {
        type: 'LEAVE_GROUP_IS_LOADING',
        isLoading
    }
}

export function leaveGroupDataSuccess(data) {
    return {
        type: 'LEAVE_GROUP_DATA_SUCCESS',
        data
    }
}

export const leaveGroup = genericDispatch(
    leaveGroupHasError, leaveGroupIsLoading, leaveGroupDataSuccess, 'POST'
)

export function createGroupHasError(hasError) {
    return {
        type: 'CREATE_GROUP_HAS_ERROR',
        hasError
    }  
}

export function createGroupIsLoading(isLoading) {
    return {
        type: 'CREATE_GROUP_IS_LOADING',
        isLoading
    }
}

export function createGroupDataSuccess(data) {
    return {
        type: 'CREATE_GROUP_DATA_SUCCESS',
        data
    }
}

export const createGroup = genericDispatch(
    createGroupHasError, createGroupIsLoading, createGroupDataSuccess, 'POST'
)




