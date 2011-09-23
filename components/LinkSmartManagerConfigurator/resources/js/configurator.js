/*  Configurator JavaScript framework, version 1.0
 *  Author: Pablo Antolin
 *
 *
 *--------------------------------------------------------------------------*/

var servletAddress = "/LinkSmartConfigurator/GetConfiguration";

function getConfigurations(){
		var responseJSON;
		var jsonResponse;
		new Ajax.Request(servletAddress, {
			method: 'get',
			parameters: {
				method: 'listConfigurations'
			},
			onComplete: function(transport){
				responseJSON = transport.responseText;
				//mockup
				//responseJSON = '[{"configurations":["eu.linksmart.eventmanager","eu.linksmart.network.NetworkManager"]}]';
				var jsonResponse = responseJSON.evalJSON();
				updateLeft(jsonResponse);
			}
		});
		
}

function updateLeft(configurations){
	//Fill in configuration menu 
	var leftHTML = "<h2>Available Configurations</h2>";
	leftHTML += "<ul>";
	for (var i = 0; i < configurations[0].configurations.length; i++) {
		var jsText = "javascript:getConfigurationData('" + configurations[0].configurations[i] + "')";
		leftHTML += "<li><a href='#' onclick='javascript:getConfigurationData(\"" + configurations[0].configurations[i] + "\");'>" + configurations[0].configurations[i] + "</a></li>";
	}
	leftHTML += "</ul>";
	document.getElementById("left").innerHTML = leftHTML;
}

function getConfigurationData(configuration){
	var responseJSON;
		var jsonResponse;
		new Ajax.Request(servletAddress, {
			method: 'get',
			parameters: {
				method: 'getConfiguration',
				configName: configuration
			},
			onComplete: function(transport){
				responseJSON = transport.responseText;
				//mockup
				//responseJSON = '[{"name":"eu.linksmart.eventmanager","parameters":[{"name":"CoreSecurityClient","value":"true"},{"name":"EventManagerDescription","value":"EventManager_AARHUS"},{"name":"NetworkManagerAddress","value":"http://localhost:8082/axis/services/NetworkManagerApplication"},{"name":"service.pid","value":"eu.linksmart.eventmanager"},{"name":"SOAPTunnelingAddress","value":"http://localhost:8082/SOAPTunneling"},{"name":"withNetworkManager","value":"true"}]}]';
				var jsonResponse = responseJSON.evalJSON();
				updateRight(jsonResponse);
			}
		});
}

function updateRight(configuration){
	//Fill in configuration form 
	document.getElementById("selectedconftitle").innerHTML = "<h1>" + configuration[0].name + "</h1>";
	var rightHTML = "<div id=\"optionsContainer\">";
	for (var i = 0; i < configuration[0].parameters.length; i++) {
		if (i%2 != 0){
			rightHTML += '<span class="postinfoodd">';
		}else{
			rightHTML += '<span class="postinfoeven">';
		}
		rightHTML += '<table class="tablecontent"><tr><td class="tabletdleft">';
		rightHTML += '<span id="' + configuration[0].parameters[i].name + '_name">' + configuration[0].parameters[i].name + ':</span></td>';
		rightHTML += '<td class="tabletdright"><span id="' + configuration[0].parameters[i].name + '_value"><input type="text" class="text" name="' + configuration[0].parameters[i].name + '" value="' + configuration[0].parameters[i].value + '" /></span></td></tr></table></span>';
	}   
	rightHTML += "</div><center><input class='btn' onclick='submitConfiguration(\"" + configuration[0].name + "\")' type='button' value='Update Configuration' /></center>"; 
	document.getElementById("selectedconfcontent").innerHTML = rightHTML;
	
	if (document.getElementById("optionsContainer").scrollHeight > 425) {
		text = document.getElementById("selectedconfcontent").innerHTML;
		text += "<div style=\"position: absolute; right:121px; top:272px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('optionsContainer'); scrollBarUpdate ('optionsContainer', 'optionsScrollbarHandler', 'optionsScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"LinkSmartStatus/images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<IMG id=\"optionsScrollDown\" onmousedown=\"step=10;scrollDivDown('optionsContainer'); scrollBarUpdate ('optionsContainer', 'optionsScrollbarHandler', 'optionsScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"LinkSmartStatus/images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<div id=\"optionsScrollbarTrack\"><div id=\"optionsScrollbarLine\"></div><div id=\"optionsScrollbarHandler\"><img src=\"LinkSmartStatus/images/imageflow/slider.png\"/></div></div></div>"
		document.getElementById("selectedconfcontent").innerHTML = text;
			    
		handled = 'optionsContainer';
		handlerer = 'optionsScrollbarHandler';
		tracker = 'optionsScrollbarTrack';
				
		var slider = new Control.Slider('optionsScrollbarHandler', 'optionsScrollbarTrack', {
			axis: 'vertical',
			onSlide: function(v) { scrollVertical(v, $('optionsContainer'), slider);  },
			onChange: function(v) { scrollVertical(v, $('optionsContainer'), slider); }
		});
				
		if (window.addEventListener) {
			document.getElementById("optionsContainer").addEventListener('DOMMouseScroll', wheel, false);
			document.getElementById("optionsContainer").onmousewheel = document.onmousewheel = wheel;
		}
	}
}

function submitConfiguration(configuration){
	
	var jsonText = '[{"name":"' + configuration + '","parameters": [';
	var arrayInput = document.getElementsByTagName("input");
	for (var i = 0; i < arrayInput.length -1; i++) {
		jsonText += '{ "name": "' + arrayInput[i].name + '","value": "' + arrayInput[i].value + '"},'
	}
	jsonText = jsonText.substr(0,jsonText.length -1);
	jsonText += ']}]'; 

	new Ajax.Request(servletAddress, {
		method: 'post',
		parameters: {
			method: 'postConfiguration',
			configuration: jsonText
		},
		onComplete: function(transport){
		}
	});
}

