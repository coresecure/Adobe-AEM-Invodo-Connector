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

InvodoAEM = {};
InvodoAEM.Combo = CQ.Ext.extend(CQ.Ext.form.ComboBox, {

    /**
     * @cfg {String} url
     * The URL where the search request is sent to (defaults to "/content.search.json").
     */

    /**
     * @cfg {Object} store
     * @deprecated Use {@link #storeConfig} instead
     */

    /**
     * @cfg {Object} storeConfig
     * <p>The configuration of the store the SearchField is bound to. To simply
     * overwrite the URL of the store's proxy use the {@link #url} config option.</p>
     * <p>The default store's reader consumes a JSON of the following format:</p>
     * <pre><code>
{
    "hits":[
        {
        "name": "sample",
        "path": "/content/sample",
        "excerpt": "",
        "title": "Sample Page"
        }
     ],
     "results":1
}
</code></pre>
      */
    storeConfig: null,

	constructor : function(config) {
		config = CQ.Util.applyDefaults(config, {
			"width": 300,
			"autoSelect": true,
			"mode":"remote",
            "pageSize": 20,
			"minChars": 0,
			"typeAhead": false,
			"typeAheadDelay": 100,
			"validationEvent": false,
			"validateOnBlur": false,
			"displayField": "durableId",
			"valueField":"durableId",
			"triggerAction": 'query',
			"emptyText": CQ.I18n.getMessage("Enter search query"),
			"loadingText": CQ.I18n.getMessage("Searching..."),
			"tpl": new CQ.Ext.XTemplate(
				'<tpl for=".">',
					'<div class="search-item" qtip="{durableId}">',
						'<div class="search-thumb"',
                        ' style="background-image:url({[values.thumbnail_image]});"></div>' +
                        '<div class="search-text-wrapper">' +
                            '<div class="search-title">{name}</div>',
                            '<div class="search-excerpt">{durableId}</div>',
                        '</div>',
                    '<div class="search-separator"></div>',
					'</div>',
				'</tpl>'),
			"itemSelector": "div.search-item"
		});
		var storeConfig = CQ.Util.applyDefaults(config.storeConfig, {
			"proxy":new CQ.Ext.data.HttpProxy( {
				"url" :"/bin/invodo/list.json",
				"method" :"GET"
			}),
			"baseParams": {
				"_charset_": "utf-8"
			},
			"reader":new CQ.Ext.data.JsonReader({
				"id":"id",
				"root":"items",
				"totalProperty":"results",
				"fields" : [ "id","durableId","name","thumbnail_image" ]
			})
		});
		config.store = new CQ.Ext.data.Store(storeConfig);
		InvodoAEM.Combo.superclass.constructor.call(this, config);
	},
	asyncSetDisplayValue: function(v){
		var value = CQ.Ext.isEmpty(v) ? '' : v;
		var combo = this;
		this.store.baseParams[this.queryParam] = value;
		this.store.baseParams['isID'] = true;
		var success = this.store.load({
			params: this.getParams(value),
			callback: function() {
						combo.setDisplayValue(combo.value, false);
						if (value != combo.value) 			combo.setValue(value);
			}
		});
		if (!success) {
			// Load was cancelled, so we'll just have to make do with the valueField:
			combo.setDisplayValue(combo.value, false);

		}
		this.store.baseParams['isID'] = false;
		
	} 
	
	
});
CQ.Ext.reg ( "InvodoCombo", InvodoAEM.Combo);