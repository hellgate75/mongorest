/**
 * 
 */
define(["require", "exports"], function(require, exports) {
	var styleOptions = {};
	var writeForm = function(placeHolder, title, options, styleOptions) {
		var html = '';
		if(!!title)
			html += '<h3>'+title+'</h3>'
			html += '<table width="100%" cellspacing="0" cellpadding="0" border="0">';
		var badgeIndexes=[];
		options.attributes.forEach(function(key) {
			html+='	    <tr>\n';
			html+='      		<td width="30%" align="right">'+options.titles[key]+' :<span class="input-spacer"></span></td>\n';
			html+='      		<td width="70%" align="left">';
			if(options.editableModifier[key]) {
				if (options.dataTypes[key] === 'badge') {
					badgeIndexes.push(key);
					html+='<table border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td width="100%"><input id=\"newTag_'+key+'\" type="text" class="input-data-button"></input>';
					html+='<button id="tag_add_'+key+'" key="'+key+'" type="button" class="btn badge-button">Add</button>';
					html+='<button id="tag_removeAll_'+key+'" key="'+key+'" type="button" class="btn badge-button">Clear</button>';
					html+='</td></tr><tr><td width="100%"><ul id="tagList_'+key+'" class="list-group"></ul></td></tr></table>';

				}
				else {
					if(options.dataTypes[key] === 'integer') {
						html+='<input id="field_'+key+'" type="number" pattern=" pattern="[0-9]+" step="1" class="input-data"></input>';
					}
					else if(options.dataTypes[key] === 'integer' || options.dataTypes[key] === 'double') {
						html+='<input id="field_'+key+'" type="number" pattern="[0-9]+([\.,][0-9]+)?" step="0.01" class="input-data"></input>';
					}
					else {
						html+='<input id="field_'+key+'" type="text" class="input-data"></input>';
					}
				}
			}
			else {
				html+='<label id="field_'+key+'"></label>';
			}
			html+='</td>\n';
			html+='	    </tr>\n';
		});
		html+='</table>';
		$('#'+placeHolder).html(html);
		badgeIndexes.forEach(function(code){
			$('#'+placeHolder+' #tag_add_' + code).unbind("click");
			$('#'+placeHolder+' #tag_add_' + code).on("click", function() {
				var key = $(this).attr("key");
				dataform_addNewTag(placeHolder, key, $('#'+placeHolder+' #newTag_'+key), null, $('#'+placeHolder+' #tagList_'+key));
			});
			$('#' + placeHolder + ' #tag_removeAll_' + code).unbind("click");
			$('#'+placeHolder+' #tag_removeAll_' + code).on("click", function() {
				var key = $(this).attr("key");
				dataform_removeAllTags(placeHolder, 'tagList_'+key);
			});
		}); 
	};
	var counter=1;
	function dataform_addNewTag(placeHolder, key, inputBox, tagValue, listBox) {
		if (inputBox)
			tagValue = inputBox.val();
		if (tagValue.trim()!=='') {
			var newLi = document.createElement("li");
			newLi.id = 'tag_'+key+'_' + counter;
			newLi.className = "tag-line";
			newLi.innerHTML = '<span class="badge">'+tagValue.trim()+' <span id="tag_closer_'+counter+'" class="badge-closer">X</span></span>';
			listBox.append(newLi);
			$('#'+placeHolder+' #tag_closer_'+counter).on("click", function() {dataform_removeTag(newLi.id);});
			counter++;
			if (inputBox)
				inputBox.val('');
		}
	};
	function dataform_removeTag(id) {
		$('#'+id).remove();
	};
	function dataform_removeAllTags(placeHolder, tagList) {
		$('#'+placeHolder+' #'+tagList+' .tag-line').each(function(index, elem){$(elem).remove();});
	}
	var fillForm = function(placeHolder, options, data) {
		if (data!==null) {
			options.attributes.forEach(function(key) {
				if(options.editableModifier[key]) {
					if (options.dataTypes[key] === 'badge') {
						$('#'+placeHolder+' #newTag_'+key).val('');
						dataform_removeAllTags(placeHolder, 'tagList_'+key);
						data[key].forEach(function(tagValue) {
							dataform_addNewTag(placeHolder, key, null, tagValue,  $('#'+placeHolder+' #tagList_'+key));
						});
					}
					else {
						$('#'+placeHolder+' #field_'+key).val(data[key]);
					}
				}
				else {
					$('#'+placeHolder+' #field_'+key).html(data[key]);
				}
			});
		}
		else {
			options.attributes.forEach(function(key) {
				if(options.editableModifier[key]) {
					if (options.dataTypes[key] === 'badge') {
						$('#'+placeHolder+' #newTag_'+key).val('');
						dataform_removeAllTags(placeHolder, 'tagList_'+key);
					}
					else {
						$('#'+placeHolder+' #field_'+key).val('');
					}
				}
				else {
					$('#'+placeHolder+' #field_'+key).html('');
				}
			});
		}
	};
	var updateFormData = function(objectData, options, placeHolder) {
		if (objectData === null) {
			objectData = {};
			options.attributes
			.forEach(function(key) {
				if (options.editableModifier[key]) {
					if (options.dataTypes[key] === 'badge') {
						var tags = [];
						$('#'+placeHolder+' #tagList_' + key + ' .tag-line>.badge')
						.each(
								function(tagIndex, tagSpan) {
									var tagHTML = $(tagSpan)
									.html();
									var tagValue = tagHTML
									.substr(
											0,
											tagHTML
											.indexOf(('<')))
											.trim();
									if (tagValue !== '') {
										tags.push(tagValue);
									}
								});
						objectData[key] = tags;
					} else {
						if (options.dataTypes[key] === 'integer') {
							objectData[key] = parseInt($(
									'#'+placeHolder+' #field_'+key).val());
						} else if (options.dataTypes[key] === 'double') {
							objectData[key] = parseFloat($(
									'#'+placeHolder+' #field_'+key).val());
						} else if (options.dataTypes[key] === 'float') {
							objectData[key] = parseFloat($(
									'#'+placeHolder+' #field_'+key).val());
						} else {
							objectData[key] = $(
									'#'+placeHolder+' #field_'+key).val();
						}
					}
				} else {
					if (options.dataTypes[key] === 'integer') {
						objectData[key] = 0;
					} else if (options.dataTypes[key] === 'double') {
						objectData[key] = 0.0;
					} else if (options.dataTypes[key] === 'float') {
						objectData[key] = 0.0;
					} else {
						objectData[key] = $('#'+placeHolder+' #field_'+key)
						.html();
					}
				}
			});
		} else {
			options.attributes
			.forEach(function(key) {
				if (options.editableModifier[key]) {
					if (options.dataTypes[key] === 'badge') {
						var tags = [];
						$('#'+placeHolder+' #tagList_' + key + ' .tag-line>.badge')
						.each(
								function(tagIndex, tagSpan) {
									var tagHTML = $(tagSpan)
									.html();
									var tagValue = tagHTML
									.substr(
											0,
											tagHTML
											.indexOf(('<')))
											.trim();
									if (tagValue !== '') {
										tags.push(tagValue);
									}
								});
						objectData[key] = tags;
					} else {
						if (options.dataTypes[key] === 'integer') {
							objectData[key] = parseInt($(
									'#'+placeHolder+' #field_'+key).val());
						} else if (options.dataTypes[key] === 'double') {
							objectData[key] = parseFloat($(
									'#'+placeHolder+' #field_'+key).val());
						} else if (options.dataTypes[key] === 'float') {
							objectData[key] = parseFloat($(
									'#'+placeHolder+' #field_'+key).val());
						} else {
							objectData[key] = $(
									'#'+placeHolder+' #field_'+key).val();
						}
					}
				} else {
					if (options.dataTypes[key] === 'integer') {
						objectData[key] = parseInt($(
								'#'+placeHolder+' #field_'+key).html());
					} else if (options.dataTypes[key] === 'double') {
						objectData[key] = parseFloat($(
								'#'+placeHolder+' #field_'+key).html());
					} else if (options.dataTypes[key] === 'float') {
						objectData[key] = parseFloat($(
								'#'+placeHolder+' #field_'+key).html());
					} else {
						objectData[key] = $('#'+placeHolder+' #field_'+key)
						.html();
					}
				}
			});
		}
		return objectData;
	};
	return function(placeHolderId, options){
		return {
			options: options,
			placeHolder: placeHolderId,
			styles: styleOptions,
			title: null,
			redraw: function() {
				writeForm(this.placeHolder, this.title, this.options, this.styles);
			},
			fill: function(data) {
				fillForm(this.placeHolder, this.options, data);
			},
			update: function(oututObject) {
				return updateFormData(oututObject, this.options, this.placeHolder);
			}
		}
	};
});