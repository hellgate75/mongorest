/**
 * Site scripts
 */
/* Load in asynchronous mode from the books JAXR-RS Books rest service */
var main;
require(["main"], function(Main) {
	main = new Main();
	main.init();
	main.loadMetaTypes();
	main.generateForm();
	main.loadMongoData();
	main.setupMongoTypeDropdown();
	
});
