/**
 * 用于注册接口类。
 */
(function(global){
    var log=<LOG>;
    if(log)
        console.log("<HOST_APP> init begin");
    <HOST_APP_NAMESPACES>;

	//global.<HOST_APP> = <HOST_APP>;
	var handleObj=global.__handle__(global.<HOST_APP>,<NAMESPACE>);
    <HOST_APP_FUN> handleObj.commonFunction();
    handleObj.initOk();
    if(log)
        console.log("<HOST_APP> init end");
})(<GLOBAL>);
