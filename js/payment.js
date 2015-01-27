;
(function(plus){
    var _PAYMENT = 'Payment',
		B = window.plus.bridge;
    function Channel (){
        this.id = '';
        this.description = '',
        this.serviceReady = true,
        this.installService = function(){
            B.exec( _PAYMENT, "installService", [this.id] );
        }
    }
    var payment = {
        Channel : Channel,
        getChannels : function( successCallback, errorCallback ){
            var success = typeof successCallback !== 'function' ? null : function(channels) {
                var ret = [],
					len = channels.length;
                for(var i = 0; i < len; i++){
                    var channel = new payment.Channel();
                    channel.id = channels[i].id;
                    channel.description = channels[i].description;
                    channel.serviceReady = channels[i].serviceReady;
                    ret[i] = channel;
                }
                successCallback(ret);
            };
            var fail = typeof errorCallback !== 'function' ? null : function( error ) {
                var err = {};
                
                errorCallback(error);
            };
            var callbackId =  B.callbackId( success, fail );
            
            B.exec( _PAYMENT, "getChannels", [callbackId] );
        },
        request : function(channel, statement, successCallback, errorCallback){
            var success = typeof successCallback !== 'function' ? null : function(strings) {
                successCallback(strings);
            };
            var fail = typeof errorCallback !== 'function' ? null : function( error ) {
                errorCallback(error);
            };

            if ( !(channel instanceof Channel)  ) {
                window.setTimeout( function(){
                    fail({code:62000});
                },0);
                return;
            };

            var callbackId =  B.callbackId( success, fail );
            B.exec( _PAYMENT, "request", [channel.id,statement,callbackId] );
        }
    };
    plus.payment = payment;
})(window.plus);