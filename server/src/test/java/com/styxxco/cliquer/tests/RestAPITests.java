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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;

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
    public void testLoginPage() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/login");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void getProfileInvalid() throws Exception {
        MockHttpServletRequestBuilder badReq = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("username", "hackerman")
                .param("type", "hacker");

        MvcResult result = mockMvc.perform(badReq).andReturn();
        assertEquals(400, result.getResponse().getStatus());

        MockHttpServletRequestBuilder unknownUser = MockMvcRequestBuilders
                .get("/api/getProfile")
                .param("username", "hackerman")
                .param("type", "user");
        result = mockMvc.perform(badReq).andReturn();
        assertEquals(400, result.getResponse().getStatus());
    }
}
