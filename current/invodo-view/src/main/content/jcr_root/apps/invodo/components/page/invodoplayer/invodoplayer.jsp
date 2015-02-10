<%
/*    
    Adobe CQ5 Invodo Connector  
    
    Copyright (C) 2011 Coresecure Inc.
        
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
%>
<%@ page contentType="text/html"
             pageEncoding="utf-8"
             import="com.day.cq.widget.HtmlLibraryManager,
                     com.day.text.Text,
                     java.util.Iterator,
                     com.day.cq.wcm.api.Page,
                     java.io.IOException,
                     com.day.cq.wcm.foundation.ParagraphSystem,
                     com.day.cq.wcm.foundation.Paragraph,
                     com.day.cq.wcm.api.components.IncludeOptions,
                     java.util.ResourceBundle,
                     com.day.cq.i18n.I18n" %>
<%@ page import="com.day.cq.wcm.api.NameConstants" %>
<%!
%><%@include file="/libs/foundation/global.jsp"%><%
    
    final ResourceBundle resourceBundle = slingRequest.getResourceBundle(null);
    I18n i18n = new I18n(resourceBundle);  

    String segmentPath = Text.getRelativeParent(resource.getPath(), 1);
    String parentPath = Text.getRelativeParent(segmentPath, 1);
    String parentName = Text.getName(parentPath);
    boolean canVisitParent = false;
    try {
        Item item = currentNode.getSession().getItem(parentPath + "/jcr:primaryType");
        if(item != null) {
            canVisitParent = NameConstants.NT_PAGE.equals(((Property) item).getString());
        }
    } catch (Exception e) {
        //do nothing
    }

    String title = properties.get("jcr:title", Text.getName(segmentPath));
    String descr = properties.get("jcr:description", "");
    String width = "480";
    String height = "270";
    
    ValueMap playerProperties = currentPage.getProperties();

    if (playerProperties.containsKey("width") && playerProperties.containsKey("height")) {

        width = playerProperties.get("width", String.class);
        height = playerProperties.get("height", String.class);
    } else if (playerProperties.containsKey("width") && !playerProperties.containsKey("height")) {
        width = playerProperties.get("width", String.class) ;
        height =String.valueOf(270 * playerProperties.get("width", 1) / 480);

    } else if (!playerProperties.containsKey("width") && playerProperties.containsKey("height")) {
        height = playerProperties.get("height", String.class) ;
        width  =String.valueOf(480 * playerProperties.get("height", 1) / 270);
    } 

%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>
<head>
    <title>Invodo Player | <%= title %></title>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <%
    HtmlLibraryManager htmlMgr = sling.getService(HtmlLibraryManager.class);
    if (htmlMgr != null) {
        htmlMgr.writeIncludes(slingRequest, out, "cq.wcm.edit", "cq.tagging", "cq.personalization", "cq.security");
    }
    String dlgPath = null;
    if (editContext != null && editContext.getComponent() != null) {
        dlgPath = editContext.getComponent().getDialogPath();
    }
    %>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
    <script type="text/javascript" >
        CQ.WCM.launchSidekick("<%= currentPage.getPath() %>", {
            propsDialog: "<%= dlgPath == null ? "" : dlgPath %>",
            locked: <%= currentPage.isLocked() %>
        });
    </script>
</head>
<body>
    <cq:include path="clientcontext" resourceType="cq/personalization/components/clientcontext"/>

    <h1><%= title %><p><%= descr %></p></h1>
    
    <div class="definition-container"><%
        %><%
    %></div>

    
   

	<h2 class="no-icon"><%=i18n.get("Player ID") %></h2>
    <p><%=i18n.get("Use the Page Properties editor to edit the Player ID.") %></p>
    <div class="edit-box" style="width:75%">
        <cq:text property="playerID" placeholder="<%=i18n.get("NONE") %>" tagName="p"/>
    </div>

	<h2 class="no-icon"><%=i18n.get("Player Key") %></h2>
    <p><%=i18n.get("Use the Page Properties editor to edit the Player Key.") %></p>
    <div class="edit-box" style="width:75%">
        <cq:text property="playerKey" placeholder="<%=i18n.get("NONE") %>" tagName="p"/>
    </div>
    
     <h2 class="no-icon"><%=i18n.get("Player Preview") %></h2>
	    <div class="edit-box" style="width:75%">
	        <% if (!width.isEmpty() && !height.isEmpty()) {%>
	            <!-- DO NOT USE!!!! FOR PREVIEW PURPOSES ONLY. -->
	            
	            <!-- Start of Invodo Player -->
	            
	            <div style="display:none">
	            
	            </div>
	            
	            <!--
	            By use of this code snippet, I agree to the Invodo Publisher T and C 
	            found at https://accounts.invodo.com/en/terms-and-conditions/. 
	            -->
	            
	            <script language="JavaScript" type="text/javascript" src="https://sadmin.invodo.com/js/InvodoExperiences.js"></script>
	            
	            <object id="myExperience" class="InvodoExperience">
	              <param name="bgcolor" value="#FFFFFF" />
	              <param name="width" value="<%=width %>" />
	              <param name="height" value="<%=height %>" />
	              <param name="playerID" value="<%=properties.get("playerID","") %>" />
	              <param name="playerKey" value="<%=properties.get("playerKey","") %>" />
	              <param name="isVid" value="true" />
	              <param name="isUI" value="true" />
	              <param name="dynamicStreaming" value="true" />
	              <param name="cacheAMFURL" value="https://share.invodo.com/services/messagebroker/amf"/><param name="secureConnections" value="true" />
	            </object>
	            
	            <!-- 
	            This script tag will cause the Invodo Players defined above it to be created as soon
	            as the line is read by the browser. If you wish to have the player instantiated only after
	            the rest of the HTML is processed and the page load is complete, remove the line.
	            -->
	            <script type="text/javascript">invodo.createExperiences();</script>
	            <%} %>
	    </div>
    
</body>
</html>