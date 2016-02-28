/**
 * 
 */
define(["require", "exports"], function(require, exports) {
	var styleOptions = {};
	var writeTable = function(placeHolder, title, options, data, styleOptions) {
		var html = '';
		if(!!title)
			html += '<h2>'+title+'</h2>'
		html += '<table width="100%" cellspacing="0" cellpadding="0" border="0">';
		html += "<tr>";
		for(var currentIndex=0; currentIndex<options.attributes.length; currentIndex++) {
			html+="<td align=\"right\">&nbsp;</td>";
		}
		html += "<td align=\"center\">";
		html += '<button id="createNewItemButton" type="button" class="btn btn-primary action-button" data-toggle="modal" data-target=".edit-mongoobject-modal">Create New</button>'
		html += "</td></tr>\n";
		html +='<tr>';
		options.attributes.forEach(function(key) {
			html += '<th align="left"><label class="table-th">'+options.titles[key]+'</label></th>';
		});
		html +='<th align="left"><label class="table-th">actions</label></th></tr>\n';
		var itemObjectIndexes = [];
		if (data) {
			if (data.length) {
				data.forEach(function(item) {
					itemObjectIndexes.push(item[options.typeId]);
					html +="<tr id=\"item_"+item[options.typeId]+"\">";
					options.attributes.forEach(function(key) {
						var value = item[key];
						if (options.dataTypes[key] === 'badge') {
							value="";
							item[key].forEach(function(tag) {value+=(value.length>0?", ":"")+tag;});
						}
						html +="<td align=\"center\">"+value+"</td>";
					});
					
					html += '<td align="right" style="min-width: 140px;">';
					//edit button
					html += '<button id="editItemButton_'+item[options.typeId]+'" type="button" class="btn btn-primary action-button" data-toggle="modal" data-target=".edit-mongoobject-modal">Edit</button>'
					html += '<button id="deleteItemButton_'+item[options.typeId]+'" type="button" class="btn btn-default action-button" data-toggle="modal" data-target=".delete-mongoobject-modal">Delete</button>'
					html += "</td>";
					html += "</tr>\n";
					
				});
			}
			else {
				html += "<tr><td colspan=\""+(options.attributes.length + 1)+"\" align=\"center\">";
				html += 'No data found ...'
				html += "</td></tr>\n";

			}
		}
		else {
			html += "<tr><td colspan=\""+(options.attributes.length + 1)+"\" align=\"center\">";
			html += 'No data found ...'
			html += "</td></tr>\n";

		}
		html += "<tr><td colspan=\""+(options.attributes.length + 1)+"\" align=\"right\">";
		html += 'Results : ' + data.length + " <&nbsp;&nbsp;";
		html += "</td></tr>\n";
		html += '</table>';
		$("#"+placeHolder).html(html);
		$('#' + placeHolder + ' #createNewItemButton').unbind("click");
		$('#' + placeHolder + ' #createNewItemButton').on("click", function() {
			options.newActionHandler();
		});
		itemObjectIndexes.forEach(function(itemObjectId) {
			$('#' + placeHolder + ' #editItemButton_'+itemObjectId).unbind("click");
			$('#' + placeHolder + ' #editItemButton_'+itemObjectId).on("click", function() {
				options.updateActionHandler(itemObjectId);
			});
			$('#' + placeHolder + ' #deleteItemButton_'+itemObjectId).unbind("click");
			$('#' + placeHolder + ' #deleteItemButton_'+itemObjectId).on("click", function() {
				options.deleteActionHandler(itemObjectId);
			});
		});
	};
	return function(placeHolderId, options){
		return {
			options: options,
			placeHolder: placeHolderId,
			styles: styleOptions,
			title: null,
			data: [],
			setData: function(data) {
				this.data = data;
			},
			redraw: function() {
				writeTable(this.placeHolder, this.title, this.options, this.data, this.styles);
			},
		}
	};
});
