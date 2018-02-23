export { logIn, logOut, registerHasError, registerIsLoading, registerSuccess, registerUser, setToken } from './auth'
export { updateUserData, addSkills, addNewSkill, deleteNewSkill, clearNewSkills } from './user'
export { fetchHasError, fetchIsLoading, fetchDataSuccess, fetchData } from './fetch'
export { postSkillHasError, postSkillIsLoading, postSkillDataSuccess, postSkill,
         getSkillsHasError, getSkillsIsLoading, getSkillsDataSuccess, getSkills,
         removeSkillHasError, removeSkillIsLoading, removeSkillDataSuccess, removeSkill } from './skills'

//TODO: Change getSkillList to one reducer function