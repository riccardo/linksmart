/**
 * @author Adrian
 */

function getEventManagerInfo() {
	eventManagerInfo = '{"available":"yes", "subscriptions":[]}'.evalJSON();
	
	new Ajax.Request('http://localhost:8082/GetEventManagerSubscriptions', {
		method: 'get',
		parameters: {},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				eventManagerInfo.available = "no";
				printEventManagerSubscriptions();
				return;
			}
			var eventSubscriptions = response.split("<br>");
			if (response != "") var length = eventSubscriptions.length;
			else var length = 0
			for (var i = 0; i < length; i++) {
				eventManagerInfo.subscriptions[i] = '{"topic":"", "hid":"", "date":"", "counter":"" }'.evalJSON();
				var data = eventSubscriptions[i].split("|");
				eventManagerInfo.subscriptions[i].topic = data[0];
				eventManagerInfo.subscriptions[i].hid = data[1];
				eventManagerInfo.subscriptions[i].date = data[2];
				eventManagerInfo.subscriptions[i].counter = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printEventManagerSubscriptions();
		}
	});
}

function getEventManagerSearch() {
	eventManagerInfo = '{"available":"yes", "subscriptions":[]}'.evalJSON();
	
	new Ajax.Request('http://localhost:8082/GetEventManagerSubscriptions', {
		method: 'get',
		parameters: {},
		onComplete: function(transport){
			var response = transport.responseText;
			if (response.indexOf("Error 404") != -1) {
				eventManagerInfo.available = "no";
				printEventManagerSearch();
				return;
			}
			var eventSubscriptions = response.split("<br>");
			if (response != "") var length = eventSubscriptions.length;
			else var length = 0
			for (var i = 0; i < length; i++) {
				eventManagerInfo.subscriptions[i] = '{"topic":"", "hid":"", "date":"", "counter":"" }'.evalJSON();
				var data = eventSubscriptions[i].split("|");
				eventManagerInfo.subscriptions[i].topic = data[0];
				eventManagerInfo.subscriptions[i].hid = data[1];
				eventManagerInfo.subscriptions[i].date = data[2];
				eventManagerInfo.subscriptions[i].counter = data[3];
			}
			if (loadPrevScroll == 1) {
				previousScroll = document.getElementById("infoContentwrap").scrollTop;
			}
			else {
				previousScroll = 0;
				loadPrevScroll = 1;
			}
			printEventManagerSearch();
		}
	});
}

function printEventManagerSubscriptions(){
	if (eventManagerInfo.available == "no") {
		var text = "<h1>Event Manager not available</h1>";
		document.getElementById("searchEvent").style.visibility = "hidden";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("searchEvent").style.visibility = "visible";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=33%><h3>TOPIC</h3></TD><TD class=\"hed\" WIDTH=32%><h3>ENDPOINT</h3></TD><TD class=\"hed\" WIDTH=25%><h3>DATE</h3></TD><TD class=\"hed\" WIDTH=10%><h3>COUNTER</h3></TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < eventManagerInfo.subscriptions.length; i++) {
		text += "<TR><TD WIDTH=33%>" + wrap(eventManagerInfo.subscriptions[i].topic,50) + "</TD><TD WIDTH=32%>" + wrap(eventManagerInfo.subscriptions[i].hid,48) + "</TD><TD WIDTH=25%>" + eventManagerInfo.subscriptions[i].date + "</TD><TD WIDTH=10%>" + eventManagerInfo.subscriptions[i].counter + "</TD></TR>";
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;
		
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
}

function printEventManagerSearch() {
	var search = document.getElementById('searchEventtext').value
	if (eventManagerInfo.available == "no") {
		var text = "Event Manager not available";
		document.getElementById("searchEvent").style.visibility = "hidden";
		document.getElementById("infoContent").innerHTML = text;
		return;
	}
	document.getElementById("searchEvent").style.visibility = "visible";
	var text = "<div id=\"tableContainer\" style=\"position:relative; overflow: hidden; width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\"><TABLE class=\"stats\" WIDTH=100%>";
	text += "<TR><TD class=\"hed\" WIDTH=40%>TOPIC</TD><TD class=\"hed\" WIDTH=25%>ENDPOINT</TD><TD class=\"hed\" WIDTH=25%>DATE</TD><TD class=\"hed\" WIDTH=10%>COUNTER</TD></TR></TABLE></div><div style=\"width: 100%; border-right: 1px solid #666666;border-left: 1px solid #666666;border-top: 1px solid #666666;\" id=\"infoContentwrap\">";
	text += "<TABLE class=\"stats\" WIDTH=100%>";
	for (var i = 0; i < eventManagerInfo.subscriptions.length; i++) {
		if ((eventManagerInfo.subscriptions[i].topic.indexOf(search) != -1)||(eventManagerInfo.subscriptions[i].hid.indexOf(search) != -1)||(eventManagerInfo.subscriptions[i].date.indexOf(search) != -1)||(eventManagerInfo.subscriptions[i].counter.indexOf(search) != -1)) {
			text += "<TR><TD WIDTH=40%>" + eventManagerInfo.subscriptions[i].topic + "</TD><TD WIDTH=25%>" + eventManagerInfo.subscriptions[i].hid + "</TD><TD WIDTH=25%>" + eventManagerInfo.subscriptions[i].date + "</TD><TD WIDTH=10%>" + eventManagerInfo.subscriptions[i].counter + "</TD></TR>";
		}
	}
	text += "</TABLE></div>";
	document.getElementById("infoContent").innerHTML = text;
		
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
	else {
		document.getElementById("infoContentwrap").scrollTop = 0;
	}
}