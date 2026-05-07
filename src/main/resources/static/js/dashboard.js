// ===============================
// CHART VARIABLES
// ===============================

let usersChart;
let conversionChart;
let confidenceChart;

// ===============================
// FETCH LIVE ANALYTICS
// ===============================

async function fetchAnalytics() {

    const flagKey =
        window.location.pathname.split("/").pop();

    const response =
        await fetch(`/api/analytics/${flagKey}`);

    const data =
        await response.json();

    updateDashboard(data);

    updateCharts(data);
}

// ===============================
// UPDATE DASHBOARD
// ===============================

function updateDashboard(data) {

    // WINNER
    document.getElementById("winnerText")
        .innerText = data.winner;

    // SIGNIFICANCE
    document.getElementById("significanceText")
        .innerText =
            data.statisticallySignificant;

    // SUMMARY
    document.getElementById("totalUsers")
        .innerText =
            data.summary.totalUsers;

    document.getElementById("totalConversions")
        .innerText =
            data.summary.totalConversions;

    document.getElementById("winningRate")
        .innerText =
            data.summary.winningConversionRate;

    // VARIANTS
    Object.keys(data).forEach(key => {

        if (
            key === "summary" ||
            key === "winner" ||
            key === "statisticallySignificant"
        ) {
            return;
        }

        const variant = data[key];

        document.getElementById(`users-${key}`)
            .innerText = variant.users;

        document.getElementById(`conversions-${key}`)
            .innerText = variant.conversions;

        document.getElementById(`rate-${key}`)
            .innerText = variant.conversionRate;

        document.getElementById(`confidence-${key}`)
            .innerText = variant.confidence;
    });
}

// ===============================
// UPDATE CHARTS
// ===============================

function updateCharts(data) {

    const labels = [];

    const users = [];

    const rates = [];

    const confidence = [];

    Object.keys(data).forEach(key => {

        if (
            key === "summary" ||
            key === "winner" ||
            key === "statisticallySignificant"
        ) {
            return;
        }

        labels.push(key);

        users.push(data[key].users);

        rates.push(data[key].conversionRate);

        confidence.push(data[key].confidence);
    });

    // USERS CHART
    usersChart.data.labels = labels;

    usersChart.data.datasets[0].data = users;

    usersChart.update();

    // CONVERSION CHART
    conversionChart.data.labels = labels;

    conversionChart.data.datasets[0].data = rates;

    conversionChart.update();

    // CONFIDENCE CHART
    confidenceChart.data.labels = labels;

    confidenceChart.data.datasets[0].data = confidence;

    confidenceChart.update();
}

// ===============================
// INITIALIZE CHARTS
// ===============================

function initializeCharts() {

    // USERS CHART
    usersChart = new Chart(
        document.getElementById("usersChart"),
        {
            type: "bar",

            data: {
                labels: [],

                datasets: [{
                    label: "Users",

                    data: [],

                    backgroundColor: [
                        "#38bdf8",
                        "#22c55e",
                        "#facc15"
                    ]
                }]
            },

            options: {
                responsive: true
            }
        }
    );

    // CONVERSION CHART
    conversionChart = new Chart(
        document.getElementById("conversionChart"),
        {
            type: "line",

            data: {
                labels: [],

                datasets: [{
                    label: "Conversion Rate %",

                    data: [],

                    borderColor: "#38bdf8",

                    backgroundColor: "#38bdf8",

                    tension: 0.4
                }]
            },

            options: {
                responsive: true
            }
        }
    );

    // CONFIDENCE CHART
    confidenceChart = new Chart(
        document.getElementById("confidenceChart"),
        {
            type: "doughnut",

            data: {
                labels: [],

                datasets: [{
                    label: "Confidence",

                    data: [],

                    backgroundColor: [
                        "#22c55e",
                        "#facc15",
                        "#f43f5e"
                    ]
                }]
            },

            options: {
                responsive: true
            }
        }
    );
}

// ===============================
// START
// ===============================

initializeCharts();

fetchAnalytics();

// LIVE POLLING
setInterval(fetchAnalytics, 3000);