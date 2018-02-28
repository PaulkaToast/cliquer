import React from "react";
import { Register } from "./Register";
import { mount } from "enzyme";
import { MemoryRouter } from "react-router";

describe( "Register Page" , () => {
    let mountedregisterPage;
    const registerPage = () => {
        if (!mountedregisterPage) {
            mountedregisterPage = mount(
                <MemoryRouter>
                <Register/>
                </MemoryRouter>
            );
        }
        return mountedregisterPage;
    }

    it("shows the facebook button", () => {
        const divs = registerPage().find(".fb-container");
        expect(divs.length).toBe(1);
    })

    it("shows link to log in", () => {
        const divs = registerPage().find(".login-container");
        expect(divs.length).toBe(2);
    })

    it("shows register form", () => {
        const divs = registerPage().find(".form-register");
        expect(divs.length).toBe(1);
    })

    it("shows the logo", () => {
        const divs = registerPage().find(".logo");
        expect(divs.length).toBe(2);
    })
})