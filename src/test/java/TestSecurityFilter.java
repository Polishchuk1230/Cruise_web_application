import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.com.cruises.controller.filters.SecurityFilter;
import ua.com.cruises.controller.uris.AppPath;
import ua.com.cruises.model.Role;
import ua.com.cruises.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestSecurityFilter {
    private static final String TEST_CONTEXT_PATH = "/testing";

    private User userForTest;

    private HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
    private HttpSession mockedSession = Mockito.mock(HttpSession.class);

    {
        Mockito.when(mockedRequest.getContextPath())
                .thenReturn(TEST_CONTEXT_PATH);

        Mockito.when(mockedRequest.getSession())
                .thenReturn(mockedSession);
    }

    @BeforeEach
    private void userPreparation() {
        userForTest = new User(1, "username", null, "000000000000");
    }

    /*
    * Testing access of a user with Role.ADMIN to the page that should be accessed only users with Role.ADMIN
    * Success result of this testing method will be invoking of mockedChain.doFilter()
    *
    * (Instead of AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE at CHECKED_URI can be every existing page an admin should
    * have access to)
    * */
    @Test
    public void testAdminAccessToAdminsOnlyPage() throws ServletException, IOException {
        //set testing uri-destination
        final String CHECKED_URI = TEST_CONTEXT_PATH + AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE;
        Mockito.when(mockedRequest.getRequestURI())
                .thenReturn(CHECKED_URI);

        //give ADMIN and USER Roles to the test User
        userForTest.setRoles(new ArrayList<>(List.of(Role.USER, Role.ADMIN)));
        Mockito.when(mockedSession.getAttribute("user"))
                .thenReturn(userForTest);

        HttpServletResponse assertResponse = new HttpServletResponseWrapper(Mockito.mock(HttpServletResponse.class)) {
            @Override
            public void sendError(int sc) {
                Assertions.fail("Test failed with code: " + sc);
            }
        };

        FilterChain mockedChain = Mockito.mock(FilterChain.class);

        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.doFilter(mockedRequest, assertResponse, mockedChain);
    }

    /*
     * Testing access of a user without Role.ADMIN to the page that should be accessed only users with Role.ADMIN
     * Success result of this testing method will be invoking of servletResponse.sendError(403)
     *
     * (Instead of AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE at CHECKED_URI can be every existing page not an admin should NOT
     * have access to)
     * */
    @Test
    public void testNotAdminAccessToAdminOnlyPage() throws ServletException, IOException {
        //set testing uri-destination
        final String CHECKED_URI = TEST_CONTEXT_PATH + AppPath.PATH_TO_ORDER_ADMINISTRATION_PAGE;
        Mockito.when(mockedRequest.getRequestURI())
                .thenReturn(CHECKED_URI);

        //give only USER Role to the test User
        userForTest.setRoles(new ArrayList<>(List.of(Role.USER)));
        Mockito.when(mockedSession.getAttribute("user"))
                .thenReturn(userForTest);

        HttpServletResponse assertResponse = new HttpServletResponseWrapper(Mockito.mock(HttpServletResponse.class)) {
            @Override
            public void sendError(int sc) {
                if (sc != 403) {
                    Assertions.fail("The code must be 403. Test failed with code: " + sc);
                }
            }
        };

        FilterChain mockedChain = (request, response) ->
                Assertions.fail("The error code must be 403. But the filter was passed.");

        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.doFilter(mockedRequest, assertResponse, mockedChain);
    }

    /*
     * Testing access to a not existing page.
     * Success result of this testing method will be invoking of servletResponse.sendError(404)
     *
     * (Instead of /notexistingpage at CHECKED_URI can be every not existing page)
     * */
    @Test
    public void testAccessToNotExistingPage() throws ServletException, IOException {
        //set testing uri-destination
        final String CHECKED_URI = TEST_CONTEXT_PATH + "/notexistingpage";
        Mockito.when(mockedRequest.getRequestURI())
                .thenReturn(CHECKED_URI);

        //give only USER Role to the test User
        userForTest.setRoles(new ArrayList<>(List.of(Role.USER)));
        Mockito.when(mockedSession.getAttribute("user"))
                .thenReturn(userForTest);

        HttpServletResponse assertResponse = new HttpServletResponseWrapper(Mockito.mock(HttpServletResponse.class)) {
            @Override
            public void sendError(int sc) {
                if (sc != 404) {
                    Assertions.fail("The code must be 404. Test failed with code: " + sc);
                }
            }
        };

        FilterChain mockedChain = (request, response) ->
                Assertions.fail("The error code must be 404. But the filter was passed.");

        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.doFilter(mockedRequest, assertResponse, mockedChain);
    }
}
