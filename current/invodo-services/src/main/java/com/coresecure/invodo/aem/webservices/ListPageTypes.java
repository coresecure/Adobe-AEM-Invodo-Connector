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

import com.coresecure.invodo.aem.ConfigurationService;
import com.coresecure.invodo.aem.ConfigurationUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.jackrabbit.api.security.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

@Service
@Component
@Properties(value = {
        @Property(name = "sling.servlet.extensions", value = { "json" }),
        @Property(name = "sling.servlet.paths", value = "/bin/invodo/pagetypes")
})
public class ListPageTypes extends SlingAllMethodsServlet {

    @Override
    protected void doPost(final SlingHttpServletRequest request,
                          final SlingHttpServletResponse response) throws ServletException,
            IOException {

        api(request, response);



    }
    private static Logger loggerVar = LoggerFactory.getLogger(ConfigurationService.class);


    public void api(final SlingHttpServletRequest request,
                    final SlingHttpServletResponse response) throws ServletException,
            IOException {
        PrintWriter outWriter = response.getWriter();
        response.setContentType("application/json");
        JSONObject root = new JSONObject();

        String[] pageTypes = ConfigurationUtil.getPagetypes();
        try {
            JSONArray items = new JSONArray();
            for (String item: pageTypes) {
                JSONObject itemObj = new JSONObject();
                itemObj.put("name",item);
                itemObj.put("value",item);
                items.put(itemObj);
            }
            root.put("items",items);
            root.put("results",items.length());
            outWriter.write(root.toString(1));
        } catch (JSONException je) {
            outWriter.write("{\"items\":[],\"results\":0}");
        }

    }


    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException,
            IOException {
        api(request, response);

    }

}