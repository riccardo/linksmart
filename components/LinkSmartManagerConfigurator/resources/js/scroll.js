/**
 * @author Adrian Rejas
 */
	defaultStep=1;
	step=defaultStep;
	var handled;
	var handlerer;
	var tracker;
	var timerUp;
	var timerDown;
	var timerBar;
	var mousey;
	
	function toTop(id){
		document.getElementById(id).scrollTop=0;
	}

	function scrollDivDown(id){
		var divaux = document.getElementById(id);
		divaux.scrollTop+=step;
		timerDown=setTimeout("scrollDivDown('"+id+"')",10);
	}

	function scrollDivUp(id){
		var divaux = document.getElementById(id);
		divaux.scrollTop-=step;
		timerUp=setTimeout("scrollDivUp('"+id+"')",10);
	}

	function toBottom(id){
		document.getElementById(id).scrollTop=document.getElementById(id).scrollHeight;
	}

	function toPoint(id){
		document.getElementById(id).scrollTop=100;
	}
	
	// scroll the element vertically based on its width and the slider maximum value
	function scrollVertical(value, element, slider) {
		element.scrollTop = Math.round(value/slider.maximum*(element.scrollHeight-element.offsetHeight));
	}
	
	function scrollBarUpdate (id, handler, track) {
		divid = document.getElementById(id);
		divtrack = document.getElementById(track);
		if ((divid == undefined)||(divtrack == undefined)) return;
		divhandler = document.getElementById(handler);
		var percent = divid.scrollTop/(divid.scrollHeight - divid.offsetHeight);
		var hgt = divtrack.offsetHeight - divhandler.offsetHeight;
		var exact = hgt*percent;
		var aprox = Math.round(exact);
		divhandler.style.top = aprox + "px";
		timerBar = setTimeout("scrollBarUpdate('"+id+"','"+handler+"','"+track+"')",10);
	}
	
	// scroll the element horizontally based on its width and the slider maximum value
	function scrollHorizontal(value, element, slider) {
		element.scrollLeft = Math.round(value/slider.maximum*(element.scrollWidth-element.offsetWidth));
	}
	
	function handle(delta) {
        if (delta < 0) {
			document.getElementById(handled).scrollTop += 50;
			scrollBarUpdate (handled, handlerer, tracker);
			clearTimeout(timerBar);
		}
		else {
			document.getElementById(handled).scrollTop -= 50;
			scrollBarUpdate (handled, handlerer, tracker);
			clearTimeout(timerBar);
		}
	}

	function wheel(event){
        var delta = 0;
        if (!event) /* For IE. */
                event = window.event;
        if (event.wheelDelta) { /* IE/Opera. */
                delta = event.wheelDelta/120;
                if (window.opera)
                        delta = -delta;
        } else if (event.detail) { /** Mozilla case. */
                delta = -event.detail/3;
        }
        if (delta)
                handle(delta);
        if (event.preventDefault)
                event.preventDefault();
		event.returnValue = false;
	}
	
	function initdragscroll(event) {
		mousey = event.clientY;
		document.getElementById(handled).onmousemove = mouseposition;
	}
	
	function finishdragscroll(event) {
		document.getElementById(handled).onmousemove = "";
	}
	
	function mouseposition(event){
		var lastmousey = event.clientY;
		if ((lastmousey - mousey) > 20)  mousey = lastmousey;
		document.getElementById(handled).scrollTop -= lastmousey - mousey;
		scrollBarUpdate (handled, handlerer, tracker);
		clearTimeout(timerBar);
		mousey = lastmousey;		
	}

