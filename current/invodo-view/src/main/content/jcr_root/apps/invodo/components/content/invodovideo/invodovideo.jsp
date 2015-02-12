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
<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.components.DropTarget,java.util.UUID,com.coresecure.invodo.aem.*,com.day.cq.wcm.foundation.Image"%>
<%
    UUID video_uuid = new UUID(64L,64L);
	String VideoRandomID = new String(video_uuid.randomUUID().toString().replaceAll("-",""));

	Image image = null;
    String imageCTAData="/etc/designs/cs/invodo/InvodoExperiences/images/playbutton_black_64px.png";
    if (currentNode.hasNode("ctaimage")) {
        image = new Image(resourceResolver.getResource(currentNode.getNode("ctaimage").getPath()));
        if (image.hasContent()) imageCTAData = resourceResolver.map(image.getPath()+".img.png"+ image.getSuffix());
    }

%>
<cq:includeClientLib js="ivd.InvodoExperiences-custom"/>

<script type="text/javascript">

    var filepath = "//e.invodo.com/4.0/s/hpotter.com.js";
	invodoTools.filepath=filepath;
    invodoTools.pageName="<%=currentPage.getName()%>";
    invodoTools.pageType="<%=currentPage.getTemplate().getName()%>";
    //a_podId, a_widgetId, a_parentDomId, a_type, a_mode, a_chromeless, a_autoplay
    invodoTools.addVideoCue("<%=properties.get("videoPlayer","")%>","player1-<%=VideoRandomID%>","<%=VideoRandomID%>","<%=properties.get("type","inplayer")%>","<%=properties.get("mode","embedded")%>","<%=properties.get("chromelessmode",false)%>",<%=properties.get("autoplay",false)%>,"<%=imageCTAData%>");

 </script>

<%
	if ("cta".equals(properties.get("type","inplayer")) || "overlay".equals(properties.get("mode","embedded"))) {
%>
	<p id="<%=VideoRandomID%>"></p>
<%
    } else {
%>
	<div id="<%=VideoRandomID%>" style="height:<%=properties.get("height","270")%>px;width:<%=properties.get("width","480")%>px;"></div>
<%
    }
%>