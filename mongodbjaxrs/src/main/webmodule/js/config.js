requirejs.config({
    //Remember: only use shim config for non-AMD scripts,
    //scripts that do not already call define(). The shim
    //config will not work correctly if used on AMD scripts,
    //in particular, the exports and init config will not
    //be triggered, and the deps config will be confusing
    //for those cases.
    shim: {
    	 baseUrl: "./",
        'datatable': {
             exports: 'datatable'
        },
        'dataform': {
            //These script dependencies should be loaded before loading
            //backbone.js
            exports: 'dataform'
        },
        'eventbus': {
            deps: ['jquery-1.12.0.min'],
            //These script dependencies should be loaded before loading
            //backbone.js
            exports: 'eventbus'
        },
        'main': {
            deps: ['datatable','dataform', "eventbus"],
            exports: 'main'
         }
    }
});