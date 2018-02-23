package com.styxxco.cliquer.tests;

import com.google.firebase.auth.FirebaseToken;
import com.styxxco.cliquer.database.RoleRepository;
import com.styxxco.cliquer.domain.Role;
import com.styxxco.cliquer.security.FirebaseParser;
import com.styxxco.cliquer.security.FirebaseTokenHolder;
import com.styxxco.cliquer.security.SecurityConfiguration;
import com.styxxco.cliquer.service.FirebaseService;
import com.styxxco.cliquer.web.RestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityTests {

    @Autowired
    private RestController restController;

    @Autowired
    private FirebaseService firebaseServiceMock;

    @Autowired
    private RoleRepository roleRepository;

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyTokenSignupError() {
        restController.signUp("", "test", "test");
    }

    @Test(expected = FirebaseParser.FirebaseTokenInvalidException.class)
    public void testExpireTokenError() {
        restController.signUp("eyJhbGciOiJSUzI1NiIsImtpZCI6IjkzYzJjMmYxMGFiZWRkOWIzOGVmYzUzNWRkOGVmNWUyNGI1N2U5YjEifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vY2xpcXVlcjMwNyIsImF1ZCI6ImNsaXF1ZXIzMDciLCJhdXRoX3RpbWUiOjE1MTg5ODc2NzcsInVzZXJfaWQiOiJYOFNqSUJZUThvTm9BU2s4bFh5bG1nQTJqTWsyIiwic3ViIjoiWDhTaklCWVE4b05vQVNrOGxYeWxtZ0Eyak1rMiIsImlhdCI6MTUxODk5MzA5NCwiZXhwIjoxNTE4OTk2Njk0LCJlbWFpbCI6InNoYXdua21vbnRnb21lcnlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInNoYXdua21vbnRnb21lcnlAZ21haWwuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.u_cxfhuIp5RgI1Oq3PkC7o3G_St_zkbO3bYMegC03VyBADjHaSwlR0EfPP-AzuYYns_KBHr3rqjZunLkMReFSen0WED3DS9a6eaaRK28JRLv1c6SuAOqeVWpDvbj55yIr4umLo_IfetUwLrnArvrkUbFgs1kFKNwUS4An_0H59NwyZEqB5GTDoUf5CW_XiLHUdDXTUb0dz3c6kbja2i0JHlfLZINzvUGG4WqFyOVNqAKnUEzfbcy7lWeLAeCW8OcGAOmKuTvmTHfLKkSwkFsUIJ9XHDHuKpJMgFEtCNje81GFLWE6HpSuNfhyn1xdsVpf9y1B7RPE80r52lEd2Wndw", "test", "test");
    }

}
