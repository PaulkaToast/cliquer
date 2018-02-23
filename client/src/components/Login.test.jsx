import React from "react";
import { Login } from "./Login";
import { mount } from "enzyme";
import { MemoryRouter } from "react-router";

describe( "Login Page" , () => {
    let mountedloginPage;
    const loginPage = () => {
        if (!mountedloginPage) {
            mountedloginPage = mount(
                <MemoryRouter>
                <Login/>
                </MemoryRouter>
            );
        }
        return mountedloginPage;
    }

    it("shows the facebook button", () => {
        const divs = loginPage().find(".fb-container");
        expect(divs.length).toBe(1);
    })
})