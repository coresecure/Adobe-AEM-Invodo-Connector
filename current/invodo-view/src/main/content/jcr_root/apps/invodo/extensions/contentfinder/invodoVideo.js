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

{
    "tabTip": CQ.I18n.getMessage("Invodo"),
    "id": "cfTab-Invodo",
    "iconCls": "cq-cft-tab-icon invodo",
    "xtype": "contentfindertab",
    "ranking": 1,
    "allowedPaths": [
                     "/content/*",
                     "/etc/scaffolding/*",
                     "/etc/workflow/packages/*"
                 ],
    "items": [
		CQ.wcm.ContentFinderTab.getQueryBoxConfig({
            "id": "cfTab-Invodo-QueryBox",
            /*"height": 95,*/
            "items": [
                CQ.wcm.ContentFinderTab.getSuggestFieldConfig({"url": "/bin/wcm/contentfinder/suggestions.json/content/dam"})
                 /*
                ,
                {
                    "xtype": "combo",
                    "name": "size",
                    "value": CQ.I18n.getMessage("Any format"),
                    "mode": "local",
                    "store": new CQ.Ext.data.SimpleStore({
                        "fields": ["value", "text"],
                        "data": [
                            {
                                "text": CQ.I18n.getMessage("Any Format"),
                                "value": "image"
                            },{
                                "text": CQ.I18n.getMessage("Web Graphics (GIF)"),
                                "value": "image/gif"
                            },{
                                "text": CQ.I18n.getMessage("Photographs (JPEG)"),
                                "value": "image/jpeg"
                            },{
                                "text": CQ.I18n.getMessage("Web Images (PNG)"),
                                "value": "image/png"
                            }
                        ]
                    })
                }
                */
            ]
        }),
        CQ.wcm.ContentFinderTab.getResultsBoxConfig({
            "itemsDDGroups": [CQ.wcm.EditBase.DD_GROUP_ASSET],
            "itemsDDNewParagraph": {
                "path": "invodo/components/content/invodovideo",
                "propertyName": "./videoPlayer"
            },
            "noRefreshButton": true,
            "items": {
                "tpl":
                    '<tpl for=".">' +
                '<div class="cq-cft-search-item" title="{thumbnail_image}" ondblclick="window.location= \'{url}\';">' +
                                    '<div class="cq-cft-search-thumb-top"' +
                                    ' style="background-image:url(\'{thumbnail_image}\');"></div>' +
                                         '<div class="cq-cft-search-text-wrapper">' +
                                            '<div class="cq-cft-search-title"><p class="cq-cft-search-title">{name}</p><p>{durableId}</p></div>' +
                                        '</div>' +
                                    '<div class="cq-cft-search-separator"></div>' +
                            '</div>' +
                    '</tpl>',
                "itemSelector": CQ.wcm.ContentFinderTab.DETAILS_ITEMSELECTOR
            },
            "tbar": [
                CQ.wcm.ContentFinderTab.REFRESH_BUTTON,"->"
            ]
        },{
            "url": "/bin/invodo/list.sidekick.json"
        },{
            "autoLoad":false,
            "reader": new CQ.Ext.data.JsonReader({
                "root": "items",
                "fields": [
                    "id", "name", "durableId", "path", "thumbnail_image", "url"
                ],
                "id": "path"
                
            })
        
        })
    ]

}