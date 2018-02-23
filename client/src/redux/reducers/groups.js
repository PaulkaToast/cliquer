function groups(state = {}, action) {
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
            return Object.assign({}, state, {
                deleteGroupData: action.data,
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
            return Object.assign({}, state, {
                leaveGroupData: action.data,
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
        default:
            return state
    }
}

export default groups