/**
 * @author Adrian
 */

var NMServletURL = 'http://localhost:8082/GetNetworkManagerStatus';

function getNetworkManagerInfo() {
	$.ajax({
		url: NMServletURL,
		type: 'GET',
		data: {	method: 'getNetworkManagers' },
		dataType: 'html',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.NMAvailable = false;
			updateNMViews('network-managers');
		},
		success: function(response) {
			var networkManagers = response.split("<br>");
			for (var i = 0; i < networkManagers.length; i++) {
				if (networkManagers[i] == '') continue; //empty lines at end
				var data = networkManagers[i].split("|");
				networkManagerInfo.NMs[i] = {hid:data[0], description:data[1], host:data[2], endpoint:data[3] };
			}
			updateNMViews('network-managers');
		}
	});
}

function getLocalHidsInfo() {
	$.ajax({
		url: NMServletURL, 
		type: 'GET',
		data: {	method: 'getLocalHids'	},
		dataType: 'html',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.LocalAvailable = false;
			updateNMViews('local-hids');
		},
		success: function(response) {
			var localHids = response.split("<br>");
			for (var i = 0; i < localHids.length; i++) {
				if (localHids[i] == '') continue; //empty lines at end
				var data = localHids[i].split("|");
				networkManagerInfo.LocalHIDs[i] = {hid:data[0], description:data[1], host:data[2], endpoint:data[3] };
			}
			updateNMViews('local-hids');
		}
	});
}

function getRemoteHidsInfo() {	
	$.ajax({
		url: NMServletURL,
		type: 'GET',
		data: {	method: 'getRemoteHids'	},
		dataType: 'html',
		error: function(jqXHR, textStatus, errorThrown) {
			networkManagerInfo.RemoteAvailable = false;
			updateNMViews('remote-hids');
		},
		success: function(response) {
			var remoteHids = response.split("<br>");
			for (var i = 0; i < remoteHids.length; i++) {
				if (remoteHids[i] == "") continue; //empty lines at end
				var data = remoteHids[i].split("|");
				networkManagerInfo.RemoteHids[i] = {hid:data[0], description:data[1], host:data[2], endpoint:data[3] };
			}
			updateNMViews('remote-hids');
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
			dataType: 'html',
			error: function(jqXHR, textStatus, errorThrown) {
				networkManagerInfo.SearchAvailable = false;
				updateNMViews('search-hids');
			},
			success: function(response) {
				var remoteHids = response.split("<br>");
				for (var i = 0; i < remoteHids.length; i++) {
					if (remoteHids[i] == "") continue; //empty lines at end
					var data = remoteHids[i].split("|");
					networkManagerInfo.SearchHIDs[i] = {hid:data[0], description:data[1], host:data[2], endpoint:data[3] };
				}
				updateNMViews('search-hids');
			}
		});
	}
}
*/

function updateNMViews(view) {
//sync the data to their availability
	if (!networkManagerInfo.NMAvailable) {
		networkManagerInfo.NMs = [];
	} 
	
	if (!networkManagerInfo.LocalAvailable) {
		networkManagerInfo.LocalHIDs = [];
	}
	
	if (!networkManagerInfo.RemoteAvailable) {
		networkManagerInfo.RemoteHIDs = [];
	}
	
	if (!networkManagerInfo.SearchAvailable) {
		networkManagerInfo.SearchHIDs = [];
	}

	//console.log(view);
	//console.log(networkManagerInfo); 

	if (view=='network-managers') {
		syncHIDListToView('network-managers', networkManagerInfo.NMs);
	} else 	if (view=='local-hids') {
		syncHIDListToView('local-hids', networkManagerInfo.LocalHIDs);
	} else 	if (view=='remote-hids') {
		syncHIDListToView('remote-hids', networkManagerInfo.RemoteHIDs);
	} else 	if (view=='network-managers') {
		syncHIDListToView('search-hids', networkManagerInfo.SearchHIDs);
	}
}


function doesNMDataHaveThisHID(NMData, HID) {
	if (NMData.hid === HID) {
		return true;
	} else {
		return false;
	}
}

function syncHIDListToView(cssClass, newData) {
	var filter = '.' + cssClass;
	
	if (newData.length == 0) {
		$('#hid-list-data tr').filter(filter).addClass('ui-state-highlight').hide('slow', function(){ $('#hid-list-data tr').filter(filter).remove(); showHideHIDView(); });
	} else {
		var HIDRows=$('#hid-list-data tr').filter(filter);

		//delete gone HIDs
		for (var i = 0; i < HIDRows.length; i++) { //as long as there are
			var oneHID = HIDRows.eq(i).find('td:first').html(); //not text because the server sends us HTML!!
			var foundIn = $.each(newData, function(index, value) { 
									return doesNMDataHaveThisHID(newData[index], oneHID);
								});
			if (foundIn.length == 0) {
				//not found anywhere, this HID should be removed
				var thisRow = HIDRows.eq(i);
				thisRow.addClass('ui-state-highlight').hide('slow', function(){ thisRow.remove();});
				i--; //because one was just removed, so the i+1th new order would be the i+2 old order
			}
		}

		//at this point all that remains are the known and still-active HIDs
		var knownHIDs = HIDRows.find('.hid');

		//now insert new HIDs
		var rowTemplate = '<tr class="' + cssClass + '"><td class="hid">{hid}</td><td class="description">{description}</td><td class"host">{host}</td><td class="endpoint">{endpoint}</td></tr>';
		var HIDTable = $('#hid-list-data');
		for (var i = 0; i < newData.length; i++) {
			var foundIn = $.each(knownHIDs, function(index, value) { 
					//console.log(value);
					if ($(value).html().trim() == newData[i].hid.trim()) {
						return true;
					} else {
						return false;
					}
			});

			if (foundIn.length == 0) { //i.e. not known
			    var newRow = $(	 rowTemplate.replace( /\{hid\}/g, newData[i].hid )
			    							.replace( /\{description\}/g, newData[i].description )
			    							.replace( /\{host\}/g, newData[i].host )
			    							.replace( /\{endpoint\}/g, newData[i].endpoint) );

			    var searchWord=$('#hid-filter-input').val().trim();
			    if (searchWord.length > 0) {
					if (newRow.html().indexOf(searchWord) == -1) { //this new row does not have the search word we're currently searching, mark it to be hidden
						newRow.addClass('filter-hide');
					}
				}
			    HIDTable.append( newRow );
		    }
		}
	}
	
	showHideHIDViews(); //remove the "no data" part, or show it if we really have nothing to report
}


function showSearchResults() {

	var searchWord=$('#hid-filter-input').val().trim();
	var HIDRows=$('#hid-list-data tr');

	if (searchWord.length > 0) {
		var rows = HIDRows.length;
		for (var i=0; i < rows; i++) {
			var oneRow = HIDRows.eq(i);
			if (oneRow.html().indexOf(searchWord) >= 0) {
				oneRow.removeClass('filter-hide');
			} else {
				oneRow.addClass('filter-hide');
			}
		}
	} else {
		HIDRows.removeClass('filter-hide');
	}
	showHideHIDViews();
}

function showHideHIDViews() {

	if ($('#hid-list-data tr').length > 0) {
		$('#hid-list-empty').hide();
		$('#hid-list-data-table').show();
	} else {
		$('#hid-list-empty').show();
		$('#hid-list-data-table').hide();
	}

	var stillVisible=jQuery();
	
	if ( $( '#hid-type-nm' ).is(':checked') ) {
		$('#hid-list-data .network-managers').filter('.filter-hide').hide('slow');
		$('#hid-list-data .network-managers').not('.filter-hide').show('slow');
		stillVisible = stillVisible.add($('#hid-list-data .network-managers').not('.filter-hide'));
	 } else {
		$('#hid-list-data .network-managers').hide('slow');
	 }

	 if ( $( '#hid-type-local' ).is(':checked') ) {
		$('#hid-list-data .local-hids').filter('.filter-hide').hide('slow');
		$('#hid-list-data .local-hids').not('.filter-hide').show('slow');
		stillVisible = stillVisible.add($('#hid-list-data .local-hids').not('.filter-hide'));
	 } else {
 		$('#hid-list-data .local-hids').hide('slow');
   	 }

	 if ( $( '#hid-type-remote' ).is(':checked') ) {
		$('#hid-list-data .remote-hids').filter('filter-hide').hide('slow');
		$('#hid-list-data .remote-hids').not('filter-hide').show('slow');
		stillVisible = stillVisible.add($('#hid-list-data .remote-hids').not('.filter-hide'));
	 } else {
 		$('#hid-list-data .remote-hids').hide('slow');
   	 }
	 
	 if (stillVisible.length > 0) {
		 //there are at least some rows after filtering; make sure table header is shown and the "no data in this view" is not
		 $('#hid-table-header').show();
		 $('#hid-table-no-data').hide();
	 } else {
		 //there are no rows after filtering; make sure table header is hidden and the "no data in this view" is shown
		 $('#hid-table-header').hide();
		 $('#hid-table-no-data').show();
	 }
	 
	 //console.log(stillVisible);
}
