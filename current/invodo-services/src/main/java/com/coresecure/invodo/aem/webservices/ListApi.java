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
        @Property(name = "sling.servlet.paths", value = "/bin/invodo/list")
})
public class ListApi extends SlingAllMethodsServlet {

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


        int requestedAPI = 0;
        String requestedToken="";
        List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
        String query = request.getParameter("query");
        if (selectors.contains("sidekick")) {
            try {
                String limit = request.getParameter("limit");
                int startVal = 0;
                int stopVal = 40;
                if (limit != null && !limit.trim().isEmpty() && limit.split("\\.\\.")[0] != null && limit.split("\\.\\.")[1] != null) {
                    startVal = Integer.parseInt(limit.split("\\.\\.")[0]);
                    stopVal = Integer.parseInt(limit.split("\\.\\.")[1]);
                }

                JSONObject result = ConfigurationUtil.getCachedVideos(startVal, (stopVal - startVal), false, query);
                if (result != null) {
                    outWriter.write(result.toString(2));
                } else {
                    outWriter.write("{\"items\":[],\"results\":0,\"error\":2}");
                }
            } catch (Exception e) {
                loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                outWriter.write("{\"items\":[],\"results\":0,\"error\":1}");
            }
        } else {
            try {
                int startVal = Integer.parseInt(request.getParameter("start"));
                int limitResults = Integer.parseInt(request.getParameter("limit"));

                JSONObject result = ConfigurationUtil.getCachedVideos(startVal, limitResults, false, query);
                if (result != null) {
                    outWriter.write(result.toString(2));
                } else {
                    outWriter.write("{\"items\":[],\"results\":0}");
                }
            } catch (Exception e) {
                loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                outWriter.write("{\"items\":[],\"results\":0}");
            }
        }


    }


    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse response) throws ServletException,
            IOException {
        api(request, response);

    }

}