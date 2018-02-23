package com.styxxco.cliquer.tests;

import com.styxxco.cliquer.domain.Account;
import com.styxxco.cliquer.service.AccountService;
import com.styxxco.cliquer.service.FirebaseService;
import com.styxxco.cliquer.web.RestController;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(value = RestController.class, secure = false)
public class RestAPITests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;
    @MockBean
    private FirebaseService firebaseService;

    @Test
    public void signUpWithoutFirebase() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(result.getResponse().getStatus(), 400);
    }

    @Test
    public void signUpWithFirebase() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/signup")
                .header("X-Authorization-Firebase", "dummy");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void getProfileValid() throws Exception {
        String username = "someUser";
        ObjectId id = new ObjectId();
        Account fakeUser = new Account(username, "Some", "User");

        Mockito.when(
                accountService.getUserProfile(username)
        ).thenReturn(fakeUser);
        Mockito.when(
                accountService.getMemberProfile(id)
        ).thenReturn(fakeUser);
        Mockito.when(
                accountService.getPublicProfile(id)
        ).thenReturn(fakeUser);

        MockHttpServletRequestBuilder user = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("identifier", username)
                .param("type", "user");

        MvcResult result = mockMvc.perform(user).andReturn();
        String expected = "{username: someUser, firstName: Some, lastName: User}";
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);

        MockHttpServletRequestBuilder memberAndPublic = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("identifier", id.toHexString());

        result = mockMvc.perform(memberAndPublic.param("type", "member")).andReturn();
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);

        memberAndPublic = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("identifier", id.toHexString());

        result = mockMvc.perform(memberAndPublic.param("type", "public")).andReturn();
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

    @Test
    public void getProfileInvalid() throws Exception {
        MockHttpServletRequestBuilder badReq = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("identifier", "hackerman")
                .param("type", "hacker");

        MvcResult result = mockMvc.perform(badReq).andReturn();
        Assert.assertEquals("\"BAD_REQUEST\"", result.getResponse().getContentAsString());

        MockHttpServletRequestBuilder unknownUser = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("identifier", "hackerman")
                .param("type", "user");
        result = mockMvc.perform(badReq).andReturn();
        Assert.assertEquals("\"BAD_REQUEST\"", result.getResponse().getContentAsString());
    }
}
