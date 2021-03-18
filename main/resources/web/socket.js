
ws = new WebSocket(window.location.href.replace(/^http/, 'ws'));

function config() {
	if (typeof ws !== 'undefined') {
		var lvl = document.getElementById('lvl').value;
		var clz = document.getElementById('clz').value;
		var msg = document.getElementById('msg').value;
		var lns = document.getElementById('lns').value;
		var buf = document.getElementById('buf').value;
		ws.send('config level=' + lvl + ' class=' + clz + ' message=' + msg + ' lines=' + lns + ' buffer=' + buf);
	}
}

function cls() {
	ws.send('clear');
	return false;
}

function fetch() {
	if (typeof ws === 'undefined') {
		document.getElementById('message').innerHTML = 'not connected!';
	} else {
		ws.send('fetch');
	}
}

function shorten(name) {
	short=name.replace(/.*[.]/g, '')
	return '<span title="' + name + '">' + short + '</span>'
}

ws.onopen = function() {
	document.getElementById('message').innerHTML = 'connected!';
	config();
};

ws.onmessage = function(raw){
	var trs = '';
	var lines = raw.data.split();

	if(lines != "") {

		for (var line of raw.data.split("\n")) {
			cells = line.split(',', 3);
			trs += '<tr class="' + cells[0].toLowerCase() + '">';
			trs += '<td>' + cells[0] + '</td>'
			trs += '<td>' + shorten(cells[1]) + '</td>'
			trs += '<td>' + cells[2] + '</td>'
			trs += '</tr>';
		}
	}
	document.getElementById('tbody').innerHTML = trs;
	document.getElementById('message').innerHTML = '';
};

ws.onclose = function() {
	document.getElementById('message').innerHTML += 'closed!';
};

setInterval(fetch, 100); 

