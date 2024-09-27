const { exec } = require('child_process');
const WebSocket = require('ws');
const schedule = require('node-schedule');

const wsUrl = 'ws://localhost:7613?role=grabber';

const command = "java -cp '/usr/lib/jvm/java-17-oracle/lib/PIJDBCDriver.jar' jdbc.java";

const ws = new WebSocket(wsUrl);

const task = () => {
	console.log('Grabbing data from JDBC...');
	exec(command, (err, out, stderr) => {
		if (err) {
			console.error(`Error executing command: ${err}`);
		}

		const lines = out.split('\n');

		const data = {};
		const pattern = /(-?\w+)\s+(-?[\d.]+)/;

		for (let line of lines) {
			const match = line.trim().match(pattern);
			if (match) {
				const variable = match[1];
				const value = parseFloat(match[2]);
				data[variable] = value;
			}
		}

		if (data["Driver"]) delete data["Driver"];

		console.log(`[${new Date().toLocaleTimeString()}] Data grabbed! =>`, JSON.stringify(data, null, 2));

		if (ws && ws.readyState === WebSocket.OPEN) {
			ws.send(JSON.stringify(data));
			console.log(`[${new Date().toLocaleTimeString()}] Pushed a payload to websocket!`);
		} else {
			console.error("WebSocket not connected! data not sent");
		}
	});
};

ws.on('open', () => {
	console.log('Connected to websocket server!');
	
	const job = schedule.scheduleJob('*/1 * * * *', task);
	
	if (job) console.log("Job started!");
	task();
});

