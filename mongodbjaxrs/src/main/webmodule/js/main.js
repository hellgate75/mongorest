/**
 * Site scripts
 */
/* Load in asynchronous mode from the books JAXR-RS Books rest service */
define(["require", "exports", "datatable", "dataform", "eventbus"], function(require, exports, Datatable, Dataform, eventbus){
	var mongoObjectList = [];
	var mongoMetaTypes = [];
	var dataTable = null;
	var dataForm = null;
	var currentMeta = null;
	function loadMetaTypes() {
		mongoMetaTypes = [];
		currentMeta = {  typeName: "Book", 
						 typeTitle: "Books", 
						 typeId: "id", 
						 mongoType:"bookXML", 
						 collection:"books",
						 attributes: ["id", "title", "description", "user", "url", "likes", "tags"],
						 dataTypes: {"id" : "identifier", "title" : "text", "description" : "text", "user" : "text", "url" : "text", "likes" : "integer", "tags" : "badge"},
						 editableModifier: {"id" : false, "title" : true, "description" : true, "user" : true, "url" : true, "likes" : false, "tags" : true},
						 titles: {"id" : "Id", "title" : "Title", "description" : "Description", "user" : "Author", "url" : "URL", "likes" : "Likes", "tags" : "Tags"},
						};
		mongoMetaTypes.push(currentMeta);
		var authorsData = {  typeName: "Author", 
				 typeTitle: "Authors", 
				 typeId: "id", 
				 mongoType:"authorXML", 
				 collection:"authors",
				 attributes: ["id", "name", "surname", "age", "editor", "blog", "social", "rate", "tags"],
				 dataTypes: {"id" : "identifier", "name" : "text", "surname" : "text", "editor" : "text", "blog" : "text", "social" : "text", "age" : "integer", "rate" : "double", "tags" : "badge"},
				 editableModifier: {"id" : false, "name" : true, "surname" : true, "editor" : true, "blog" : true, "social" : true, "age" : true, "rate" : true, "tags" : true},
				 titles: {"id" : "Id", "name" : "Name", "surname" : "Surname", "editor" : "Editor", "blog" : "Blog", "social" : "Social", "age" : "Age", "rate" : "Rate", "tags" : "Tags"},
				};
		mongoMetaTypes.push(authorsData);
	};
	var selectedDataTypeIndex = 0;
	function setupMongoTypeDropdown() {
		var html = "";
		var codes = [];
		mongoMetaTypes.forEach(function(config, index) {
			html += '<li><label id="dt_'+config.typeName+'" class="mongo-dropdown-item'+(selectedDataTypeIndex===index ? ' selected':'')+'">'+config.typeName+'</label></li>';
			codes.push('dt_'+config.typeName);
		});
		$('.change-mongoobject').html(mongoMetaTypes[selectedDataTypeIndex].typeName+'<span class="caret"></span>');
		$('.change-mongoobject-menu').html(html);
		codes.forEach(function(code, index){
			$('#'+code).on('click', function() {
				changeMongoTypeDropdown(index);
			});
		});
	};
	function changeMongoTypeDropdown(index) {
		selectedDataTypeIndex = index;
		currentMeta = mongoMetaTypes[selectedDataTypeIndex];
		generateForm();
		loadMongoData();
		setupMongoTypeDropdown();
	};
	function loadMongoData() {
		$.ajax("/mongodbjaxrs/resources/MongoDbRest/" + currentMeta.collection, {
			method: "GET",
			dataType: "json",
			accepts: "json",
			cache: false,
			success : writeMongoObjectList,		
			error : function(){
				var html = "Errore di caricamento dei libri";
				$("#table").html(html);
			},		
		});
	};
	function deleteMongoData(bookid) {
		$.ajax("/mongodbjaxrs/resources/MongoDbRest/remove/"+currentMeta.collection+"/"+bookid, {
			method: "GET",
			dataType: "json",
			accepts: "json",
			cache: false,
			success : function() {
				loadMongoData();
				//closeModal('.delete-mongoobject-modal');
			},		
			error : function(){
			},		
		});
	};
	function saveMongoData(mongoObject, index) {
		$.ajax("/mongodbjaxrs/resources/MongoDbRest/"+currentMeta.collection+"/" + (!mongoObject.id ? "new" : mongoObject.id), {
			method: "POST",
			dataType: "json",
			accepts: "json",
			data: encodeURIComponent(JSON.stringify(mongoObject)),
			success : function(data) {
				updateModelAfterSave(data, mongoObject, index);
			},		
			error : function(){
				$('#bookError').css('display', 'block');
				$('#bookFormError').css('display', 'none');
				$('#bookSuccess').css('display', 'none');
			},		
		});
	};
	function setSaveButtonDisabled(status) {
		$('#saveButton')[0].disabled = status;
	};
	function setDeleteButtonDisabled(status) {
		$('#deleteButton')[0].disabled = status;
	};
	function updateModelAfterSave(response, mongoObject, index) {
		try {
			if (parseInt(response.code) === 0) {
				if (mongoObject[currentMeta.typeId] === '' || index === -1) {
					loadMongoData();
					setSaveButtonDisabled(true);
				} else {
					mongoObjectList[index] = mongoObject;
					writeMongoObjectList(mongoObjectList);
				}
				$('#bookError').css('display', 'none');
				$('#bookFormError').css('display', 'none');
				$('#bookSuccess').css('display', 'block');
			} else {
				$('#bookError').css('display', 'block');
				$('#bookFormError').css('display', 'none');
				$('#bookSuccess').css('display', 'none');
			}
		} catch (e) {
			$('#bookError').css('display', 'block');
			$('#bookFormError').css('display', 'none');
			$('#bookSuccess').css('display', 'none');
		}
	};
	function getDataTableOptions() {
		return {
			attributes: currentMeta.attributes,
			titles: currentMeta.titles,
			dataTypes: currentMeta.dataTypes,
			typeId: currentMeta.typeId,
			editableModifier: currentMeta.editableModifier,
			newActionHandler: updateNewMongoObjectModal,
			updateActionHandler: updateEditMongoObjectModal,
			deleteActionHandler: updateDeleteMongoObjectModal,
		};
	};

	function writeMongoObjectList(data) {
		mongoObjectList = (!data[currentMeta.mongoType] ? data : data[currentMeta.mongoType]);
		var options = getDataTableOptions();
		if (!dataTable) {
			dataTable = new Datatable("table", options);
		}
		else {
			dataTable.options = options;
		}
		dataTable.title = currentMeta.typeTitle;
		dataTable.setData(mongoObjectList);
		dataTable.redraw();
	};
	function closeModal(modalJQuerySelector) {
		try {
			$(modalJQuerySelector).removeClass('in');
		} catch (e) {
		}
	};
	function updateNewMongoObjectModal() {
		updateEditMongoObjectModal(null);
	};
	function updateEditMongoObjectModal(id) {
		counter=1;
		setSaveButtonDisabled(false);
		var selectedMongoObject = null;
		if (id!==null) {
			mongoObjectList.forEach(function(mongoObject) {if (mongoObject[currentMeta.typeId]===id) {selectedMongoObject=mongoObject;}});
		}
		$('#bookError').css('display', 'none');
		$('#bookFormError').css('display', 'none');
		$('#bookSuccess').css('display', 'none');
		if (!dataForm) {
			var options = getDataTableOptions();
			dataForm = new Dataform("modal-edit-body", options);
			dataForm.title = currentMeta.typeName + " Details";
			//dataForm.redraw();
		}
		dataForm.fill(selectedMongoObject);
	};
	function updateDeleteMongoObjectModal(id) {
		var selectedMongoObject = null;
		if (id!==null) {
			mongoObjectList.forEach(function(mongoObject) {if (mongoObject[currentMeta.typeId]===id) {selectedMongoObject=mongoObject;}});
		}
		if (selectedMongoObject===null) {
			$("#mongoObjectIdentifier").val("");
			$("#mongoObjectDeleteCaption").html("Mongo Object Reference not found");
			setDeleteButtonDisabled(true);
		}
		else {
			$("#mongoObjectIdentifier").val(selectedMongoObject.id);
			$("#mongoObjectDeleteCaption").html("["+selectedMongoObject.id+"] "+selectedMongoObject.title);
			setDeleteButtonDisabled(false);
		}
	};
	function getCurrentMongoObjectId() {
		
		if (currentMeta.editableModifier[currentMeta.typeId]) {
			return $('#field_' + currentMeta.typeId).val();
		}
		else {
			return $('#field_' + currentMeta.typeId).html();
		}
		
	};
	function validateMongoObjectForm() {
		var id = getCurrentMongoObjectId();
		var selectedMongoObject = null;
		var bookIndex = -1;
		if (id!=='') {
			mongoObjectList.forEach(function(mongoObject, index) {if (mongoObject[currentMeta.typeId]===id) {selectedMongoObject=mongoObject;bookIndex=index;}});
		}

		$('#bookError').css('display', 'none');
		$('#bookFormError').css('display', 'none');
		$('#bookSuccess').css('display', 'none');
		try {
			if (!dataForm) {
				var options = getDataTableOptions();
				dataForm = new Dataform("modal-edit-body", options);
				dataForm.title = currentMeta.typeName + " Details";
				dataForm.redraw();
			}
			selectedMongoObject = dataForm.update(selectedMongoObject);
			saveMongoData(selectedMongoObject, bookIndex);
			return;
		} catch (e) {
			console.log(e);
			$('#bookError').css('display', 'none');
			$('#bookFormError').css('display', 'block');
			$('#bookSuccess').css('display', 'none');
		}
	};
	function generateForm() {
		var options = getDataTableOptions();
		if (!dataForm) {
			dataForm = new Dataform("modal-edit-body", options);
		}
		else {
			dataForm.options = options;
		}
		dataForm.title = currentMeta.typeName + " Details";
		dataForm.redraw();
	};
	function initActions() {
		$("#saveButton").on('click', function(){validateMongoObjectForm();});
		$("#deleteButton").on('click', function(){deleteMongoData($('#mongoObjectIdentifier').val());});
	};
	return function(){
		return {
			loadMetaTypes: loadMetaTypes,
			generateForm: generateForm,
			loadMongoData: loadMongoData,
			setupMongoTypeDropdown: setupMongoTypeDropdown,
			init: initActions
		};
	};
});

/* Load in the books at startup ... */
