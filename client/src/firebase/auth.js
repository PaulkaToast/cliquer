import { auth } from './firebase'

//Register
export const createUserWithEmailAndPassword = (email, password) =>
  auth.createUserWithEmailAndPassword(email, password)

//Login
export const logInWithEmailAndPassword = (email, password) =>
  auth.signInWithEmailAndPassword(email, password)

//Logout
export const logOut = () =>
  auth.signOut()

//Password Reset
export const doPasswordReset = (email) =>
auth.sendPasswordResetEmail(email)

//Password Change
export const doPasswordUpdate = (password) =>
auth.currentUser.updatePassword(password)

export const signInWithFacebook = (provider) =>
auth.signInWithPopup(provider)
