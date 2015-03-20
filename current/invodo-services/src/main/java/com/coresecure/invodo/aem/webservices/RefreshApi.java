/*
    Adobe CQ5 Invodo Connector

    Copyright (C) 2015 Coresecure Inc.

        Authors:    Alessandro Bonfatti

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
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

        if (request.getParameter("checkuser") != null && "1".equals(request.getParameter("checkuser"))) {
            authcheck(request, response);
        } else {
            api(request, response);
        }


    }

    public void authcheck(final SlingHttpServletRequest request,
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
                }
            }
            root.put("is_authorized",is_authorized);
            response.getWriter().write(root.toString());
        } catch (Exception e) {
            response.getWriter().write("{\"is_authorized\":false}");
        }


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
        if (request.getParameter("checkuser") != null && "1".equals(request.getParameter("checkuser"))) {
            authcheck(request, response);
        } else {
            api(request, response);
        }
    }

}