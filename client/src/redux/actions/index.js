export { logIn, logOut, registerHasError, registerIsLoading, registerSuccess, registerUser, setToken } from './auth'
export { updateUserData, addSkills, addNewSkill, deleteNewSkill, clearNewSkills } from './user'
export { fetchHasError, fetchIsLoading, fetchDataSuccess, fetchData } from './fetch'
export { postSkillHasError, postSkillIsLoading, postSkillDataSuccess, postSkill,
         getSkillsHasError, getSkillsIsLoading, getSkillsDataSuccess, getSkills,
         removeSkillHasError, removeSkillIsLoading, removeSkillDataSuccess, removeSkill } from './skills'
export { getProfileHasError, getProfileIsLoading, getProfileDataSuccess, getProfile,
         deleteProfileHasError, deleteProfileIsLoading, deleteProfileDataSuccess, deleteProfile } from './profile'
export { getGroupsHasError, getGroupsIsLoading, getGroupsDataSuccess, getGroups,
         groupInviteHasError, groupInviteIsLoading, groupInviteDataSuccess, groupInvite,
         joinGroupHasError, joinGroupIsLoading, joinGroupDataSuccess, joinGroup,
         deleteGroupHasError, deleteGroupIsLoading, deleteGroupDataSuccess, deleteGroup,
         leaveGroupHasError, leaveGroupIsLoading, leaveGroupDataSuccess, leaveGroup,
         createGroupHasError, createGroupIsLoading, createGroupDataSuccess, createGroup  } from './groups'
export { addFriendHasError, addFriendIsLoading, addFriendDataSuccess, addFriend,
         removeFriendHasError, removeFriendIsLoading, removeFriendDataSuccess, removeFriend } from './friends'
export { getMessagesHasError, getMessagesIsLoading, getMessagesDataSuccess, getMessages } from './messages'
//TODO: Change getSkillList to one reducer function