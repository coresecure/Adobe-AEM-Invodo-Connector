invodoTools={};
invodoTools.videos=[];
invodoTools.filepath=false;
invodoTools.pageName="";
invodoTools.pageType="";

invodoTools.init = function() {
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
					var index;
					for (index = 0; index < invodoTools.videos.length; ++index) {
    					var item = invodoTools.videos[index];
                        if (item.type === "cta" ||  item.mode === "overlay") {
                            Invodo.Widget.add({podId: item.podId, widgetId:item.widgetId, mode:"overlay", type:"inplayer", chromelessmode:item.chromelessmode, listensTo:"cta"+index});
	                        Invodo.Widget.add({podId: item.podId, widgetId:"cta"+index, type:"cta", chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, data:item.data});
                        } else if (item.type === "spin") {
							Invodo.Widget.add({podId: item.podId, widgetId:item.widgetId, parentDomId:item.parentDomId, showControls:item.showControls, type:item.type});
                        } else {
                        	Invodo.Widget.add({podId: item.podId, widgetId:item.widgetId, mode:item.mode, type:item.type, chromelessmode:item.chromelessmode, parentDomId:item.parentDomId, autoplay:item.autoplay});
                        }
					}
                  // Handle memory leak in IE
                  fileref.onload = fileref.onreadystatechange = null;
                  if (head && fileref.parentNode) {
                      head.removeChild(fileref);
                  }
              }
          };

          fileref.setAttribute("type", "text/javascript");
          fileref.setAttribute("src", filepath);

          head.appendChild(fileref);
      }
  }

invodoTools.addVideoCue = function(a_podId,a_widgetId,a_parentDomId,a_type, a_mode, a_chromeless, a_autoplay, a_data) {
    invodoTools.videos.push({podId:a_podId, widgetId:a_widgetId, parentDomId:a_parentDomId, type:a_type, mode:a_mode, autoplay:a_autoplay, chromelessmode:a_chromeless, data:a_data});
}

invodoTools.addSpinCue = function(a_podId,a_widgetId,a_parentDomId,a_type, a_introSpin, a_showControls) {
    invodoTools.videos.push({podId:a_podId, widgetId:a_widgetId, parentDomId:a_parentDomId, type:a_type, introSpin:a_introSpin, showControls:a_showControls});
}


$( window ).load(function() {
	invodoTools.init();
});