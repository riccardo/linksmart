/**
 * @author Adrian
 */

function getNetworkManagerInfo() {
	networkManagerInfo = '{"available":"yes", "networkManagers":[], "localHids":[], "remoteHids":[]}'.evalJSON();
	networkManagerInfo.networkManagers.splice(0,networkManagerInfo.networkManagers.length);	
	
	new Ajax.Request('http://localhost:8082/GetNetworkManagerStatus', {
		method: 'get',
		parameters: {
			method: 'getNetworkManagers'
		},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				networkManagerInfo.available = "no";
				printNetworkManagers();
				return;
			}
			var networkManagers = response.split("<br>");
			for (var i = 0; i < networkManagers.length; i++) {
				networkManagerInfo.networkManagers[i] = '{"hid":"", "description":"", "host":"", "endpoint":"" }'.evalJSON();
				var data = networkManagers[i].split("|");
				networkManagerInfo.networkManagers[i].hid = data[0];
				networkManagerInfo.networkManagers[i].description = data[1];
				networkManagerInfo.networkManagers[i].host = data[2];
				networkManagerInfo.networkManagers[i].endpoint = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printNetworkManagers();
		}
	});
}

function getLocalHidsInfo() {
	networkManagerInfo.localHids.splice(0,networkManagerInfo.localHids.length);	
	new Ajax.Request('http://localhost:8082/GetNetworkManagerStatus', {
		method: 'get',
		parameters: {
			method: 'getLocalHids'
		},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				networkManagerInfo.available = "no";
				showNetworkManagers();
				return;
			}
			var localHids = response.split("<br>");
			for (var i = 0; i < localHids.length; i++) {
				var data = localHids[i].split("|");
				networkManagerInfo.localHids[i] = '{"hid":"", "description":"", "host":"", "endpoint":"" }'.evalJSON();
				networkManagerInfo.localHids[i].hid = data[0];
				networkManagerInfo.localHids[i].description = data[1];
				networkManagerInfo.localHids[i].host = data[2];
				networkManagerInfo.localHids[i].endpoint = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printLocalHids();
		}
	});
}

function getRemoteHidsInfo() {	
	networkManagerInfo.remoteHids.splice(0,networkManagerInfo.remoteHids.length);	
	new Ajax.Request('http://localhost:8082/GetNetworkManagerStatus', {
		method: 'get',
		parameters: {
			method: 'getRemoteHids'
		},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				networkManagerInfo.available = "no";
				showNetworkManagers();
				return;
			}
			var remoteHids = response.split("<br>");
			for (var i = 0; i < remoteHids.length; i++) {
				if (i == remoteHids.length - 1) {
					if (remoteHids[i] == "")				
						continue;
				}
				var data = remoteHids[i].split("|");
				networkManagerInfo.remoteHids[i] = '{"hid":"", "description":"", "host":"", "endpoint":"" }'.evalJSON();
				networkManagerInfo.remoteHids[i].hid = data[0];
				networkManagerInfo.remoteHids[i].description = data[1];
				networkManagerInfo.remoteHids[i].host = data[2];
				networkManagerInfo.remoteHids[i].endpoint = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printRemoteHids();
		}
	});
}

function getNetworkManagerSearch() {	
	new Ajax.Request('http://localhost:8082/GetNetworkManagerStatus', {
		method: 'get',
		parameters: {
			method: 'getNetworkManagerSearch'
		},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				networkManagerInfo.available = "no";
				showNetworkManagers();
				return;
			}
			var remoteHids = response.split("<br>");
			for (var i = 0; i < remoteHids.length; i++) {
				if (i == remoteHids.length - 1) {
					if (remoteHids[i] == "")				
						continue;
				}
				var data = remoteHids[i].split("|");
				networkManagerInfo.remoteHids[i] = '{"hid":"", "description":"", "host":"", "endpoint":"" }'.evalJSON();
				networkManagerInfo.remoteHids[i].hid = data[0];
				networkManagerInfo.remoteHids[i].description = data[1];
				networkManagerInfo.remoteHids[i].host = data[2];
				networkManagerInfo.remoteHids[i].endpoint = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printNetworkManagerSearch();
		}
	});
}

function printNetworkManagers(){
	if (networkManagerInfo.available == "no") {
		var text = "<h1>Network Manager not available</h1>";
		document.getElementById("search").style.visibility = "hidden";
		document.getElementById("suboptions").style.visibility = "hidden";
		document.getElementById("suboptions").style.display = "none";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("search").style.visibility = "visible";
	document.getElementById("suboptions").style.visibility = "visible";
	document.getElementById("suboptions").style.display = "block";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=20%><h3>HID</h3></TD><TD class=\"hed\" WIDTH=38%><h3>ATTRIBUTES</h3></TD><TD class=\"hed\" WIDTH=34%><h3>ROUTE</h3></TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < networkManagerInfo.networkManagers.length; i++) {
		text += "<TR><TD WIDTH=20%>" + networkManagerInfo.networkManagers[i].hid + "</TD><TD WIDTH=38%>" + wrap(networkManagerInfo.networkManagers[i].description,55) + "</TD><TD WIDTH=34%>" + networkManagerInfo.networkManagers[i].endpoint + "</TD></TR>";
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;

	/*
	
	if (document.getElementById("infoContentwrap").scrollHeight > 400) {
		document.getElementById("tableContainer").style.width = "98%";
		document.getElementById("infoContentwrap").style.width = "98%";
		text = document.getElementById("infoContent").innerHTML;
		text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"LinkSmartStatus/images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"NetworkManagerStatus2/images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"LinkSmartStatus/images/imageflow/slider.png\"/></div></div></div>"
		document.getElementById("infoContent").innerHTML = text;
			    
		handled = 'infoContentwrap';
		handlerer = 'infoScrollbarHandler';
		tracker = 'infoScrollbarTrack';
				
		var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
			axis: 'vertical',
			onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
			onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
		});
				
		if (window.addEventListener) {
			document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
			document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
		}
		
		if (previousScroll < (document.getElementById("infoContentwrap").scrollHeight - 402)) document.getElementById("infoContentwrap").scrollTop = previousScroll;
		else document.getElementById("infoContentwrap").scrollTop = (document.getElementById("infoContentwrap").scrollHeight - 402);
		scrollBarUpdate (handled, handlerer, tracker);
	}
	*/
}

function printLocalHids() {
	if (networkManagerInfo.available == "no") {
		var text = "<h1>Network Manager not available</h1>";
		document.getElementById("search").style.visibility = "hidden";
		document.getElementById("suboptions").style.visibility = "hidden";
		document.getElementById("suboptions").style.display = "none";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("search").style.visibility = "visible";
	document.getElementById("suboptions").style.visibility = "visible";
	document.getElementById("suboptions").style.display = "block";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=20%><h3>HID</h3></TD><TD class=\"hed\" WIDTH=38%><h3>ATTRIBUTES</h3></TD><TD class=\"hed\" WIDTH=34%><h3>ROUTE</h3></TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < networkManagerInfo.localHids.length; i++) {
		text += "<TR><TD WIDTH=20%>" + networkManagerInfo.localHids[i].hid + "</TD><TD WIDTH=38%>" + wrap(networkManagerInfo.localHids[i].description,55) + "</TD><TD WIDTH=34%>" + networkManagerInfo.localHids[i].endpoint + "</TD></TR>";
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;
		
/*
	if (document.getElementById("infoContentwrap").scrollHeight > 400) {
		document.getElementById("tableContainer").style.width = "98%";
		document.getElementById("infoContentwrap").style.width = "98%";
		text = document.getElementById("infoContent").innerHTML;
		text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"LinkSmartStatus/images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"LinkSmartStatus/images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"LinkSmartStatus/images/imageflow/slider.png\"/></div></div></div>"
		document.getElementById("infoContent").innerHTML = text;
			    
		handled = 'infoContentwrap';
		handlerer = 'infoScrollbarHandler';
		tracker = 'infoScrollbarTrack';
				
		var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
			axis: 'vertical',
			onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
			onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
		});
				
		if (window.addEventListener) {
			document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
			document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
		}
		
		if (previousScroll < (document.getElementById("infoContentwrap").scrollHeight - 402)) document.getElementById("infoContentwrap").scrollTop = previousScroll;
		else document.getElementById("infoContentwrap").scrollTop = (document.getElementById("infoContentwrap").scrollHeight - 402);
		scrollBarUpdate (handled, handlerer, tracker);
	}
	*/
}

function printRemoteHids() {
	if (networkManagerInfo.available == "no") {
		var text = "<h1>Network Manager not available</h1>";
		document.getElementById("search").style.visibility = "hidden";
		document.getElementById("suboptions").style.visibility = "hidden";
		document.getElementById("suboptions").style.display = "none";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("search").style.visibility = "visible";
	document.getElementById("suboptions").style.visibility = "visible";
	document.getElementById("suboptions").style.display = "block";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=20%><h3>HID</h3></TD><TD class=\"hed\" WIDTH=38%><h3>ATTRIBUTES</h3></TD><TD class=\"hed\" WIDTH=34%><h3>BACKBONE</h3></TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < networkManagerInfo.remoteHids.length; i++) {
		text += "<TR><TD WIDTH=20%>" + networkManagerInfo.remoteHids[i].hid + "</TD><TD WIDTH=38%>" + wrap(networkManagerInfo.remoteHids[i].description,55) + "</TD><TD WIDTH=34%>" + networkManagerInfo.remoteHids[i].endpoint + "</TD></TR>";
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;
	
	/*
	if (document.getElementById("infoContentwrap").scrollHeight > 400) {
		document.getElementById("tableContainer").style.width = "98%";
		document.getElementById("infoContentwrap").style.width = "98%";
		text = document.getElementById("infoContent").innerHTML;
		text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"LinkSmartStatus/images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"LinkSmartStatus/images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"LinkSmartStatus/images/imageflow/slider.png\"/></div></div></div>"
		document.getElementById("infoContent").innerHTML = text;
			    
		handled = 'infoContentwrap';
		handlerer = 'infoScrollbarHandler';
		tracker = 'infoScrollbarTrack';
				
		var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
			axis: 'vertical',
			onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
			onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
		});
				
		if (window.addEventListener) {
			document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
			document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
		}
		
		if (previousScroll < (document.getElementById("infoContentwrap").scrollHeight - 402)) document.getElementById("infoContentwrap").scrollTop = previousScroll;
		else document.getElementById("infoContentwrap").scrollTop = (document.getElementById("infoContentwrap").scrollHeight - 402);
		scrollBarUpdate (handled, handlerer, tracker);
	}
	*/
}

function printNetworkManagerSearch() {
	var search = document.getElementById('searchtext').value
	if (networkManagerInfo.available == "no") {
		var text = "<h1>Network Manager not available</h1>";
		document.getElementById("search").style.visibility = "hidden";
		document.getElementById("suboptions").style.visibility = "hidden";
		document.getElementById("suboptions").style.display = "none";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("search").style.visibility = "visible";
	document.getElementById("suboptions").style.visibility = "visible";
	document.getElementById("suboptions").style.display = "block";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=20%><h3>HID</h3></TD><TD class=\"hed\" WIDTH=38%><h3>ATTRIBUTES</h3></TD><TD class=\"hed\" WIDTH=34%><h3>ROUTE</h3></TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < networkManagerInfo.localHids.length; i++) {
		if ((networkManagerInfo.localHids[i].hid.indexOf(search) != -1)||(networkManagerInfo.localHids[i].description.indexOf(search) != -1)||(networkManagerInfo.localHids[i].host.indexOf(search) != -1)||(networkManagerInfo.localHids[i].endpoint.indexOf(search) != -1)) {
			text += "<TR><TD WIDTH=20%>" + networkManagerInfo.localHids[i].hid + "</TD><TD WIDTH=38%>" + wrap(networkManagerInfo.localHids[i].description,55) + "</TD><TD WIDTH=34%>" + networkManagerInfo.localHids[i].endpoint + "</TD></TR>";
		}
	}
	for (var i = 0; i < networkManagerInfo.remoteHids.length; i++) {
		if ((networkManagerInfo.remoteHids[i].hid.indexOf(search) != -1)||(networkManagerInfo.remoteHids[i].description.indexOf(search) != -1)||(networkManagerInfo.remoteHids[i].host.indexOf(search) != -1)||(networkManagerInfo.remoteHids[i].endpoint.indexOf(search) != -1)) {
			text += "<TR><TD WIDTH=20%>" + networkManagerInfo.remoteHids[i].hid + "</TD><TD WIDTH=38%>" + wrap(networkManagerInfo.remoteHids[i].description,55) + "</TD><TD WIDTH=34%>" + networkManagerInfo.remoteHids[i].endpoint + "</TD></TR>";
		}
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;
		
	/*
	if (document.getElementById("infoContentwrap").scrollHeight > 400) {
		document.getElementById("tableContainer").style.width = "98%";
		document.getElementById("infoContentwrap").style.width = "98%";
		text = document.getElementById("infoContent").innerHTML;
		text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"LinkSmartStatus/images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"LinkSmartStatus/images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
		text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"LinkSmartStatus/images/imageflow/slider.png\"/></div></div></div>"
		document.getElementById("infoContent").innerHTML = text;
			    
		handled = 'infoContentwrap';
		handlerer = 'infoScrollbarHandler';
		tracker = 'infoScrollbarTrack';
				
		var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
			axis: 'vertical',
			onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
			onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
		});
				
		if (window.addEventListener) {
			document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
			document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
		}
		
		if (previousScroll < (document.getElementById("infoContentwrap").scrollHeight - 402)) document.getElementById("infoContentwrap").scrollTop = previousScroll;
		else document.getElementById("infoContentwrap").scrollTop = (document.getElementById("infoContentwrap").scrollHeight - 402);
		scrollBarUpdate (handled, handlerer, tracker);

	}
	*/
}

/*function getNetworkManagers(){
	new Ajax.Request('php/getNetworkManagers.php', {
		method: 'post',
		parameters: {},
		onComplete: function(transport){
			var response = transport.responseText;
			var networkManagers = response.split("<br>");
			var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
			text += "<TR><TD class=\"hed\" WIDTH=20%>HID</TD><TD class=\"hed\" WIDTH=38%>DESCRIPTION</TD><TD class=\"hed\" WIDTH=10%>  HOST</TD><TD class=\"hed\" WIDTH=34%>ROUTE</TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
			text += "<TABLE class=\"stats\" WIDTH=100%>";
			for (var i = 0; i < networkManagers.length; i++) {
				var data = networkManagers[i].split("|");
				text += "<TR><TD WIDTH=20%>" + data[0] + "</TD><TD WIDTH=38%>" + data[1] + "</TD><TD WIDTH=10%>" + data[2] + "</TD><TD WIDTH=34%>" + data[3] + "</TD></TR>";
			}
			text += "</TABLE></div>";
			document.getElementById("infoContent").innerHTML = text;
			
			if (document.getElementById("infoContentwrap").scrollHeight > 400) {
				document.getElementById("tableContainer").style.width = "97%";
				document.getElementById("infoContentwrap").style.width = "97%";
				text = document.getElementById("infoContent").innerHTML;
				text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"images/imageflow/slider.png\"/></div></div></div>"
				document.getElementById("infoContent").innerHTML = text;
			    
				handled = 'infoContentwrap';
				handlerer = 'infoScrollbarHandler';
				tracker = 'infoScrollbarTrack';
				
				var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
					axis: 'vertical',
					onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
					onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
				});
				
				if (window.addEventListener) {
					document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
					document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
				}
				
				if (num != undefined) {
					document.getElementById("infoContentwrap").scrollTop = prevscroll;
					div2 = document.getElementById("infoScrollbarHandler");
					div2.style.top = prevbarpos;
				}
			}	
		}
	});
}

function getLocalHids() {
	new Ajax.Request('php/getLocalHids.php', {
		method: 'post',
		parameters: {},
		onComplete: function(transport){
			var response = transport.responseText;
			var localhids = response.split("<br>");
			var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
			text += "<TR><TD class=\"hed\" WIDTH=17%>HID</TD><TD class=\"hed\" WIDTH=41%>DESCRIPTION</TD><TD class=\"hed\" WIDTH=10%>  HOST</TD><TD class=\"hed\" WIDTH=34%>ROUTE</TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
			text += "<TABLE class=\"stats\" WIDTH=100%>";
			for (var i = 0; i < localhids.length; i++) {
				var data = localhids[i].split("|");
				text += "<TR><TD WIDTH=17%>" + data[0] + "</TD><TD WIDTH=41%>" + data[1] + "</TD><TD WIDTH=10%>" + data[2] + "</TD><TD WIDTH=34%>" + data[3] + "</TD></TR>";
			}
			text += "</TABLE></div>";
			document.getElementById("infoContent").innerHTML = text;
			
			if (document.getElementById("infoContentwrap").scrollHeight > 400) {
				document.getElementById("tableContainer").style.width = "97%";
				document.getElementById("infoContentwrap").style.width = "97%";
				text = document.getElementById("infoContent").innerHTML;
				text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"images/imageflow/slider.png\"/></div></div></div>"
				document.getElementById("infoContent").innerHTML = text;
			    
				handled = 'infoContentwrap';
				handlerer = 'infoScrollbarHandler';
				tracker = 'infoScrollbarTrack';
				
				var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
					axis: 'vertical',
					onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
					onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
				});
				
				if (window.addEventListener) {
					document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
					document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
				}
				
				if (num != undefined) {
					document.getElementById("infoContentwrap").scrollTop = prevscroll;
					div2 = document.getElementById("infoScrollbarHandler");
					div2.style.top = prevbarpos;
				}
			}
		}
	});
}

function getRemoteHids() {
	new Ajax.Request('php/getRemoteHids.php', {
		method: 'post',
		parameters: {},
		onComplete: function(transport){
			var response = transport.responseText;
			var remotehids = response.split("<br>");
			var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
			text += "<TR><TD class=\"hed\" WIDTH=17%>HID</TD><TD class=\"hed\" WIDTH=41%>DESCRIPTION</TD><TD class=\"hed\" WIDTH=10%>  HOST</TD><TD class=\"hed\" WIDTH=34%>ROUTE</TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
			text += "<TABLE class=\"stats\" WIDTH=100%>";
			for (var i = 0; i < remotehids.length; i++) {
				var data = remotehids[i].split("|");
				text += "<TR><TD WIDTH=17%>" + data[0] + "</TD><TD WIDTH=41%>" + data[1] + "</TD><TD WIDTH=10%>" + data[2] + "</TD><TD WIDTH=34%>" + data[3] + "</TD></TR>";
			}
			text += "</TABLE></div>";
			document.getElementById("infoContent").innerHTML = text;
			
			if (document.getElementById("infoContentwrap").scrollHeight > 400) {
				document.getElementById("tableContainer").style.width = "97%";
				document.getElementById("infoContentwrap").style.width = "97%";
				text = document.getElementById("infoContent").innerHTML;
				text += "<div style=\"position: absolute; right:0px; top: 0px; width: 2%; height:425px\" id=\"infoScrollbar\"><IMG  id=\"infoScrollUp\" style=\"position:absolute; cursor:pointer;\" onmousedown=\"step=10;scrollDivUp('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerUp); clearTimeout(timerBar);\" onmouseup=\"clearTimeout(timerUp); clearTimeout(timerBar);\" SRC=\"images/imageflow/button_up.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<IMG id=\"infoScrollDown\" onmousedown=\"step=10;scrollDivDown('infoContentwrap'); scrollBarUpdate ('infoContentwrap', 'infoScrollbarHandler', 'infoScrollbarTrack');\" onmouseout=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" onmouseup=\"clearTimeout(timerDown); clearTimeout(timerBar);\" style=\"position:absolute; cursor:pointer;\" SRC=\"images/imageflow/button_down.png\" WIDTH=17 HEIGHT=17 BORDER=0>";
				text += "<div id=\"infoScrollbarTrack\"><div id=\"infoScrollbarLine\"></div><div id=\"infoScrollbarHandler\"><img src=\"images/imageflow/slider.png\"/></div></div></div>"
				document.getElementById("infoContent").innerHTML = text;
			    
				handled = 'infoContentwrap';
				handlerer = 'infoScrollbarHandler';
				tracker = 'infoScrollbarTrack';
				
				var slider = new Control.Slider('infoScrollbarHandler', 'infoScrollbarTrack', {
					axis: 'vertical',
					onSlide: function(v) { scrollVertical(v, $('infoContentwrap'), slider);  },
					onChange: function(v) { scrollVertical(v, $('infoContentwrap'), slider); }
				});
				
				if (window.addEventListener) {
					document.getElementById("infoContentwrap").addEventListener('DOMMouseScroll', wheel, false);
					document.getElementById("infoContentwrap").onmousewheel = document.onmousewheel = wheel;
				}
				
				if (num != undefined) {
					document.getElementById("infoContentwrap").scrollTop = prevscroll;
					div2 = document.getElementById("infoScrollbarHandler");
					div2.style.top = prevbarpos;
				}
			}
		}
	});
}*/
