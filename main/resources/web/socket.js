
connecting = false;

function openSocket() {
	var connected = !(typeof ws === 'undefined' || ws.readyState !== WebSocket.OPEN);
	if(connected || connecting) {
		return;
	}
	connecting = true;
	console.log('tiqqer: connecting ' + new Date());
	ws = new WebSocket(window.location.href.replace(/^http/, 'ws'));
	connecting = false;

	if(ws == null) {
		return;
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
				cells = line.split(',');
				if(cells.length < 3) {
					console.log('invalid line: ' + line);
					continue;
				}
				trs += '<tr class="' + cells[0].toLowerCase() + '">';
				trs += '<td>' + cells[0] + '</td>'
				trs += '<td>' + shorten(cells[1]) + '</td>'
				trs += '<td>' + line.replace(/[^,]*,[^,]*,/, '') + '</td>'
				trs += '</tr>';
			}
		}
		document.getElementById('tbody').innerHTML = trs;
		document.getElementById('message').innerHTML = '';
	};

	ws.onclose = function() {
		document.getElementById('message').innerHTML += 'closed!';
	};
}

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
	if(typeof ws !== 'undefined' && ws.readyState === WebSocket.OPEN) {
		ws.send('clear');
	}
	return false;
}

function fetch() {
	if(typeof ws === 'undefined' || ws.readyState !== WebSocket.OPEN) {
		document.getElementById('message').innerHTML = 'not connected!';
	} else {
		ws.send('fetch');
	}
}

function shorten(name) {
	short=name.replace(/.*[.]/g, '')
	return '<span title="' + name + '">' + short + '</span>'
}


setInterval(fetch, 100);
setInterval(openSocket, 5000);

