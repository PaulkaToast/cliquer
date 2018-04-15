function groups(state = {}, action) {
    let groupsCopy
    let members
    switch(action.type) {
        case 'GET_GROUPS_HAS_ERROR':
            return Object.assign({}, state, {
                getGroupsHasError: action.hasError,
            })
        case 'GET_GROUPS_DATA_SUCCESS':
            return Object.assign({}, state, {
                getGroupsData: action.data,
            })
        case 'GET_GROUPS_IS_LOADING':
            return Object.assign({}, state, {
                getGroupsIsLoading: action.isLoading,
            })
        case 'GROUP_INVITE_HAS_ERROR':
            return Object.assign({}, state, {
                groupInviteHasError: action.hasError,
            })
        case 'GROUP_INVITE_DATA_SUCCESS':
            return Object.assign({}, state, {
                groupInviteData: action.data,
            })
        case 'GROUP_INVITE_IS_LOADING':
            return Object.assign({}, state, {
                groupInviteIsLoading: action.isLoading,
            })
        case 'JOIN_GROUP_HAS_ERROR':
            return Object.assign({}, state, {
                joinGroupHasError: action.hasError,
            })
        case 'JOIN_GROUP_DATA_SUCCESS':
            return Object.assign({}, state, {
                joinGroupData: action.data,
            })
        case 'JOIN_GROUP_IS_LOADING':
            return Object.assign({}, state, {
                joinGroupIsLoading: action.isLoading,
            })
        case 'DELETE_GROUP_HAS_ERROR':
            return Object.assign({}, state, {
                deleteGroupHasError: action.hasError,
            })
        case 'DELETE_GROUP_DATA_SUCCESS':
            groupsCopy = state.getGroupsData ? {...state.getGroupsData} : {}
            if(groupsCopy[action.gid]) delete groupsCopy[action.gid]
            return Object.assign({}, state, {
                deleteGroupData: action.data,
                getGroupsData: groupsCopy
            })
        case 'DELETE_GROUP_IS_LOADING':
            return Object.assign({}, state, {
                deleteGroupIsLoading: action.isLoading,
            })
        case 'LEAVE_GROUP_HAS_ERROR':
            return Object.assign({}, state, {
                leaveGroupHasError: action.hasError,
            })
        case 'LEAVE_GROUP_DATA_SUCCESS':
            groupsCopy = state.getGroupsData ? {...state.getGroupsData} : {}
            if(groupsCopy[action.gid]) delete groupsCopy[action.gid]
            return Object.assign({}, state, {
                leaveGroupData: action.data,
                getGroupsData: groupsCopy
            })
        case 'LEAVE_GROUP_IS_LOADING':
            return Object.assign({}, state, {
                leaveGroupIsLoading: action.isLoading,
            })
        case 'CREATE_GROUP_HAS_ERROR':
            return Object.assign({}, state, {
                createGroupHasError: action.hasError,
            })
        case 'CREATE_GROUP_DATA_SUCCESS':
            return Object.assign({}, state, {
                createGroupData: action.data,
            })
        case 'CREATE_GROUP_IS_LOADING':
            return Object.assign({}, state, {
                createGroupIsLoading: action.isLoading,
            })
        case 'SET_CURRENT_GROUP': 
            return Object.assign({}, state, {
                currentGroup: action.group,
            })
        case 'ADD_CURRENT_GROUP_MEMBER':
            members = state.currentGroup && state.currentGroup.members ? {...state.currentGroup.members} : {}
            members[action.member.accountID] = action.member
            return Object.assign({}, state, {
                currentGroup: Object.assign({}, state.currentGroup, {
                    members
                })
            })
        case 'REMOVE_CURRENT_GROUP_MEMBER':
            members = state.currentGroup && state.currentGroup.members ? {...state.currentGroup.members} : {}
            if(members[action.memberID]) delete members[action.memberID]
            return Object.assign({}, state, {
                currentGroup: Object.assign({}, state.currentGroup, {
                    members
                })
            })
        case 'UPDATE_CHAT_LOG':
            const messages = state.currentGroup && state.currentGroup.messages ? [...state.currentGroup.messages, action.message] : [action.message]
            return Object.assign({}, state, {
                currentGroup: Object.assign({}, state.currentGroup, {
                    messages
                })
            })
        case 'GET_CHAT_LOG_HAS_ERROR':
            return Object.assign({}, state, {
                getChatLogHasError: action.hasError,
            })
        case 'GET_CHAT_LOG_DATA_SUCCESS':
            return Object.assign({}, state, {
                currentGroup: Object.assign({}, state.currentGroup, {
                    messages: action.data
                })
            })
        case 'GET_CHAT_LOG_IS_LOADING':
            return Object.assign({}, state, {
                getChatLogIsLoading: action.isLoading,
            })
        case 'POST_CHAT_MESSAGE_HAS_ERROR':
            return Object.assign({}, state, {
                postChatMessageHasError: action.hasError,
            })
        case 'POST_CHAT_MESSAGE_DATA_SUCCESS':
            return Object.assign({}, state, {
                postChatMessageData: action.data,
            })
        case 'POST_CHAT_MESSAGE_IS_LOADING':
            return Object.assign({}, state, {
                postChatMessageIsLoading: action.isLoading,
            })
        case 'SET_GROUP_SETTINGS_HAS_ERROR':
            return Object.assign({}, state, {
                setGroupSettingsHasError: action.hasError,
            })
        case 'SET_GROUP_SETTINGS_DATA_SUCCESS':
            return Object.assign({}, state, {
                setGroupSettingsData: action.data,
            })
        case 'SET_GROUP_SETTINGS_IS_LOADING':
            return Object.assign({}, state, {
                setGroupSettingsIsLoading: action.isLoading,
            })
        case 'KICK_HAS_ERROR':
            return Object.assign({}, state, {
                kickHasError: action.hasError,
            })
        case 'KICK_DATA_SUCCESS':
            groupsCopy = state.getGroupsData ? {...state.getGroupsData} : {}
            if(groupsCopy[action.extra.gid]) {
                delete groupsCopy[action.extra.gid].groupMemberIDs[action.extra.memberID]
            }
            return Object.assign({}, state, {
                kickData: action.data,
                getGroupsData: groupsCopy
            })
        case 'KICK_IS_LOADING':
            return Object.assign({}, state, {
                kickIsLoading: action.isLoading,
            })
        case 'GET_RATE_FORM_HAS_ERROR':
            return Object.assign({}, state, {
                getRateFormHasError: action.hasError,
            })
        case 'GET_RATE_FORM_DATA_SUCCESS':
            return Object.assign({}, state, {
                getRateFormData: action.data,
            })
        case 'GET_RATE_FORM_IS_LOADING':
            return Object.assign({}, state, {
                getRateFormIsLoading: action.isLoading,
            })
        case 'POST_RATE_FORM_HAS_ERROR':
            return Object.assign({}, state, {
                postRateFormHasError: action.hasError,
            })
        case 'POST_RATE_FORM_DATA_SUCCESS':
            return Object.assign({}, state, {
                postRateFormData: action.data,
            })
        case 'POST_RATE_FORM_IS_LOADING':
            return Object.assign({}, state, {
                postRateFormIsLoading: action.isLoading,
            })
        case 'INVITE_ALL_HAS_ERROR':
            return Object.assign({}, state, {
                inviteAllHasError: action.hasError,
            })
        case 'INVITE_ALL_DATA_SUCCESS':
            return Object.assign({}, state, {
                inviteAllData: action.data,
            })
        case 'INVITE_ALL_IS_LOADING':
            return Object.assign({}, state, {
                inviteAllIsLoading: action.isLoading,
            })
        case 'CREATE_EVENT_HAS_ERROR':
            return Object.assign({}, state, {
                createEventHasError: action.hasError,
            })
        case 'CREATE_EVENT_DATA_SUCCESS':
            return Object.assign({}, state, {
                createEventData: action.data,
            })
        case 'CREATE_EVENT_IS_LOADING':
            return Object.assign({}, state, {
                createEventIsLoading: action.isLoading,
            })
        default:
            return state
    }
}

export default groups