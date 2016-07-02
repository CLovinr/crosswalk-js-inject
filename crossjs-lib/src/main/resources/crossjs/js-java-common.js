/**
*通用代码.
* 使用:
* 1.var handleObj = global.__handle__(namespace,namespaceStr);
* 2.HOST_APP_FUN handleObj.commonFunction();
* 3.handleObj.initOk();
*/
(function(global,bridge,jsCallbackReturnBridge){
    var log=<LOG>;
    if(log)
        console.log("<TOP_NAME> initialization begin");
    var _index=0;
    /*是否递归搜索。若为否，则{f:function(){},obj:{f2:function(){}}}中只会转换函数f而f2不会被转换。*/
    var searchMore=<SEARCH_MORE>;
    function uuid() {
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 36; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4";
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
        s[8] = s[13] = s[18] = s[23] = "-";
        var uuid = s.join("");
        return uuid;
    }
    function _ID(){
        return uuid()+"-"+_index++;
    }
    /*<COMMON_NAMESPACES>;*/
    var callJava = null;
    /**
     * 存储函数，并返回一个对应格式的字符串id。
     */
    function _function2Id(fun,callJavaObj){
        var id =_ID();
        callJavaObj.queue[id] = fun;
        return "<JSON_FUNCTION_STARTS>"+id;
    }
	/**
	 * 转换数组
	 */
    function _parseArrayFun(arr,callJavaObj){
        for(var i=0;i<arr.length;i++){
        	arr[i]=_parseObjFun(arr[i],callJavaObj);
        }
        return arr;
    }
    /**
     * 【js->java】把从js端传到java端的内容进行转换。
     * 在传递对象到java层之前，转换里面的函数
     */
    function _parseObjFun(obj,callJavaObj){

		if(typeof obj=="function"){
			return _function2Id(obj,callJavaObj);
		}
		else if(obj instanceof Array){
			return _parseArrayFun(obj,callJavaObj);
		}
		else{
			var robj={};
	        for(var name in obj){
	            var arg=obj[name];
	            var type = typeof arg;
	            if(type==="function"){
	                robj[name]=_function2Id(arg,callJavaObj);
	            }else if(searchMore&&type==="object"&&arg!==null){
	                if(arg instanceof Array){
	                    robj[name]=_parseArrayFun(arg,callJavaObj);
	                }else{
	                	/*递归*/
	                    robj[name]=_parseObjFun(arg,callJavaObj);
	                }

	            }else{
					robj[name]=arg;
				}
	        }
	        return robj;
        }

    };

    var javaCallbackTag = <JAVA_CALLBACK>;
	function _addJavaCallback(callbackId,callJavaObj){
			var callFun = function(){
				var rs=	callJava.apply(callJavaObj.namespace,[callJavaObj,callJavaObj.namespaceStr,true,javaCallbackTag,callbackId,"callback"].concat(Array.prototype.slice.call(arguments, 0)));
				return rs;
			};
			callFun.destroy=function(){
				callJava.apply(callJavaObj.namespace,[callJavaObj,callJavaObj.namespaceStr,true,javaCallbackTag,callbackId,"destroy"]);
			};
			callFun.setPermanent=function(isPermanent){
				callJava.apply(callJavaObj.namespace,[callJavaObj,callJavaObj.namespaceStr,true,javaCallbackTag,callbackId,"setPermanent",isPermanent?true:false]);
			};
			return callFun;
	 };



	/**
	 * 【java->js】把从java端传过来的内容进行转换。
	 * 转换json对象中的指定格式的字符串（与某个java对象Java2JsCallback对应的）为js函数.
	 * 从java端传递动态函数过来，就是通过该处实现的。
	 */
	function parseString2Fun(obj,callJavaObj){
	    var type = typeof obj;
	    var returnObj=obj;
		if(type==="string"&&obj.indexOf(javaCallbackTag)==0){/*java端的回调,从java端传递函数过来。*/
			var index = javaCallbackTag.length;
			var callbackId = obj.substr(index);
			returnObj = _addJavaCallback(callbackId,callJavaObj);
	     }else if(searchMore&&type==="object"&&obj!=null){
	        if(obj instanceof Array){
	            for(var i=0;i<obj.length;i++){
	                returnObj[i]=parseString2Fun(obj[i],callJavaObj);
	            }
	        }else{
	            for(var x in obj){
            	     returnObj[x]=parseString2Fun(obj[x],callJavaObj);
            	}
	        }

	     }
	     return returnObj;
	};
	/**
	 * 该函数的作用是：把调用的参数等转换成字符串传递的java层，并得到返回的结果。
	 * ********************用于js端调用java接口************************
	 */
     callJava =  function (){
            var args = Array.prototype.slice.call(arguments, 0);
            var callJavaObj = args.shift();
            var namespaceStr=args.shift();
            var isJavaCallback=args.shift();

            if (args.length < 1) {
                throw namespaceStr+" call error, message:miss method name";
            }
            var aTypes = [];
            for (var i = 1;i < args.length;i++) {
                var arg = args[i];
                var type = typeof arg;
                aTypes[aTypes.length] = type;
                if (type === "function"||(type==="object"&&arg!==null)) {
                    args[i] = _parseObjFun(arg,callJavaObj);
                }
            }
            var res = JSON.parse(bridge(JSON.stringify({
                isJavaCallback:isJavaCallback,
                method: args.shift(),
                types: aTypes,
                args: args,
                namespace:namespaceStr
            })));
            if (res.code != 200) {
                throw namespaceStr+" call error, code:" + res.code + ", message:" + res.result;
            }
            return parseString2Fun(res.result,callJavaObj);
    };
	/**
	 * 每一个注入的java端的接口类，都对应一个对象（通过该函数返回的）
	 */
  var __handle__ = function(namespace,namespaceStr){

	    var returnObj =	 {
	    	namespace:namespace,
	    	namespaceStr:namespaceStr,
	        queue: {},
	        destroy:function(id){/*用于清除注册的函数,对应于java端的destroy函数*/
	            if(log){
	                console.log("delete function(id="+id+")");
	            }
	            delete returnObj.queue[id];
	        },
	        /**
	         * ********************用于java端调用js************************
	         */
	        callback: function () {/*对应于java端的apply函数*/
	            var args = Array.prototype.slice.call(arguments, 0);
	            var id = args.shift();
	            var isPermanent = args.shift();
				args=args.shift();//params:Array
	            for(var i=0;i<args.length;i++){
	                args[i] = parseString2Fun(args[i],returnObj);
	            }
	            var callResult = null;
	            var type;
	            try{
	                callResult = returnObj.queue[id].apply(returnObj, args);
	            }catch(e){}
	            if (!isPermanent) {
	                returnObj.destroy(id);
	            }
	            if(callResult!==null&&callResult!=undefined&&(typeof callResult !=="string")){
	            	callResult=JSON.stringify(callResult);
	            }
				jsCallbackReturnBridge(JSON.stringify({"id":id,"return":callResult}));
	        },
	        commonFunction:function(){/*返回一个函数*/
			    var commonFun= function () {
			        return callJava.apply(namespace,[returnObj,namespaceStr,false].concat(Array.prototype.slice.call(arguments, 0)));
			    };

			    return commonFun;
	        },initOk:function(){/*更改函数的行为*/

	        	/*有时候，我们希望在该方法执行前插入一些其他的行为用来检查当前状态或是监测
			    代码行为，这就要用到拦截（Interception）或者叫注入（Injection）技术了*/
			    /**
			     * Object.getOwnPropertyName 返回一个数组，内容是指定对象的所有属性
			     *
			     * 其后遍历这个数组，分别做以下处理：
			     * 1. 备份原始属性；
			     * 2. 检查属性是否为 function（即方法）；
			     * 3. 若是重新定义该方法，做你需要做的事情，之后 apply 原来的方法体。
			     */
			    Object.getOwnPropertyNames(namespace).forEach(function (property) {
			        var original = namespace[property];
			        if (typeof original === "function"
			        			//&&property!=="callback"&&property!=="destroy"
			        			) {
			            namespace[property] = function () {
			                return original.apply(namespace,  [property].concat(Array.prototype.slice.call(arguments, 0)));
			            };

			        }

			    });
			    namespace.__callback__=returnObj.callback;
			    namespace.__destroy__=returnObj.destroy;
	        }
	    };
	    
	    return returnObj;
    
    }
    global.__handle__ = __handle__;
    if(log)
        console.log("<TOP_NAME> initialization end");
})(<GLOBAL>,<BRIDGE>,<RETURN_BRIDGE>);
