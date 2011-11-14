/*  Configurator JavaScript framework, version 1.0
 *  Author: Pablo Antolin
 *  Modified on 07/2011 by Erion Elmasllari, FIT
 *
 *--------------------------------------------------------------------------*/

var servletAddress = "/LinkSmartConfigurator/GetConfiguration";
var currentConfiguration = null;
var currentConfigurationData = null;


function isNumeric(s) {
    return (s - 0) == s && s.length > 0;
}
/*
//augment arrays to support indexOf property if needed (older browsers)
if (!Array.prototype.indexOf) {
//    Array.prototype.indexOf = function (searchElement , fromIndex ) {
    Array.prototype.indexOf = function (searchElement) {
        "use strict";
        if (this === void 0 || this === null) {
            throw new TypeError();
        }
        var t = Object(this);
        var len = t.length >>> 0;
        if (len === 0) {
            return -1;
        }
        var n = 0;
        if (arguments.length > 0) {
            n = Number(arguments[1]);
            if (n !== n) { // shortcut for verifying if it's NaN
                n = 0;
            } else if (n !== 0 && n !== window.Infinity && n !== -window.Infinity) {
                n = (n > 0 || -1) * Math.floor(Math.abs(n));
            }
        }
        if (n >= len) {
            return -1;
        }
        var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
        for (; k < len; k++) {
            if (k in t && t[k] === searchElement) {
                return k;
            }
        }
        return -1;
    }
}
*/

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
	currentConfiguration = configuration;
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
				currentConfigurationData = responseJSON.evalJSON();
				updateRight(currentConfigurationData);
			}
		});
}


function updateRight(configuration){
	//Fill in configuration form 
	document.getElementById("selectedconftitle").innerHTML = "<h1>" + configuration.name + "</h1>";

	var rightHTML = '<div id="optionsContainer">';

	var zebraDark=false;
	for (var paramName in configuration.parameters) {
		var paramValue = configuration.parameters[paramName];
		if (paramName.startsWith('ParamDescription')) {
			continue; //this is only descriptive, should not be usable/configurable.
		} 
		var labelText = configuration.parameters['ParamDescription.'+paramName+'.description'];
		if (!labelText) {
			labelText = paramName;
		}
		rightHTML += '<div class="' + (zebraDark?'postinfoeven':'postinfoodd') + ' paramform" id="' + paramName + '_form_div"><span class="errormsg" id="' + paramName + '_form_errormsg"></span><label for="' + paramName + '_form">' + labelText + '</label>';
		var formElement='';
		
		switch (configuration.parameters['ParamDescription.'+paramName+'.type']) {
			case 'choice':
				if (configuration.parameters['ParamDescription.'+paramName+'.choice0']) {
					formElement = '<select name="' + paramName + '" id="' + paramName + '_form" >';
					var n=0; //start enumerating choices from config file
					while (configuration.parameters['ParamDescription.'+paramName+'.choice'+n]) {
						var choiceData = configuration.parameters['ParamDescription.'+paramName+'.choice'+n].split('|');
						formElement += '<option value="' + choiceData[0] + '" ' + (paramValue==choiceData[0]? 'selected': '')  + '>' + choiceData[1] + '</option>';
						n++;
					}
					formElement += '</select>';
				} else { //has said it's choice, but has not given any choice as paramdescription
					formElement = '<input type="text" class="text" name="' + paramName + '" id="' + paramName + '_form" value="' + paramValue + '" />';
				}
				break;
			case 'boolean': //select instead of checkbox, because checkbox does not get sent if unchecked, and receiver servlet then deletes the param.
				formElement = '<select name="' + paramName + '" id="' + paramName + '_form" >';
				formElement += '<option value="true" ' + (paramValue=='true'? 'selected': '') + '>True</option>';
				formElement += '<option value="false" ' + (paramValue=='false'? 'selected': '') + '>False</option>';
				formElement += '</select>';
				break;			
			case 'integer':
				var rangeMin = (configuration.parameters['ParamDescription.'+paramName+'.min'] ? configuration.parameters['ParamDescription.'+paramName+'.min'] : '');
				var rangeMax = (configuration.parameters['ParamDescription.'+paramName+'.max'] ? configuration.parameters['ParamDescription.'+paramName+'.max'] : '');
				formElement = '';
				if (rangeMin != '' && rangeMax != '') {
					formElement = '<span class="range">Range:' + rangeMin + ' - ' + rangeMax +'</span>'; 
				} else if (rangeMin != '') {
					formElement = '<span class="range">Minimum:' + rangeMin + '</span>'; 
				} else if (rangeMax != '') {
					formElement = '<span class="range">Maximum:' + rangeMax + '</span>'; 
				}
				formElement += ' <input type="text" class="text" name="' + paramName + '" id="' + paramName + '_form" value="' + paramValue + '" />';
				break;
			case 'readonly':
				formElement = '<input type="text" readonly="readonly" class="text" name="' + paramName + '" id="' + paramName + '_form" value="' + paramValue + '" />';
				break;
			case 'text':
			default:
				formElement = (configuration.parameters['ParamDescription.'+paramName+'.ereg'] ? 
									('<span class="range">Must match this regular expression: ^' + configuration.parameters['ParamDescription.'+paramName+'.ereg'] +'</span>') 
									: 
									''
							  );
				formElement += '<input type="text" class="text" name="' + paramName + '" id="' + paramName + '_form" value="' + paramValue + '" />';
		}
		
		rightHTML += formElement + '</div>'; 
		zebraDark = !zebraDark;
	}   
	rightHTML += "</div><center><input class='btn' onclick='submitConfiguration(\"" + configuration.name + "\")' type='button' value='Update Configuration' /></center>"; 
	
	document.getElementById("selectedconfcontent").innerHTML = rightHTML;
}

function submitConfiguration(configuration){
	
	var inputIsOK = validateInputs(currentConfigurationData);
	if (!inputIsOK) {
		alert("Please correct the values in red -- they contain forbidden or meaningless values.");
		return false;
	}
	
	var jsonText = '[{"name":"' + configuration + '","parameters": [';
	var arrayInput = document.getElementsByTagName("input");
	for (var i = 0; i < arrayInput.length -1; i++) { //-1 because we do not want the button to be counted
		jsonText += '{ "name": "' + arrayInput[i].name + '","value": "' + arrayInput[i].value + '"},'
	}

	var arraySelect = document.getElementsByTagName("select");
	for (var i = 0; i < arraySelect.length; i++) { //after modifications for type checking and interface improvement, we include also selects
		jsonText += '{ "name": "' + arraySelect[i].name + '","value": "' + arraySelect[i].value + '"},'
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
			alert("Configuration saved");
		}
	});
}


function validateInputs(configuration) {
	
	var noErrors = true;
	var isThisOneInputValid = true;
	
	for (var paramName in configuration.parameters) {
		var paramValue = configuration.parameters[paramName];
		if (paramName.startsWith('ParamDescription')) {
			continue; //this is only descriptive, should not be usable/configurable.
		}

		//at this point we know we have a parameter
		//get the HTML form element for it
		var paramFormElement = document.getElementById(paramName + '_form');

		switch (configuration.parameters['ParamDescription.'+paramName+'.type']) {
			case 'choice':
				var choices = [];
				if (configuration.parameters['ParamDescription.'+paramName+'.choice0']) { //we have at least one choice available
					var n=0; //start enumerating choices from config file
					while (configuration.parameters['ParamDescription.'+paramName+'.choice'+n]) {
						var choiceData = configuration.parameters['ParamDescription.'+paramName+'.choice'+n].split('|');
						choices.push(choiceData[0]);
						n++;
					}
					isThisOneInputValid = validate_choice(paramFormElement, choices);
				} else { //has said it's choice, but has not given any choice as paramdescription. Default to text, accept all since we don't have an ereg
					isThisOneInputValid = validate_text(paramFormElement, '.*');
				}
				break;
			case 'boolean': //select instead of checkbox, because checkbox does not get sent if unchecked, and receiver servlet then deletes the param.
				isThisOneInputValid = validate_boolean(paramFormElement);
				break;			
			case 'integer':
				var rangeMin = (configuration.parameters['ParamDescription.'+paramName+'.min'] ? configuration.parameters['ParamDescription.'+paramName+'.min'] : null);
				var rangeMax = (configuration.parameters['ParamDescription.'+paramName+'.max'] ? configuration.parameters['ParamDescription.'+paramName+'.max'] : null);
				isThisOneInputValid = validate_integer(paramFormElement, rangeMin, rangeMax);
				break;
			case 'readonly':
				break;
			case 'text':
			default:
				var ereg = (configuration.parameters['ParamDescription.'+paramName+'.ereg'] ? configuration.parameters['ParamDescription.'+paramName+'.ereg'] : '.*');
				isThisOneInputValid = validate_text(paramFormElement, ereg);
		}
		noErrors = noErrors && isThisOneInputValid;
	}
	
	return noErrors;
}

function setErrorMessageForInput(input, errormsg) {
	var elementWrapper = document.getElementById(input.id + '_div');
	var elementErrorMsg = document.getElementById(input.id + '_errormsg');

	elementWrapper.setAttribute('class', elementWrapper.getAttribute('class') + ' in-error');
	elementErrorMsg.innerHTML = errormsg;
}

function clearErrorMessageforInput(input) {
	var elementWrapper = document.getElementById(input.id + '_div');
	var elementErrorMsg = document.getElementById(input.id + '_errormsg');

	elementWrapper.setAttribute('class', elementWrapper.getAttribute('class').replace(' in-error', ''));
	elementErrorMsg.innerHTML = '';
}

function validate_choice(input, choices) {
	var selectedValue = input.options[input.selectedIndex].value;
	if (choices.indexOf(selectedValue) == -1) { 
		setErrorMessageForInput(input, 'Please choose a value from the given options');
		return false;
	} else {
		clearErrorMessageforInput(input);
		return true;
	}
}

function validate_integer(input, min, max) {
	if (!isNumeric(input.value)) {
		setErrorMessageForInput(input, 'Please enter a number');
		return false;
	}
	
	var intValue = parseInt(input.value);
	var isOK=true;
	if (min !== null && intValue < min) { 
			isOK = false;
	}
	if (max !== null && intValue > max) {
		isOK = false
	}

	if (!isOK) {
		setErrorMessageForInput(input, 'Please enter a value within the accepted range');
	} else {
		clearErrorMessageforInput(input);
	}
	
	return isOK;
}

function validate_boolean(input) {
	
	return validate_choice(input, ['true', 'false']); //since it is internally implemented as list
}

function validate_text(input, ereg) {
	var re = new RegExp('^' + ereg + '$');
	if (input.value.match(re)) {
	    clearErrorMessageforInput(input);
	    return true;
	} else {
		setErrorMessageForInput(input, 'Please fill in a value matching the regular expression');
		return false;
	}
}