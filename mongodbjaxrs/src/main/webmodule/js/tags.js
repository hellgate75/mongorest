/**
 * 
 */
var Tags = function(placeHolderId, options){
	var styleOptions = {};
	var writeTags = function(placeHolder, title, options, styleOptions) {
		var html = '';
		html+='<table border="0" cellpadding="0" cellspacing="0" width="100%"><tr><td width="100%"><input id=\"newTag_'+options.key+'\" type="text" class="input-data-button"></input>';
		html+='<button id="btnAddTag_'+key+'" type="button" class="btn badge-button">Add</button>';
		html+='<button id="btnClearTags_'+key+'" type="button" class="btn badge-button">Clear</button>';
		html+='</td></tr><tr><td width="100%"><ul id="tagList_'+options.key+'" class="list-group"></ul></td></tr></table>';
		$('#'+placeHolder).html(html);
		$('#'+placeHolder+ ' #btnAddTag_'+key).on('click', function() {
			tags_addNewTag($(placeHolder, '#'+placeHolder+ ' #newTag_'+options.key), null, $('#'+placeHolder+ ' #tagList_'+options.key));
		});
		$('#'+placeHolder+ ' #btnClearTags_'+key).on('click', function() {
			tags_removeAllTags(placeHolder, 'tagList_'+options.key);
		});
	};
	function tags_addNewTag(placeHolder, inputBox, tagValue, listBox) {
		if (inputBox)
			tagValue = inputBox.val();
		if (tagValue.trim()!=='') {
			var newLi = document.createElement("li");
			newLi.id = 'tag' + counter;
			newLi.className = "tag-line";
			newLi.innerHTML = '<span class="badge">'+tagValue.trim()+' <span class="badge-closer">X</span></span>';
			listBox.append(newLi);
			$(newLi).find('.badge-closer').each(function(index, tagbtn) {
				$(tagbtn).attr("onclick", function() {
					tags_removeTag(placeHolder, newLi.id, listBox.id);
				});
			});

			counter++;
			if (inputBox)
				inputBox.val('');
		}
	};
	function tags_removeTag(placeHolder, id, tagList) {
		$('#'+placeHolder+ ' '+'#'+tagList+' #'+id).remove();
	};
	function tags_removeAllTags(placeHolder, tagList) {
		$('#'+placeHolder+ ' '+'#'+tagList+' .tag-line').each(function(index, elem){$(elem).remove();});
	}
	var fillTags = function(placeHolder, options, data) {
			$('#'+placeHolder+' #newTag_'+key).val('');
			tags_removeAllTags(placeHolder, 'tagList_'+options.key);
			data.forEach(function(tagValue) {tag_addNewTag(placeHolder, null, tagValue,  $('#tagList_'+options.key));});
	};
	return {
		options: options,
		placeHolder: placeHolderId,
		styles: styleOptions,
		title: null,
		redraw: function() {
			writeTags(this.placeHolder, this.title, this.options, this.styles);
		},
		fill: function(data) {
			fillTags(this.placeHolder, this.options, data);
		},
	}
};
var Dataform = function(placeHolderId, options){
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
					html+='<button type="button" class="btn badge-button" onclick="javascript:addNewTag($(\'#newTag_'+key+'\'), null, $(\'#tagList_'+key+'\'));">Add</button>';
					html+='<button type="button" class="btn badge-button" onclick="javascript:removeAllTags(\'tagList_'+key+'\');">Clear</button>';
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
	};
	var fillForm = function(placeHolder, options, data) {
		if (data!==null) {
			options.attributes.forEach(function(key) {
				if(options.editableModifier[key]) {
					if (options.dataTypes[key] === 'badge') {
						$('#'+placeHolder+' #newTag_'+key).val('');
						options.removeAllTagsHandler('tagList_'+key);
						data[key].forEach(function(tagValue) {options.addNewTagHandler(null, tagValue,  $('#tagList_'+key));});
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
						options.removeAllTagsHandler('tagList_'+key);
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