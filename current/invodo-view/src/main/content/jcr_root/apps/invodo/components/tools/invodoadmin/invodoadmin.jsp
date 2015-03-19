<%
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
%><%@ page contentType="text/html"
             pageEncoding="utf-8"
             import="com.day.cq.wcm.foundation.Image,
    com.day.cq.wcm.api.components.DropTarget,
    com.day.cq.wcm.api.components.EditConfig,
    com.day.cq.wcm.commons.WCMUtils,
    com.day.cq.replication.Replicator,
    com.day.cq.replication.Agent,
    com.day.cq.replication.AgentConfig,
    com.day.cq.widget.HtmlLibraryManager,
    com.day.cq.wcm.api.WCMMode,
    com.day.cq.wcm.api.components.Toolbar,
    com.day.cq.replication.ReplicationQueue,
    com.day.cq.replication.AgentManager, java.util.Iterator,com.day.cq.widget.WidgetExtensionProvider" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
int debug = 0;
WidgetExtensionProvider extensionProvider = sling.getService(WidgetExtensionProvider.class);
String xtype = properties.get("xtype", "");
    Session session = slingRequest.getResourceResolver().adaptTo(Session.class);

    String extensionString = extensionProvider.getJsonString(xtype, session);
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; utf-8" />
    <cq:includeClientLib categories="cq.wcm.edit" />
    <cq:includeClientLib css="cs.invodo-console" />

    <cq:includeClientLib js="cs.invodo-console" />
    <title>Invodo - Media API Application</title>

</head>

<body>
    <div id="CQ"></div>
    <div id="invodo">
        <iframe id="aeminvodo" src="https://my.invodo.com/home/shelby.html#login:" width="100%" height="100%"/>
    </div>
</body>

</html>