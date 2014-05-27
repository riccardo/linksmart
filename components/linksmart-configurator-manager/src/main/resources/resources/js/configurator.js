/*  Configurator JavaScript framework, version 1.0
 *  Author: Pablo Antolin
 *  Modified on 07/2011 by Erion Elmasllari, FIT
 *
 *--------------------------------------------------------------------------*/

var configurationServletAddress = "/LinkSmartConfigurator/GetConfiguration";
var currentConfigurationData = null;

function cssID2mgrID(cssID) {
	return cssID.replace(/-/g, '.');
}

function mgrID2cssID(mgrID) {
	return mgrID.replace(/\./g, '-');

}

function isNumeric(s) {
    return (s - 0) == s && s.length > 0;
}

function getConfigurations(){
		//console.log('getting configs');
		$.ajax({
			url: configurationServletAddress, 
			type: 'GET',
			data: {method: 'listConfigurations'},
			dataType: 'json',
			success: function(responseJSON){
				//mockup
				//responseJSON = '[{"configurations":["eu.linksmart.eventmanager","eu.linksmart.network.NetworkManager"]}]';
//				console.log(responseJSON);
				updateManagerTabs(responseJSON);
			}
		});
		
}

function updateManagerTabs(configurations){


//delete gone tabs
	for (var i = 0; i < $('#mgrtabs .ui-tabs-nav li').length; i++) { //as long as there are
		var oneTab = $('#mgrtabs .ui-tabs-nav li').eq(i);
		//console.log(oneTab);
		var oneTabId = $(oneTab).attr('id').replace(/^li-/g, '');
		var oneMgrId = cssID2mgrID(oneTabId);
		if ($.inArray(oneMgrId, configurations[0].configurations) == -1) {
			//console.log(oneMgrId);
			//tab id is not in configurations
			//delete that tab
			//console.log('#li-' + oneTabId);
			$('#li-' + oneTabId).remove();
			i--; //because one was just removed, so the i+1th new order would be the i+2 old order
			$('#tab-' + oneTabId).remove(); //and remove that panel
		}
	}

	var tabTemplate = '<li id="li-{href}"><a href="#tab-{href}">{label}</a></li>';
	var mgrtabs=$('#mgrtabs');

	//Fill in configuration menu
	for (var i = 0; i < configurations[0].configurations.length; i++) {
		var label = configurations[0].configurations[i];
	    var cssID=mgrID2cssID(label);
	    var newTab = $( tabTemplate.replace( /\{href\}/g, cssID ).replace( /\{label\}/g, label.replace(/^eu\.linksmart\./g, '') ) );

	    //console.log(id);

	    if ($('#mgrtabs .ui-tabs-nav li').filter('#li-' + cssID).length == 0) {
		    mgrtabs.find( ".ui-tabs-nav" ).append( newTab );
		    var tabContentHtml = "Getting configuration data for " + label + '...';
			mgrtabs.append( '<div id="tab-' + cssID + '"><p>' + tabContentHtml + '</p></div>' );
	    }
	}

	mgrtabs.tabs( "refresh" );
   	$('#mgrtabs li').removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );

}




//call this from a before-show event in #mgrtabs.tabs()
function getConfigurationData(configuration){
	$.ajax({
			url: configurationServletAddress, 
			type: 'GET',
			data: { method: 'getConfiguration', configName: configuration },
			dataType: 'json',
			success: function(responseJSON){
				//mockup
				//responseJSON = '[{"name":"eu.linksmart.eventmanager","parameters":[{"name":"CoreSecurityClient","value":"true"},{"name":"EventManagerDescription","value":"EventManager_AARHUS"},{"name":"NetworkManagerAddress","value":"http://localhost:8082/axis/services/NetworkManagerApplication"},{"name":"service.pid","value":"eu.linksmart.eventmanager"},{"name":"SOAPTunnelingAddress","value":"http://localhost:8082/SOAPTunneling"},{"name":"withNetworkManager","value":"true"}]}]';
//				console.log(responseJSON);
				currentConfigurationData = responseJSON;
				showConfigurationDataForm(currentConfigurationData);
			}
		});
}

function makeInputBox(paramName, paramValue) {
	return $('<input/>')
			.attr('type', 'text')
			.addClass("text")
			.attr('name', paramName)
			.attr('id', mgrID2cssID(paramName) + '_form')
			.val(paramValue);

}
function showConfigurationDataForm(configuration){
	
	var panelID = mgrID2cssID(configuration.name);
	
	//Fill in configuration form
	//$('#tab-' + panelID).html("<h1>" + configuration.name + "</h1>");
	$('#tab-' + panelID).empty();
	
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
		var formDiv = '';
		var formCssID=mgrID2cssID(paramName);
		formDiv = $('<div/>')
					.addClass((zebraDark?'postinfoeven':'postinfoodd'))
					.addClass('paramform')
					.attr('id',  formCssID + '_form_div')
					.append($('<label/>').attr('for', formCssID + '_form').text(labelText));
		//console.log(formDiv);
		var formElement='';
		var cssParamName = mgrID2cssID(paramName);
		
		switch (configuration.parameters['ParamDescription.'+paramName+'.type']) {
			case 'choice':
				
				if (configuration.parameters['ParamDescription.'+paramName+'.choice0']) {
					formElement = $('<div/>')
									.attr('id', cssParamName + '_form_wrapper');
					var n=0; //start enumerating choices from config file
					while (configuration.parameters['ParamDescription.'+paramName+'.choice'+n]) {
						var choiceData = configuration.parameters['ParamDescription.'+paramName+'.choice'+n].split('|');
						var option=$('<input/>')
									.attr('type', 'radio')
									.attr('id', cssParamName + '_form_'+n)
									.attr('name', paramName)
									.val(choiceData[0]);
						if (paramValue==choiceData[0]) {
							option.attr('checked', 'checked');
						}
						formElement.append(option);
						formElement.append($('<label/>').text(choiceData[1]).attr('for', cssParamName + '_form_' + n));
						n++;
					}
					formElement.buttonset();
				} else { //has said it's choice, but has not given any choice as paramdescription
					formElement = makeInputBox(paramName, paramValue);
				}
				
				break;
			case 'multichoice':
				
				if (configuration.parameters['ParamDescription.'+paramName+'.choice0']) {
					formElement = $('<div/>')
									.attr('id', cssParamName + '_form_wrapper');
					var n=0; //start enumerating choices from config file
					while (configuration.parameters['ParamDescription.'+paramName+'.choice'+n]) {
						var choiceData = configuration.parameters['ParamDescription.'+paramName+'.choice'+n].split('|');
						var option=$('<input/>')
									.attr('type', 'checkbox')
									.attr('id', cssParamName + '_form_'+n)
									.attr('name', paramName)
									.val(choiceData[0]);
						if (paramValue.indexOf(choiceData[0]) != -1) {
							option.attr('checked', 'checked');
						}
						formElement.append(option);
						formElement.append($('<label/>').text(choiceData[1]).attr('for', cssParamName + '_form_' + n));
						n++;
					}
					formElement.buttonset();
				} else { //has said it's choice, but has not given any choice as paramdescription
					formElement = makeInputBox(paramName, paramValue);
				}
				break;
			case 'boolean': //select instead of checkbox, because checkbox does not get sent if unchecked, and receiver servlet then deletes the param.
				
					formElement = $('<div/>')
									.attr('id', cssParamName + '_form_wrapper');
					
					var yesOption=$('<input/>')
									.attr('type', 'radio')
									.attr('id', cssParamName + '_form_y')
									.attr('name', paramName )
									.val('true');
					if (paramValue=='true') {
						yesOption.attr('checked', 'checked');
					}
					formElement.append(yesOption);
					formElement.append($('<label/>').text('True').attr('for', cssParamName + '_form_y'));
					
					var noOption=$('<input/>')
									.attr('type', 'radio')
									.attr('id', cssParamName + '_form_n')
									.attr('name', paramName )
									.val('false');
					if (paramValue=='false' || paramValue == '') {
						noOption.attr('checked', 'checked');
					}
					formElement.append(noOption);
					formElement.append($('<label/>').text('False').attr('for', cssParamName + '_form_n'));
					formElement.buttonset();
				break;			
			case 'integer':
				
				var rangeMin = (configuration.parameters['ParamDescription.'+paramName+'.min'] ? configuration.parameters['ParamDescription.'+paramName+'.min'] : '');
				var rangeMax = (configuration.parameters['ParamDescription.'+paramName+'.max'] ? configuration.parameters['ParamDescription.'+paramName+'.max'] : '');
				
				formElement = $('<div/>')
							.attr('id', cssParamName + '_form_wrapper');
				var spinValidator;
				
				if (rangeMin != '' && rangeMax != '') {
					formElement.append($('<label/>').attr('for', cssParamName + '_form').text('Range:' + rangeMin + ' - ' + rangeMax).addClass('range'));
				} else if (rangeMin != '') {
					formElement.append($('<label/>').attr('for', cssParamName + '_form').text('Minimum:' + rangeMin).addClass('range'));
				} else if (rangeMax != '') {
					formElement.append($('<label/>').attr('for', cssParamName + '_form').text('Maximum:' + rangeMax).addClass('range'));
				} else {
					formElement.append($('<label/>').attr('for', cssParamName + '_form').text('Any number').addClass('range'));
		        }
				var spinnerBox=$('<input/>')
								.attr('type', 'text')
								.attr('name', paramName)
								.attr('id', cssParamName + '_form')
								.val(paramValue);
			    formElement.append(spinnerBox);
			    spinnerBox.spinner({
		            spin: spinValidator
		        });
			    if (rangeMin != '') {
			    	spinnerBox.spinner('option', 'min', rangeMin);
			    }
			    if (rangeMax != '') {
			    	spinnerBox.spinner('option', 'max', rangeMax);
			    }
			    break;
			case 'readonly':
				formElement = makeInputBox(paramName, paramValue);
				formElement.attr('readonly', 'readonly').addClass('readonly');
				break;
			case 'text':
			default:
				formElement = $('<div/>')
					.attr('id', cssParamName + '_form_wrapper');
				if (configuration.parameters['ParamDescription.'+paramName+'.ereg']) {
					formElement.append($('<label/>')
									.addClass('range')
									.text('Must match this regular expression: ^' + configuration.parameters['ParamDescription.'+paramName+'.ereg'])
									.attr('for', cssParamName + '_form')
									); 
				}
				formElement.append(makeInputBox(paramName, paramValue));
		}
		
		formDiv.append(formElement); 
		$('#tab-' + panelID).append(formDiv);
		zebraDark = !zebraDark;
	}
	var submitButton = $('<button/>')
						.text('Update Configuration')
						.attr('id', 'configurator-submit')
						.click(function () {
							submitConfiguration(configuration.name);
						});
	$('#tab-' + panelID).append($('<div/>').addClass('submit_button_centerizer').append(submitButton));
	$('#configurator-submit').button({
        icons: {
            primary: "ui-icon-disk"
        }
    });

}

function submitConfiguration(configuration){
	
	var statusAndAnswers = validateInputsAndGetAnswers(currentConfigurationData);
	var inputIsOK = statusAndAnswers.inputIsOK;
	var answers = statusAndAnswers.answers;
	
	if (!inputIsOK) {
		alert("Please correct the fields in red -- they contain forbidden or meaningless values.");
		return false;
	}
	
	var jsonText = '[{"name":"' + configuration + '","parameters": [';
	jsonText += answers
	jsonText += ']}]'; 

	$.ajax({
		url: configurationServletAddress,
		type: 'POST',
		data: {	method: 'postConfiguration', configuration: jsonText },
		//dataType: 'json',
		success: function(response){
			//console.log(response);
			alert("Configuration saved");
			
		}
	});
}


function validateInputsAndGetAnswers(configuration) {
	var noErrors = true;
	var isThisOneInputValid = true;
	

	var jsonText = '';
	var validationResult = [];

	for (var paramName in configuration.parameters) {
		var paramValue = configuration.parameters[paramName];
		if (paramName.startsWith('ParamDescription')) {
			continue; //this is only descriptive, should not be usable/configurable.
		}

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
					validationResult = validateChoiceAndGetAnswer(paramName, choices);
				} else { //has said it's choice, but has not given any choice as paramdescription. Default to text, accept all since we don't have an ereg
					validationResult = validateTextAndGetAnswer(paramName, '.*');
				}
				break;
			case 'multichoice':
				var choices = [];
				var numChoicesMin = (configuration.parameters['ParamDescription.'+paramName+'.minChoices'] ? configuration.parameters['ParamDescription.'+paramName+'.minChoices'] : null);
				var numChoicesMax = (configuration.parameters['ParamDescription.'+paramName+'.maxChoices'] ? configuration.parameters['ParamDescription.'+paramName+'.maxChoices'] : null);
				var positionalValidation = (configuration.parameters['ParamDescription.'+paramName+'.positionalValidator'] ? configuration.parameters['ParamDescription.'+paramName+'.positionalValidator'] : '.*');

				if (configuration.parameters['ParamDescription.'+paramName+'.choice0']) { //we have at least one choice available
					var n=0; //start enumerating choices from config file
					while (configuration.parameters['ParamDescription.'+paramName+'.choice'+n]) {
						var choiceData = configuration.parameters['ParamDescription.'+paramName+'.choice'+n].split('|');
						choices.push(choiceData[0]);
						n++;
					}
					validationResult = validateMultichoiceAndGetAnswer(paramName, choices, numChoicesMin, numChoicesMax, positionalValidation);
				} else { //has said it's choice, but has not given any choice as paramdescription. Default to text, accept all since we don't have an ereg
					validationResult = validateTextAndGetAnswer(paramName, '.*');
				}
				break;

			case 'boolean': //select instead of checkbox, because checkbox does not get sent if unchecked, and receiver servlet then deletes the param.
				validationResult = validateBooleanAndGetAnswer(paramName);
				break;			
			case 'integer':
				var rangeMin = (configuration.parameters['ParamDescription.'+paramName+'.min'] ? configuration.parameters['ParamDescription.'+paramName+'.min'] : null);
				var rangeMax = (configuration.parameters['ParamDescription.'+paramName+'.max'] ? configuration.parameters['ParamDescription.'+paramName+'.max'] : null);
				validationResult = validateIntegerAndGetAnswer(paramName, rangeMin, rangeMax);
				break;
			case 'readonly':
				validationResult = {isValid: true, answer: ''};
				break;
			case 'text':
			default:
				var ereg = (configuration.parameters['ParamDescription.'+paramName+'.ereg'] ? configuration.parameters['ParamDescription.'+paramName+'.ereg'] : '.*');
				validationResult = validateTextAndGetAnswer(paramName, ereg);
		}
		
		noErrors = noErrors && validationResult.isValid;
		if (validationResult.answer != '') { //readonly case above
			jsonText += validationResult.answer + ',';
		}
	}
	if (jsonText.length > 0) {
		//remove last , separator
		jsonText = jsonText.substring(0, jsonText.length - 1)
	}
	return { inputIsOK: noErrors, answers: jsonText };
}

function setErrorMessageForInput(paramName, errormsg) {
	cssID = mgrID2cssID(paramName)
	$('#' + cssID + '_form_div')
		.addClass('errorform')
		.append($('<span/>').addClass('errormsg').text(errormsg));
}

function clearErrorMessageforInput(paramName) {
	cssID = mgrID2cssID(paramName)
	$('#' + cssID + '_form_div')
		.removeClass('errorform')
		.children('.errormsg').remove();
}

function validateChoiceAndGetAnswer(paramName, choices) {
	var selectedValue=$('input[name="' + paramName + '"]').filter(':checked').val();
	var isOK = true;
	
	if (choices.indexOf(selectedValue) == -1) {
		setErrorMessageForInput(paramName, 'Please choose a value from the given options');
		isOK = false;
	} else {
		clearErrorMessageforInput(paramName);
		isOK = true;
	}
//	jsonText += '{ "name": "' + arraySelect[i].name + '","value": "' + arraySelect[i].value + '"},'
	var jsonText = '{ "name": "' + paramName + '","value": "' + selectedValue + '"}';
	return {isValid: isOK, answer: jsonText};
}


function validateMultichoiceAndGetAnswer(paramName, choices, minChoices, maxChoices, positionalValidatorEreg) {

	var allValues=$('input[name="' + paramName + '"]');
	var selectionMap = '';
	var selectedValues = '';
	var count = 0;
	
	for (i = 0; i < allValues.length; i++) {
	    if (allValues.eq(i).is(":checked")) {
	      selectionMap += 'x';
	      selectedValues += allValues.eq(i).val() + '|';
	      count++;
	    } else {
	      selectionMap += '_'
	    }
	  }
	
	var isOK = true;
	var cumulativeErrorMessages = '';
	
	if ((minChoices !== null && count < minChoices) || (maxChoices !== null && count > maxChoices)) {
		cumulativeErrorMessages += 'Please choose between ' + minChoices + ' and ' + maxChoices + ' values. ';
		isOK = false;
	}
	
	var re = new RegExp('^' + positionalValidatorEreg + '$');
	if (!selectionMap.match(re)) {
		cumulativeErrorMessages += 'Please observe the limitations given in the description. ';
		isOK = false;
	}
	
	if (isOK == false) { 
		setErrorMessageForInput(paramName, cumulativeErrorMessages);
	} else {
		clearErrorMessageforInput(paramName);
	}

	if (count > 0) {
		//remove last | separator
		selectedValues = selectedValues.substring(0, selectedValues.length - 1)
	}

	var jsonText = '{ "name": "' + paramName + '","value": "' + selectedValues + '"}';
	return {isValid: isOK, answer: jsonText};
}

function validateIntegerAndGetAnswer(paramName, min, max) {

	//	jsonText += '{ "name": "' + arrayInput[i].name + '","value": "' + arrayInput[i].value + '"},'

	var value=$('input[name="' + paramName + '"]').val();

	if (!isNumeric(value)) {
		setErrorMessageForInput(paramName, 'Please enter a number');
		return false;
	}
	
	var intValue = parseInt(value);
	var isOK=true;
	if (min !== null && intValue < min) { 
			isOK = false;
	}
	if (max !== null && intValue > max) {
		isOK = false
	}

	if (!isOK) {
		setErrorMessageForInput(paramName, 'Please enter a value within the accepted range');
	} else {
		clearErrorMessageforInput(paramName);
	}

	//	jsonText += '{ "name": "' + arrayInput[i].name + '","value": "' + arrayInput[i].value + '"},'
	var jsonText = '{ "name": "' + paramName + '","value": "' + intValue + '"}';

	return {isValid: isOK, answer: jsonText};
}

function validateBooleanAndGetAnswer(paramName) {
	return validateChoiceAndGetAnswer(paramName, ['true', 'false']); //since it is internally implemented as a "radio" choice between true and false
}

function validateTextAndGetAnswer(paramName, ereg) {
	var re = new RegExp('^' + ereg + '$');
	var value=$('input[name="' + paramName + '"]').val();
	var isOK = true;
	
	if (value.match(re)) {
	    clearErrorMessageforInput(paramName);
	    isOK = true;
	} else {
		setErrorMessageForInput(paramName, 'Please fill in a value matching the regular expression');
		isOK = false;
	}
	
	var jsonText = '{ "name": "' + paramName + '","value": "' + value + '"}';
	return {isValid: isOK, answer: jsonText};
}