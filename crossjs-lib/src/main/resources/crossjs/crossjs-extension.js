
extension.setMessageListener(function(message) {
	try{
		if(message.indexOf("javascript:")==0){
			eval(message.substr("javascript:".length));
		}
	}catch(e){
	}
});

function buildMessage__(type,value){
	var obj = {"type":type,"value":value};
	return JSON.stringify(obj);
}

var __bridge__ = function(jsonStr) {
	var result = extension.internal.sendSyncMessage(buildMessage__("bridge",jsonStr));
	return result;
};

var __async_bridge__ = function(jsonStr) {
	return new Promise(function(resolve, reject) {
		try {
			extension.postMessage(jsonStr);
		} catch (e) {
			reject(e);
		}
	});
};

/*对于被写死的接口中被允许的*/
var enables__ = JSON.parse(extension.internal.sendSyncMessage(buildMessage__("enables",location.href)));

var enableInjects__ = enables__["injects"];
var dynamics__ = enables__["dynamics"];
var static__=true;
enables__=null;

var __injectHandle__=function(injectName){
	return enableInjects__[injectName];
};
