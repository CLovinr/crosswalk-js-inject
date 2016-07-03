/**
 * 用于注册接口类。
 */
(function(global,injectHandle){
    var log=<LOG>;

    if(static__&&!injectHandle("<INJECT_NAME>")){
        if(log)
            console.log("interface of '<INJECT_NAME>' disabled");
        return;
    }

    if(log)
        console.log("<HOST_APP> init begin");
    <HOST_APP_NAMESPACES>;

	//global.< HOST_APP > = < HOST_APP >;
	var handleObj=global.__handle__(global.<HOST_APP>,<NAMESPACE>);
    <HOST_APP_FUN> handleObj.commonFunction();
    handleObj.initOk();
    if(log)
        console.log("<HOST_APP> init end");
})(<GLOBAL>,<INJECT_HANDLE>);
