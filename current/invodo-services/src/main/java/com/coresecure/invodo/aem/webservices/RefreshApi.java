package com.coresecure.invodo.aem.webservices;

import com.coresecure.invodo.aem.ConfigurationUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@Component
@Properties(value = {
        @Property(name = "sling.servlet.extensions", value = { "json" }),
        @Property(name = "sling.servlet.paths", value = "/bin/invodo/refresh")
})
public class RefreshApi extends SlingAllMethodsServlet {

    @Override
    protected void doPost(final SlingHttpServletRequest request,
                          final SlingHttpServletResponse response) throws ServletException,
            IOException {

        api(request, response);



    }


    public void api(final SlingHttpServletRequest request,
                    final SlingHttpServletResponse response) throws ServletException,
            IOException {
        PrintWriter outWriter = response.getWriter();
        response.setContentType("application/json");
        JSONObject root = new JSONObject();


        int requestedAPI = 0;
        String requestedToken="";
        boolean is_authorized = false;
        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);
            UserManager userManager = request.getResourceResolver().adaptTo(UserManager.class);
                /* to get the current user */
            Authorizable auth = userManager.getAuthorizable(session.getUserID());
            if (auth != null ) {
               if ("admin".equals(auth.getID()) && ConfigurationUtil.isAdminRefreshAllowed()){
                    is_authorized = true;
                } else {
                    Iterator<Group> groups = auth.memberOf();
                    while (groups.hasNext() && !is_authorized) {
                        Group group = groups.next();
                        is_authorized = ConfigurationUtil.getRefresherGroupsList().contains(group.getID());
                    }
                }
                if (is_authorized) {
                    ConfigurationUtil.refreshChannel(true);
                }
            }
        } catch (RepositoryException e) {

        } finally {
            if (!is_authorized) response.sendError(403);
        }

    }


    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException,
            IOException {
        api(request, response);

    }

}