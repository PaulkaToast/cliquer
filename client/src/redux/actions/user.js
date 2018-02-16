export function updateUserData(data) {
    return {
        type: 'UPDATE_USER',
        data
    }
}

export function addSkills(skills) {
    return {
        type: 'ADD_SKILLS',
        skills
    }
}