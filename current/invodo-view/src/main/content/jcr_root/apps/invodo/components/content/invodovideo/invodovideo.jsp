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
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.components.DropTarget,java.util.UUID,com.coresecure.invodo.aem.*,com.day.cq.wcm.foundation.Image"%>
<%
    UUID video_uuid = new UUID(64L,64L);
	String VideoRandomID = new String(video_uuid.randomUUID().toString().replaceAll("-",""));
	String selection_mode= properties.get("selection_mode","");

	Image image = null;
    String imageCTAData="/etc/designs/cs/invodo/InvodoExperiences/images/playbutton_black_64px.png";
    if (currentNode.hasNode("ctaimage")) {
        image = new Image(resourceResolver.getResource(currentNode.getNode("ctaimage").getPath()));
        if (image.hasContent()) imageCTAData = resourceResolver.map(image.getPath()+".img.png"+ image.getSuffix());
    }

    String videoPlayer = "";
	String type = "embedded".equals(properties.get("mode","embedded")) ? "inplayer" : "cta";
	long width = properties.get("width",-1);
    long height = properties.get("height",-1);
    if (width > 0 && !(height > 0)) {
        height = ConfigurationUtil.getHeight(videoPlayer,width);
    } else if (height > 0 && !(width > 0)) {
        width = ConfigurationUtil.getWidth(videoPlayer,height);
    }

	if("manual".equals(selection_mode)){
        videoPlayer = properties.get("videoPlayer","");
    } else if ("mpd".equals(selection_mode)){
        //Get the mpd from the page properties.
        String mpdProperty = properties.get("mpdproperty","").trim();
        String mpdPageProperty =currentPage.getProperties().get("mpd","").trim();
        if (!mpdProperty.isEmpty()) {
            videoPlayer = mpdProperty;
        } else if (!mpdPageProperty.isEmpty()) {
			videoPlayer = mpdPageProperty;
        }
    } else if ("refid".equals(selection_mode)){
        //Get the mpd from the page properties.
        String refidproperty = properties.get("refidproperty","").trim();
        String refidPageProperty =currentPage.getProperties().get("refid","").trim();
        if (!refidproperty.isEmpty()) {
            videoPlayer = refidproperty;
        } else if (!refidPageProperty.isEmpty()) {
			videoPlayer = refidPageProperty;
        }
    } 

%>
<cq:includeClientLib js="ivd.InvodoExperiences-custom"/>

<script type="text/javascript">

    var filepath = "<%=ConfigurationUtil.getJSPath()%>";
	invodoTools.filepath=filepath;
    invodoTools.pageName="<%=currentPage.getProperties().get("invodo_pageName",currentPage.getName())%>";
    invodoTools.pageType="<%=currentPage.getProperties().get("pageType","product")%>";
    invodoTools.addVideoCue("<%=videoPlayer%>","player1-<%=VideoRandomID%>","<%=VideoRandomID%>","<%=type%>","<%=properties.get("mode","embedded")%>","<%=properties.get("chromelessmode",false)%>",<%=properties.get("autoplay",false)%>,"<%=imageCTAData%>","<%=selection_mode%>",<%=width%>,<%=height%>);

 </script>


	<div class="invodo-video-container" id="<%=VideoRandomID%>"></div>
