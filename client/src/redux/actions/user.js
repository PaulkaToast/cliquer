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

export function addNewSkill(skill) {
    return {
        type: 'ADD_NEW_SKILL',
        skill
    }
}

export function deleteNewSkill(skill) {
    return {
        type: 'DELETE_NEW_SKILL',
        skill
    }
}

export function clearNewSkills() {
    return {
        type: 'CLEAR_NEW_SKILLS'
    }
}