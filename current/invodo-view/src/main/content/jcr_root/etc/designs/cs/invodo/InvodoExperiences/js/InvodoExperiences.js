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
invodoTools={};
invodoTools.videos=[];
invodoTools.filepath=false;
invodoTools.pageName="";
invodoTools.pageType="";

invodoTools.createWidget = function() {
	var index;
    for (index = 0; index < invodoTools.videos.length; ++index) {
        var item = invodoTools.videos[index];
        if (item.type === "spin") {
            $("#"+item.parentDomId).css("width",item.width);
            $("#"+item.parentDomId).css("height",item.height);
            Invodo.Widget.add({podId: item.Id, widgetId:item.widgetId, parentDomId:item.parentDomId, showControls:item.showControls, type:item.type});
        } else if (item.type == "inplayer") {
            if (item.mode === "overlay") {
                if ("mpd".equals(item.selection_mode)) {
                    Invodo.Widget.add({mpd: item.Id, widgetId:item.widgetId, mode:{name:"overlay", overlayWidth:item.width, overlayHeight:item.height}, type:"inplayer", chromelessmode:item.chromelessmode, listensTo:"cta"+index});
                    Invodo.Widget.add({mpd: item.Id, widgetId:"cta"+index, type:"cta", chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, data:item.data});
                } else if ("manual".equals(item.selection_mode)){
                    Invodo.Widget.add({podId: item.Id, widgetId:item.widgetId, mode:{name:"overlay", overlayWidth:item.width, overlayHeight:item.height}, type:"inplayer", chromelessmode:item.chromelessmode, listensTo:"cta"+index});
                    Invodo.Widget.add({podId: item.Id, widgetId:"cta"+index, type:"cta", chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, data:item.data});
                } else if ("refid".equals(item.selection_mode)) {
                    Invodo.Widget.add({refId: item.Id, widgetId:item.widgetId, mode:{name:"overlay", overlayWidth:item.width, overlayHeight:item.height}, type:"inplayer", chromelessmode:item.chromelessmode, listensTo:"cta"+index});
                    Invodo.Widget.add({refId: item.Id, widgetId:"cta"+index, type:"cta", chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, data:item.data});
                }
            } else {
                $("#"+item.parentDomId).css("width",item.width);
                $("#"+item.parentDomId).css("height",item.height);
                if (item.hasMPD) {
                    Invodo.Widget.add({mpd: item.Id, widgetId:item.widgetId, mode:item.mode, type:item.type, chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, autoplay:item.autoplay});
                } else { 
                    Invodo.Widget.add({podId: item.Id, widgetId:item.widgetId, mode:item.mode, type:item.type, chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, autoplay:item.autoplay});
                }
            }
        }
    }
    // Handle memory leak in IE
    fileref.onload = fileref.onreadystatechange = null;
    if (head && fileref.parentNode) {
        head.removeChild(fileref);
    }
}

invodoTools.init = function() {
      if (typeof Invodo !== "undefined") {
	  	  Invodo.init({
              pageName: invodoTools.pageName,
              pageType: invodoTools.pageType
          });
          invodoTools.createWidget();
      } else {
          if (invodoTools.filepath) {
              var fileref = document.createElement('script');
              var done = false;
              var head = document.getElementsByTagName("head")[0];
    
              fileref.onload = fileref.onreadystatechange = function () {
                  if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                      done = true;
                      Invodo.init({
                            pageName: invodoTools.pageName,
                            pageType: invodoTools.pageType
                      });
                      invodoTools.createWidget();
                  }
              };
    
              fileref.setAttribute("type", "text/javascript");
              fileref.setAttribute("src", filepath);
    
              head.appendChild(fileref);
          }
      }
  }

invodoTools.addVideoCue = function(a_Id,a_widgetId,a_parentDomId,a_type, a_mode, a_chromeless, a_autoplay, a_data, a_selection_mode, a_width, a_height) {
    invodoTools.videos.push({Id:a_Id, widgetId:a_widgetId, parentDomId:a_parentDomId, type:a_type, mode:a_mode, autoplay:a_autoplay, chromelessmode:a_chromeless, data:a_data, selection_mode: a_selection_mode, width:a_width, height: a_height});
}

invodoTools.addSpinCue = function(a_Id,a_widgetId,a_parentDomId,a_type, a_introSpin, a_showControls, a_width, a_height) {
    invodoTools.videos.push({Id:a_Id, widgetId:a_widgetId, parentDomId:a_parentDomId, type:a_type, introSpin:a_introSpin, showControls:a_showControls, width:a_width, height: a_height});
}

$( window ).load(function() {
	invodoTools.init();
});