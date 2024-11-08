const { exec } = require("child_process");
const WebSocket = require("ws");
const schedule = require("node-schedule");

const wsUrl = "ws://localhost:7613?role=grabber";

const command =
	"java -cp '/usr/lib/jvm/java-17-oracle/lib/PIJDBCDriver.jar' jdbc.java";

const ws = new WebSocket(wsUrl);

const getMalaysiaTime = () =>
	new Date().toLocaleString("en-US", { timeZone: "Asia/Kuala_Lumpur" });

const task = () => {
	console.log(`[${getMalaysiaTime()}] Grabbing data from JDBC...`);
	exec(command, (err, out, stderr) => {
		if (err) {
			console.error(
				`[${getMalaysiaTime()}] Error executing command: ${err}`
			);
		}

		const lines = out ? out.split("\n") : [];

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

		const bytes = JSON.stringify(data).length;

		console.log(
			`[${getMalaysiaTime()}] Data event:`,
			bytes > 0 ? `${bytes} bytes` : "Not found"
		);

		if (ws && ws.readyState === WebSocket.OPEN) {
			ws.send(JSON.stringify(data));
			console.log(`[${getMalaysiaTime()}] Pushed data`);
		} else {
			console.error(`[${getMalaysiaTime()}] WebSocket not connected`);
		}
	});
};

// console log the purpose of this script
// starts with a title and introduction
console.log("JDBC Grabber");
console.log(
	"This script grabs data from a JDBC connection and sends it to a WebSocket server."
);
console.log("The data is grabbed every minute.");
ws.on("open", () => {
	console.log(`[${getMalaysiaTime()}] Connected to websocket server!`);

	// Schedule the task to run every minute
	const job = schedule.scheduleJob("*/1 * * * *", task);

	if (job) console.log(`[${getMalaysiaTime()}] Job started!`);
	task();
});

ws.on("error", (err) => {
	console.error(`[${getMalaysiaTime()}] WebSocket error: ${err}`);
});
