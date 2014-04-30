/**
 * @author Adrian
 */

var NMServletURL = '/GetNetworkManagerStatus';

function getNetworkManagerInfo() {
	$.ajax({
		url: NMServletURL,
		type: 'GET',
		data: {	method: 'getNetworkManagers' },
		dataType: 'json',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.NMAvailable = false;
			networkManagerInfo.NMs = [];
			updateNMViews('network-managers');
		},
		success: function(response) {
			networkManagerInfo.NMs = response.VirtualAddresses;
			//console.log(response.HIDs);
			updateNMViews('network-managers');
		}
	});
}

function getLocalServicesInfo() {
	$.ajax({
		url: NMServletURL, 
		type: 'GET',
		data: {	method: 'getLocalServices'	},
		dataType: 'json',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.LocalAvailable = false;
			networkManagerInfo.LocalServices = [];
			updateNMViews('local-services');
		},
		success: function(response) {
			networkManagerInfo.LocalServices = response.VirtualAddresses;
			updateNMViews('local-services');
		}
	});
}

function getRemoteServicesInfo() {	
	$.ajax({
		url: NMServletURL,
		type: 'GET',
		data: {	method: 'getRemoteServices'	},
		dataType: 'json',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.RemoteAvailable = false;
			networkManagerInfo.RemoteServices = [];
			updateNMViews('remote-services');
		},
		success: function(response) {
			networkManagerInfo.RemoteServices = response.VirtualAddresses;
			updateNMViews('remote-services');
		}
	});
}

/*
 * the search is anyway done only within the HTML we already got, so this function is not needed for now
 *
function getNetworkManagerSearch() {
	var searchWord=$('#hid-filter-input').val().trim();
	
	if (searchWord != '') {
		$.ajax({
			url: NMServletURL,
			type: 'GET',
			data: {	method: 'getNetworkManagerSearch' },
			dataType: 'json',
			error: function(jqXHR, textStatus, errorThrown) {
				networkManagerInfo.SearchAvailable = false;
				networkManagerInfo.SearchHIDs = [];
				updateNMViews('search-hids');
			},
			success: function(response) {
				networkManagerInfo.SearchHIDs = response.HIDs;
				updateNMViews('search-hids');
			}
		});
	}
}
*/

function updateNMViews(view) {
	if (view=='network-managers') {
		syncServiceListToView('network-managers', networkManagerInfo.NMs);
	} else 	if (view=='local-services') {
		syncServiceListToView('local-services', networkManagerInfo.LocalServices);
	} else 	if (view=='remote-services') {
		syncServiceListToView('remote-services', networkManagerInfo.RemoteServices);
	} else 	if (view=='network-managers') {
		syncServiceListToView('search-services', networkManagerInfo.SearchServices);
	}
}


function doesNMDataHaveThisVirtualAddress(NMData, virtualAddress) {
	if (NMData.hid === virtualAddress) {
		return true;
	} else {
		return false;
	}
}

function syncServiceListToView(cssClass, newData) {
	var filter = '.' + cssClass;
	
	if (newData.length == 0) {
		$('#service-list-data tr').filter(filter).addClass('ui-state-highlight').hide('slow', function(){ $('#service-list-data tr').filter(filter).remove(); showHideServiceViews(); });
	} else {
		var serviceRows=$('#service-list-data tr').filter(filter);

		//delete gone services
		for (var i = 0; i < serviceRows.length; i++) { //as long as there are
			var oneService = serviceRows.eq(i).find('.service').text();
			var foundIn = $.each(newData, function(index, value) { 
									return doesNMDataHaveThisVirtualAddress(newData[index], oneService);
								});
			if (foundIn.length == 0) {
				//not found anywhere, this HID should be removed
				var thisRow = serviceRows.eq(i);
				thisRow.addClass('ui-state-highlight').hide('slow', function(){ thisRow.remove();});
				i--; //because one was just removed, so the i+1th new order would be the i+2 old order
			}
		}

		//at this point all that remains are the known and still-active services
		var knownServices = serviceRows.find('.service');

		//now insert new services
		var rowTemplate = '<tr class="' + cssClass + '"><td class="service">{service}<span class="service-extras"><a class="service-path" href="{path}">link</a> <a class="service-wsdl" href="{wsdl}">wsdl</a></span></td><td class="description">{description}</td><td class"host">{host}</td><td class="endpoint">{endpoint}</td></tr>';
		var serviceTable = $('#service-list-data');
		for (var i = 0; i < newData.length; i++) {
			var foundIn = $.each(knownServices, function(index, value) { 
					//console.log(value);
					if ($(value).html().trim() == newData[i].virtualAddress.trim()) {
						return true;
					} else {
						return false;
					}
			});

			if (foundIn.length == 0) { //i.e. not known
			    var newRow = $(	 rowTemplate.replace( /\{service\}/g, newData[i].virtualAddress )
			    							.replace( /\{description\}/g, newData[i].description )
			    							.replace( /\{host\}/g, newData[i].host )
			    							.replace( /\{endpoint\}/g, newData[i].endpoint)
			    							.replace(/\{path\}/g, newData[i].path)
			    							.replace (/\{wsdl\}/g, newData[i].wsdl) );

			    var searchWord=$('#service-filter-input').val().trim();
			    if (searchWord.length > 0) {
					if (newRow.html().indexOf(searchWord) == -1) { //this new row does not have the search word we're currently searching, mark it to be hidden
						newRow.addClass('filter-hide');
					}
				}
			    serviceTable.append( newRow );
		    }
		}
	}
	
	showHideServiceViews(); //remove the "no data" part, or show it if we really have nothing to report
}


function showSearchResults() {

	var searchWord=$('#service-filter-input').val().trim();
	var serviceRows=$('#service-list-data tr');

	if (searchWord.length > 0) {
		var rows = serviceRows.length;
		for (var i=0; i < rows; i++) {
			var oneRow = serviceRows.eq(i);
			if (oneRow.html().indexOf(searchWord) >= 0) {
				oneRow.removeClass('filter-hide');
			} else {
				oneRow.addClass('filter-hide');
			}
		}
	} else {
		serviceRows.removeClass('filter-hide');
	}
	showHideServiceViews();
}

function showHideServiceViews() {

	if ($('#service-list-data tr').length > 0) {
		$('#service-list-empty').hide();
		$('#service-list-data-table').show();
	} else {
		$('#service-list-empty').show();
		$('#service-list-data-table').hide();
	}

	var stillVisible=jQuery();
	
	if ( $( '#service-type-nm' ).is(':checked') ) {
		$('#service-list-data .network-managers').filter('.filter-hide').hide('slow');
		$('#service-list-data .network-managers').not('.filter-hide').show('slow');
		stillVisible = stillVisible.add($('#service-list-data .network-managers').not('.filter-hide'));
	 } else {
		$('#service-list-data .network-managers').hide('slow');
	 }

	 if ( $( '#service-type-local' ).is(':checked') ) {
		$('#service-list-data .local-services').filter('.filter-hide').hide('slow');
		$('#service-list-data .local-services').not('.filter-hide').show('slow');
		stillVisible = stillVisible.add($('#service-list-data .local-services').not('.filter-hide'));
	 } else {
 		$('#service-list-data .local-services').hide('slow');
   	 }

	 if ( $( '#service-type-remote' ).is(':checked') ) {
		$('#service-list-data .remote-services').filter('filter-hide').hide('slow');
		$('#service-list-data .remote-services').not('filter-hide').show('slow');
		stillVisible = stillVisible.add($('#service-list-data .remote-services').not('.filter-hide'));
	 } else {
 		$('#service-list-data .remote-services').hide('slow');
   	 }
	 
	 if (stillVisible.length > 0) {
		 //there are at least some rows after filtering; make sure table header is shown and the "no data in this view" is not
		 $('#service-table-header').show();
		 $('#service-table-no-data').hide();
	 } else {
		 //there are no rows after filtering; make sure table header is hidden and the "no data in this view" is shown
		 $('#service-table-header').hide();
		 $('#service-table-no-data').show();
	 }
	 
	 //console.log(stillVisible);
}
