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

export function setNewSkills(skills) {
    return {
        type: 'SET_NEW_SKILLS',
        skills
    }
}

export function clearNewSkills() {
    return {
        type: 'CLEAR_NEW_SKILLS'
    }
}

export function setLocation(position) {
    return {
        type: 'SET_LOCATION',
        position
    }
}

export function addObjectID(id) {
    return {
        type: 'ADD_OBJECT_ID',
        id
    }
}

export function clearObjectID() {
    return {
        type: 'CLEAR_OBJECT_ID',
    }
}