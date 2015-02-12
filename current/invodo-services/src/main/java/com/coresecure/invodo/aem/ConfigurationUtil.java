package com.coresecure.invodo.aem;

import org.apache.sling.api.request.ResponseUtil;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;


import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.*;


import javax.jcr.*;
import org.apache.sling.jcr.api.SlingRepository;
import com.day.text.*;

public class ConfigurationUtil {
    private static Logger loggerVar = LoggerFactory.getLogger(ConfigurationService.class);

    public static ResourceResolverFactory getResourceResolverFactory() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ResourceResolverFactory.class).getBundleContext();
        return (ResourceResolverFactory)bundleContext.getService(bundleContext.getServiceReference(ResourceResolverFactory.class.getName()));
    }
    public static SlingRepository getSlingRepository() {
        BundleContext bundleContext = FrameworkUtil.getBundle(SlingRepository.class).getBundleContext();
        return (SlingRepository)bundleContext.getService(bundleContext.getServiceReference(SlingRepository.class.getName()));
    }
    public static ConfigurationService getSlingSettingService() {
        BundleContext bundleContext = FrameworkUtil.getBundle(ConfigurationService.class).getBundleContext();
        return (ConfigurationService)bundleContext.getService(bundleContext.getServiceReference(ConfigurationService.class.getName()));
    }
    public static String getAffiliateId() {
        ConfigurationService configurationService = getSlingSettingService();
        return configurationService.getAffiliateId();
    }
    public static String getApiKey() {
        ConfigurationService configurationService = getSlingSettingService();
        return configurationService.getApiKey();
    }
    public static String getStoragePath() {
        ConfigurationService configurationService = getSlingSettingService();
        return configurationService.getStoragePath();
    }
    public static boolean isGroupAllowed(String groupName){
        ConfigurationService configurationService = getSlingSettingService();
        return configurationService.isGroupAllowed(groupName);
    }
    public static List<String> getRefresherGroupsList(){
        ConfigurationService configurationService = getSlingSettingService();
        return  configurationService.getRefresherGroupsList();
    }
    public static boolean isAdminRefreshAllowed(){
        ConfigurationService configurationService = getSlingSettingService();
        return configurationService.isAdminRefreshAllowed();
    }
    private static Boolean queryMatch(Node node, String query) {
        String regex = query.toLowerCase()+"(.*)?";
        boolean match = false;
        try {
            match = (node.hasProperty("rid") && node.getProperty("rid").getString().toLowerCase().matches(regex)) ||
                    (node.hasProperty("durableId") && node.getProperty("durableId").getString().toLowerCase().matches(regex)) ||
                    (node.hasProperty("name") && node.getProperty("name").getString().toLowerCase().matches(regex));

            if (node.hasProperty("tags")) {
                if(node.getProperty("tags").isMultiple()) {
                    for(Value tag : node.getProperty("tags").getValues()) {
                        match = match || tag.getString().toLowerCase().matches(regex);
                    }
                } else {
                    match = match || node.getProperty("tags").getString().toLowerCase().matches(regex);
                }
            }

        } catch (Exception e) {

        }

        return match;
    }

    public static JSONObject getCachedVideos() {
        return getCachedVideos(-1, -1, false, null);
    }
    public static JSONObject getCachedVideos(int startVal) {
        return getCachedVideos(startVal, -1, false, null);
    }
    public static JSONObject getCachedVideos(int startVal, int limit, boolean byPage, String query) {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject error = new JSONObject();
        int totalResults = 0;
        SlingRepository repository = getSlingRepository();
        ResourceResolver resourceResolver = null;
        Session session = null;
        try {
            loggerVar.trace("cacheVideos");
            session = repository.loginAdministrative(null);
            ResourceResolverFactory rrf = getResourceResolverFactory();
            resourceResolver = rrf.getAdministrativeResourceResolver(null);
            Node existingItem = session.getNode(getStoragePath());

            if (query != null && query.trim().length() > 0) {
                query = ResponseUtil.escapeXml(query).replaceAll("\"","&quot;")+"*";
            } else {
                query = "";
            }

            String ActivatedPathQuery = "/jcr:root"+getStoragePath()+"//element(*, cq:Page)[jcr:contains(content_type, 'presentation')" + (query.trim().isEmpty() ? "" : " and jcr:contains(.,\""+query+"\")")+"]";
            loggerVar.trace("Search : " + ActivatedPathQuery);
            Iterator<Resource> resIterator = resourceResolver.findResources(ActivatedPathQuery,"xpath");

            //NodeIterator pres_itr = existingItem.getNodes("presentation-*");
            long count = 0;
            while (resIterator.hasNext()) {
                //Node videoPage = pres_itr.nextNode();
                Resource itemRes = resIterator.next();
                Node videoPage = itemRes.adaptTo(Node.class);
                loggerVar.trace("Item : " + videoPage.getPath());

                if ((startVal < 0 || count >= startVal) && (limit < 0 || count < startVal+limit)) {
                    if (videoPage.hasNode("jcr:content")) {
                        Node video = videoPage.getNode("jcr:content");
                        loggerVar.trace("video : " + video.getPath());
                        JSONObject itemJson = new JSONObject();
                        itemJson.put("id", video.getProperty("id").getString());
                        itemJson.put("rid", video.getProperty("rid").getString());
                        itemJson.put("durableId", video.getProperty("durableId").getString());
                        itemJson.put("name", video.getProperty("name").getString());
                        itemJson.put("description", video.getProperty("description").getString());
                        JSONArray tags = new JSONArray();
                        if (video.hasProperty("tags")) {
                            if (video.getProperty("tags").isMultiple()) {
                                for(Value tag : video.getProperty("tags").getValues()) {
                                    tags.put(tag.getString());
                                }
                            } else {
                                tags.put(video.getProperty("tags").getString());
                            }
                        }
                        itemJson.put("tags", tags);
                        itemJson.put("thumbnail_image", video.getProperty("thumbnail_image").getString());
                        items.put(itemJson);
                    }
                }
                count++;
            }
            result.put("items",items);
            result.put("results", count);
        } catch (RepositoryException re) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(re));
            result =error;
        } catch (JSONException e) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            result = error;
        } catch(org.apache.sling.api.resource.LoginException le) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(le));
            result = error;
        } finally {
            if (session != null) {
                session.logout();
            }
            if (repository != null) repository = null;
        }
        return result;
    }

    private static JSONObject getVideos() {
        return getVideos(-1,-1,false);
    }
    private static JSONObject getVideos(int startVal) {
        return getVideos(startVal,-1,false);
    }
    private static JSONObject getVideos(int startVal, int limit, boolean byPage) {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject error = new JSONObject();
        int totalResults = 0;
        try {
            error =new JSONObject("{\"error\":[],\"results\":0}");
            String affiliate_id= getAffiliateId();
            String api_key= getApiKey();
            String url = "https://feed.invodo.com/v4/presentations?siteKey="+affiliate_id+"&apiKey="+api_key;
            if (limit>0) {
                int page = 0;
                url += "&resultsPerPage=" + limit;
                if (byPage) {
                    url += "&page=" + startVal;
                } else {
                    if (startVal > -1) {
                        page = (int) Math.floor((startVal + 1) / limit);
                        url += "&page=" + page;
                    }
                }
            }
            result.put("url",url);
            JSONObject json = JsonReader.readJsonFromUrl(url);
            JSONArray raw_items =  json.getJSONArray("items");
            for(int i = 0; i<raw_items.length();i++){
                JSONObject itemJson = new JSONObject();
                itemJson.put("id",startVal+i);
                itemJson.put("rid",raw_items.getJSONObject(i).get("id"));
                itemJson.put("durableId", raw_items.getJSONObject(i).get("durableId"));
                itemJson.put("name",   raw_items.getJSONObject(i).get("title"));
                itemJson.put("description",   raw_items.getJSONObject(i).get("description"));
                itemJson.put("tags", raw_items.getJSONObject(i).get("tags"));
                itemJson.put("thumbnail_image", raw_items.getJSONObject(i).getJSONArray("clips").getJSONObject(0).getJSONArray("encodings").getJSONObject(0).get("thumbnailPublicURL"));
                items.put(itemJson);
            }
            result.put("items",items);
            result.put("results", json.get("totalItems"));
        } catch (JSONException je) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));

            result =error;
        } catch (IOException e) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

            result = error;
        }
        return result;
    }

    public static JSONObject getVideosByPage(int page, int limit) {
        return getVideos(page, limit, true);
    }
    public static boolean cacheVideos(){
        boolean result = false;
        SlingRepository repository = getSlingRepository();
        Session session = null;
        loggerVar.trace("cacheVideos");
        try {
            session = repository.loginAdministrative(null);
            int page = 0;
            JSONArray items = new JSONArray();
            while (page == 0 || items.length() > 0) {
                JSONObject videos = getVideosByPage(page, 1000);
                items = videos.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    loggerVar.trace("video : " + i);
                    JSONObject item = items.getJSONObject(i);
                    loggerVar.trace("video object : " + item.toString(1));
                    String hash = Text.digest("MD5", item.toString().getBytes());
                    loggerVar.trace("hash : " + hash);
                    String path = getStoragePath()+"/presentations/presentation-" + item.getString("rid");
                    Node content = createPageContent(path, session);
                    loggerVar.trace("Node: " + content.getPath());
                    if (content != null) {
                        if (!content.hasProperty("hash") || !hash.equals(content.getProperty("hash").getString())) {
                            content.setProperty("id", item.getString("id"));
                            content.setProperty("rid", item.getString("rid"));
                            content.setProperty("hash", hash);
                            content.setProperty("durableId", item.getString("durableId"));
                            content.setProperty("name", item.getString("name"));
                            content.setProperty("jcr:title", item.getString("name"));
                            content.setProperty("description", item.getString("description"));
                            content.setProperty("content_type", "presentation");
                            Value[] values = new Value[item.getJSONArray("tags").length()];
                            ValueFactory valueFactory = session.getValueFactory();
                            for (int iTag = 0; iTag < item.getJSONArray("tags").length(); iTag++) {
                                values[iTag] = valueFactory.createValue(item.getJSONArray("tags").getString(iTag));
                            }
                            content.setProperty("tags", values);
                            content.setProperty("thumbnail_image", item.getString("thumbnail_image"));
                            content.save();
                            loggerVar.trace("Saved Node: " + path);

                        } else {
                            loggerVar.trace("Same Node: " + path + ", Original HASH: " + content.getProperty("hash").getString() + ", New HASH: " + hash);
                        }
                    }
                }
                page++;
                session.save(); //maximum every 1000 nodes
            }
            session.save();

        } catch (Exception je) {
            //to-do
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));
        } finally {
            if (session != null) {
                session.logout();
            }
            if (repository != null) repository = null;
        }
        return result;
    }
    public static Node createPageContent(String path, Session session){
        Node result = null;
        String contentPath = path;// + "/jcr:content";

        try {
            Node existingItem = null;
            try {
                existingItem = session.getNode(contentPath);
            } catch (PathNotFoundException pnfe) {
                existingItem = null;
            }
            if (existingItem != null && existingItem.hasNode("jcr:content")) {
                result = existingItem.getNode("jcr:content");
            } else {
                //result = JcrUtil.createPath(contentPath, "cq:Page", "cq:PageContent", session, true);
                Node pageNode = JcrUtil.createPath(contentPath, false, "sling:Folder", "cq:Page", session, false);
                result = pageNode.addNode("jcr:content", "cq:PageContent");
            }
            session.save();
        } catch (RepositoryException re) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(re));
        }
        return result;
    }
    ////PUBLICATIONS
    private static JSONObject getPublicationsByPage(int page, int limit) {
        return getPublications(page, limit, true);
    }
    private static JSONObject getPublications(int startVal, int limit, boolean byPage) {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject error = new JSONObject();
        int totalResults = 0;
        try {
            error =new JSONObject("{\"error\":[],\"results\":0}");
            String affiliate_id= getAffiliateId();
            String api_key= getApiKey();
            String url = "https://feed.invodo.com/v4/publications?siteKey="+affiliate_id+"&apiKey="+api_key;
            if (limit>0) {
                int page = 0;
                url += "&resultsPerPage=" + limit;
                if (byPage) {
                    url += "&page=" + startVal;
                } else {
                    if (startVal > -1) {
                        page = (int) Math.floor((startVal + 1) / limit);
                        url += "&page=" + page;
                    }
                }
            }
            result.put("url",url);
            JSONObject json = JsonReader.readJsonFromUrl(url);
            JSONArray raw_items =  json.getJSONArray("items");
            for(int i = 0; i<raw_items.length();i++){
                JSONObject itemJson = new JSONObject();
                itemJson.put("id",startVal+i);
                itemJson.put("rid",raw_items.getJSONObject(i).get("id"));
                itemJson.put("siteKey", raw_items.getJSONObject(i).get("siteKey"));
                itemJson.put("publicationStartDate",   raw_items.getJSONObject(i).get("publicationStartDate"));
                itemJson.put("publicationEndDate",   raw_items.getJSONObject(i).get("publicationEndDate"));
                itemJson.put("referenceId", raw_items.getJSONObject(i).get("referenceId"));
                itemJson.put("presentationIds", raw_items.getJSONObject(i).get("presentationIds"));
                itemJson.put("productIds", raw_items.getJSONObject(i).get("productIds"));
                items.put(itemJson);
            }
            result.put("items",items);
            result.put("results", json.get("totalItems"));
        } catch (JSONException je) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));

            result =error;
        } catch (IOException e) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

            result = error;
        }
        return result;
    }
    public static boolean cachePublications(){
        boolean result = false;
        SlingRepository repository = getSlingRepository();
        Session session = null;
        loggerVar.trace("cachePublications");
        try {
            session = repository.loginAdministrative(null);
            int page = 0;
            JSONArray items = new JSONArray();
            while (page == 0 || items.length() > 0) {
                JSONObject publications = getPublicationsByPage(page, 4000);
                items = publications.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    loggerVar.trace("publications : " + i);
                    JSONObject item = items.getJSONObject(i);
                    loggerVar.trace("publications object : " + item.toString(1));
                    String hash = Text.digest("MD5", item.toString().getBytes());
                    loggerVar.trace("hash : " + hash);
                    String path = getStoragePath()+"/publications/publication-" + item.getString("rid");
                    Node content = createPageContent(path, session);
                    loggerVar.trace("Node: " + content.getPath());
                    if (content != null) {
                        if (!content.hasProperty("hash") || !hash.equals(content.getProperty("hash").getString())) {
                            content.setProperty("id", item.getString("id"));
                            content.setProperty("rid", item.getString("rid"));
                            content.setProperty("hash", hash);
                            content.setProperty("siteKey", item.getString("siteKey"));
                            content.setProperty("publicationStartDate", item.getString("publicationStartDate"));
                            content.setProperty("jcr:title", item.getString("rid"));
                            content.setProperty("publicationEndDate", item.getString("publicationEndDate"));
                            content.setProperty("content_type", "publication");

                            Value[] productIds_values = new Value[item.getJSONArray("productIds").length()];
                            ValueFactory valueFactory = session.getValueFactory();
                            for (int iCount = 0; iCount < item.getJSONArray("productIds").length(); iCount++) {
                                String id = item.getJSONArray("productIds").getString(iCount);
                                productIds_values[iCount] = valueFactory.createValue(id);
                            }
                            content.setProperty("productIds", productIds_values);


                            Value[] presentationIds_values = new Value[item.getJSONArray("presentationIds").length()];
                            for (int iCount = 0; iCount < item.getJSONArray("presentationIds").length(); iCount++) {
                                String presId = item.getJSONArray("presentationIds").getString(iCount);
                                Node presNode =  null;
                                String contentPath = getStoragePath()+"/presentations/presentation-" + presId+"/jcr:content";
                                loggerVar.trace("Pres Node: " + contentPath);

                                try {
                                    presNode = session.getNode(contentPath);
                                    if (presNode != null)  {
                                        for (Value productId: productIds_values) {
                                            Node prodNode =  null;

                                            String productNodeName = "product-"+productId.getString();
                                            String productPath = getStoragePath()+"/products/" + productNodeName+"/jcr:content";
                                            try {
                                                prodNode = session.getNode(productPath);
                                                if (prodNode != null) {
                                                    Node refProd = presNode.hasNode(productNodeName) ? presNode.getNode(productNodeName) : presNode.addNode(productNodeName);
                                                    refProd.setProperty("hash", prodNode.getProperty("hash").getString());
                                                    refProd.setProperty("brand", prodNode.getProperty("brand").getString());
                                                    refProd.setProperty("mpd", prodNode.getProperty("mpd").getString());
                                                    refProd.setProperty("title", prodNode.getProperty("title").getString());
                                                    refProd.setProperty("rid", prodNode.getProperty("rid").getString());
                                                    refProd.setProperty("description", prodNode.getProperty("description").getString());
                                                }
                                            } catch (PathNotFoundException e) {
                                                loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
                                            }

                                        }
                                        presNode.save();
                                    } else {
                                        loggerVar.trace("Pres Node NOT FOUND: " + contentPath);
                                    }
                                } catch (PathNotFoundException pnfe) {
                                    presNode = null;
                                    loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(pnfe));

                                }
                                presentationIds_values[iCount] = valueFactory.createValue(presId);
                            }
                            content.setProperty("presentationIds", presentationIds_values);


                            content.setProperty("referenceId", item.getString("referenceId"));
                            content.save();
                            loggerVar.trace("Saved Node: " + path);

                        } else {
                            loggerVar.trace("Same Node: " + path + ", Original HASH: " + content.getProperty("hash").getString() + ", New HASH: " + hash);
                        }
                    }
                }
                page++;
                session.save(); //maximum every 1000 nodes
            }
            session.save();

        } catch (Exception je) {
            //to-do
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));
        } finally {
            if (session != null) {
                session.logout();
            }
            if (repository != null) repository = null;
        }
        return result;
    }
    ////PRODUCTS
    private static JSONObject getProductsByPage(int page, int limit) {
        return getProducts(page, limit, true);
    }
    private static JSONObject getProducts(int startVal, int limit, boolean byPage) {
        JSONObject result = new JSONObject();
        JSONArray items = new JSONArray();
        JSONObject error = new JSONObject();
        int totalResults = 0;
        try {
            error =new JSONObject("{\"error\":[],\"results\":0}");
            String affiliate_id= getAffiliateId();
            String api_key= getApiKey();
            String url = "https://feed.invodo.com/v4/products?siteKey="+affiliate_id+"&apiKey="+api_key;
            if (limit>0) {
                int page = 0;
                url += "&resultsPerPage=" + limit;
                if (byPage) {
                    url += "&page=" + startVal;
                } else {
                    if (startVal > -1) {
                        page = (int) Math.floor((startVal + 1) / limit);
                        url += "&page=" + page;
                    }
                }
            }
            result.put("url",url);
            JSONObject json = JsonReader.readJsonFromUrl(url);
            JSONArray raw_items =  json.getJSONArray("items");
            for(int i = 0; i<raw_items.length();i++){
                JSONObject itemJson = new JSONObject();
                itemJson.put("id",startVal+i);
                itemJson.put("rid",raw_items.getJSONObject(i).get("id"));
                itemJson.put("title", raw_items.getJSONObject(i).get("title"));
                itemJson.put("description",   raw_items.getJSONObject(i).get("description"));
                itemJson.put("mpd",   raw_items.getJSONObject(i).get("mpd"));
                itemJson.put("brand", raw_items.getJSONObject(i).get("brand"));
                items.put(itemJson);
            }
            result.put("items",items);
            result.put("results", json.get("totalItems"));
        } catch (JSONException je) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));

            result =error;
        } catch (IOException e) {
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

            result = error;
        }
        return result;
    }
    public static boolean cacheProducts(){
        boolean result = false;
        SlingRepository repository = getSlingRepository();
        Session session = null;
        loggerVar.trace("cacheProducts");
        try {
            session = repository.loginAdministrative(null);
            int page = 0;
            JSONArray items = new JSONArray();
            while (page == 0 || items.length() > 0) {
                JSONObject publications = getProductsByPage(page, 4000);
                items = publications.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    loggerVar.trace("products : " + i);
                    JSONObject item = items.getJSONObject(i);
                    loggerVar.trace("products object : " + item.toString(1));
                    String hash = Text.digest("MD5", item.toString().getBytes());
                    loggerVar.trace("hash : " + hash);
                    String path = getStoragePath()+"/products/product-" + item.getString("rid");
                    Node content = createPageContent(path, session);
                    loggerVar.trace("Node: " + content.getPath());
                    if (content != null) {
                        if (!content.hasProperty("hash") || !hash.equals(content.getProperty("hash").getString())) {
                            content.setProperty("id", item.getString("id"));
                            content.setProperty("rid", item.getString("rid"));
                            content.setProperty("hash", hash);
                            content.setProperty("title", item.getString("title"));
                            content.setProperty("description", item.getString("description"));
                            content.setProperty("jcr:title", item.getString("title"));
                            content.setProperty("mpd", item.getString("mpd"));
                            content.setProperty("content_type", "product");
                            content.setProperty("brand", item.getString("brand"));
                            content.save();
                            loggerVar.trace("Saved Node: " + path);

                        } else {
                            loggerVar.trace("Same Node: " + path + ", Original HASH: " + content.getProperty("hash").getString() + ", New HASH: " + hash);
                        }
                    }
                }
                page++;
                session.save(); //maximum every 1000 nodes
            }
            session.save();

        } catch (Exception je) {
            //to-do
            loggerVar.error(org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(je));
        } finally {
            if (session != null) {
                session.logout();
            }
            if (repository != null) repository = null;
        }
        return result;
    }
}
