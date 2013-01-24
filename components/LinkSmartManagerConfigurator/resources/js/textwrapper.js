/**
 * @author Adrian
 */

function wrap(text, size) {
	var returning = "";
	if (text == undefined) return "";
	var data = text.split(";");
	for (var i = 0; i < data.length; i++) {
		var textSize = data[i].length;
		var place = 0;
		if (textSize > size) {
			while ((place + size) < textSize) {
				returning += data[i].substr(place, size);
				returning += "<br>";
				place += size;
			}
			returning += data[i].substr(place, textSize - place);
			if (i != data.length - 1) 
				returning += "<br>";
		}
		else {
			returning += data[i];
			if (i != data.length - 1) 
				returning += "<br>";
		}
	}
	return returning;
}
