import genericDispatch from './fetch'

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

export function deleteGroupDataSuccess(data, gid) {
    return {
        type: 'DELETE_GROUP_DATA_SUCCESS',
        data,
        gid,
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

export function leaveGroupDataSuccess(data, gid) {
    return {
        type: 'LEAVE_GROUP_DATA_SUCCESS',
        data,
        gid,
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

export function setCurrentGroup(group) {
    return {
        type: 'SET_CURRENT_GROUP',
        group
    }
}

export function addCurrentGroupMember(member) {
    return {
        type: 'ADD_CURRENT_GROUP_MEMBER',
        member
    }
}

export function removeCurrentGroupMember(memberID) {
    return {
        type: 'REMOVE_CURRENT_GROUP_MEMBER',
        memberID
    }
}

export function updateChatLog(message) {
    return {
        type: 'UPDATE_CHAT_LOG',
        message
    }
}

export function getChatLogHasError(hasError) {
    return {
        type: 'GET_CHAT_LOG_ERROR',
        hasError
    }  
}

export function getChatLogIsLoading(isLoading) {
    return {
        type: 'GET_CHAT_LOG_IS_LOADING',
        isLoading
    }
}

export function getChatLogDataSuccess(data) {
    return {
        type: 'GET_CHAT_LOG_DATA_SUCCESS',
        data
    }
}

export const getChatLog = genericDispatch(
    getChatLogHasError, getChatLogIsLoading, getChatLogDataSuccess, 'GET'
)

export function postChatMessageHasError(hasError) {
    return {
        type: 'POST_CHAT_MESSAGE_ERROR',
        hasError
    }  
}

export function postChatMessageIsLoading(isLoading) {
    return {
        type: 'POST_CHAT_MESSAGE_IS_LOADING',
        isLoading
    }
}

export function postChatMessageDataSuccess(data) {
    return {
        type: 'POST_CHAT_MESSAGE_DATA_SUCCESS',
        data
    }
}

export const postChatMessage = genericDispatch(
    postChatMessageHasError, postChatMessageIsLoading, postChatMessageDataSuccess, 'POST'
)

export function setGroupSettingsHasError(hasError) {
    return {
        type: 'SET_GROUP_SETTINGS_HAS_ERROR',
        hasError
    }  
}

export function setGroupSettingsIsLoading(isLoading) {
    return {
        type: 'SET_GROUP_SETTINGS_IS_LOADING',
        isLoading
    }
}

export function setGroupSettingsDataSuccess(data) {
    return {
        type: 'SET_GROUP_SETTINGS_DATA_SUCCESS',
        data
    }
}

export const setGroupSettings = genericDispatch(
    setGroupSettingsHasError, setGroupSettingsIsLoading, setGroupSettingsDataSuccess, 'POST'
)

export function kickHasError(hasError) {
    return {
        type: 'KICK_HAS_ERROR',
        hasError
    }  
}

export function kickIsLoading(isLoading) {
    return {
        type: 'KICK_IS_LOADING',
        isLoading
    }
}

export function kickDataSuccess(data, extra) {
    //TODO: fick visual issue with member still displaying
    return {
        type: 'KICK_DATA_SUCCESS',
        data,
        extra
    }
}

export const kick = genericDispatch(
    kickHasError, kickIsLoading, kickDataSuccess, 'POST'
)

export function getRateFormHasError(hasError) {
    return {
        type: 'GET_RATE_FORM_ERROR',
        hasError
    }  
}

export function getRateFormIsLoading(isLoading) {
    return {
        type: 'GET_RATE_FORM_IS_LOADING',
        isLoading
    }
}

export function getRateFormDataSuccess(data) {
    return {
        type: 'GET_RATE_FORM_DATA_SUCCESS',
        data
    }
}

export const getRateForm = genericDispatch(
    getRateFormHasError, getRateFormIsLoading, getRateFormDataSuccess, 'GET'
)

export function postRateFormHasError(hasError) {
    return {
        type: 'POST_RATE_FORM_ERROR',
        hasError
    }  
}

export function postRateFormIsLoading(isLoading) {
    return {
        type: 'POST_RATE_FORM_IS_LOADING',
        isLoading
    }
}

export function postRateFormDataSuccess(data) {
    return {
        type: 'POST_RATE_FORM_DATA_SUCCESS',
        data
    }
}

export const postRateForm = genericDispatch(
    postRateFormHasError, postRateFormIsLoading, postRateFormDataSuccess, 'POST'
)

