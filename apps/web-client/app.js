
var express = require('express');
var PORT = 9001;

var app = express();
app.use(express.static('public'));
app.all('*', function (req, res) {
	if (req.path == '/favicon.ico') {
		res.end();
		return;
	}

});
app.listen(PORT, function () {
	console.log('server is running at %d', PORT);
});
