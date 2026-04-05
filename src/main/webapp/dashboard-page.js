document.addEventListener("DOMContentLoaded", initDashboard);

async function initDashboard() {
    const reportsContainer = document.querySelectorAll('.dashboard-card__body')[0];
    const updatesContainer = document.querySelectorAll('.dashboard-card__body')[1];

    if (!reportsContainer || !updatesContainer) return;

    const token = localStorage.getItem('jwt');
    if (!token) {
        reportsContainer.innerHTML = "<p style='color:red'>Please log in to view.</p>";
        updatesContainer.innerHTML = "<p style='color:red'>Please log in to view.</p>";
        return;
    }

    try {
        const response = await fetch('/reports', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            
            // Render Reports
            reportsContainer.innerHTML = '';
            if (data.length === 0) {
                reportsContainer.innerHTML = "<p>No recent reports.</p>";
            } else {
                data.slice(0, 5).forEach(report => { // show only top 5
                    const div = document.createElement('div');
                    div.style.borderBottom = "1px solid #eee";
                    div.style.padding = "10px 0";
                    
                    let dateStr = "Unknown Date";
                    if (report.createdAt && Array.isArray(report.createdAt)) {
                        const d = report.createdAt;
                        dateStr = new Date(d[0], d[1] - 1, d[2], d[3] || 0, d[4] || 0).toLocaleString();
                    }

                    div.innerHTML = `
                        <strong>${report.title || 'Untitled'}</strong><br/>
                        <small>Status: ${report.status} | Severity: ${report.severity}</small><br/>
                        <small style="color:#666;">Date: ${dateStr}</small>
                    `;
                    reportsContainer.appendChild(div);
                });
            }

            // Render Updates (re-using reports for now, like updates-page)
            updatesContainer.innerHTML = '';
            if (data.length === 0) {
                updatesContainer.innerHTML = "<p>No recent updates.</p>";
            } else {
                data.slice(0, 5).forEach(report => {
                    const div = document.createElement('div');
                    div.style.borderBottom = "1px solid #eee";
                    div.style.padding = "10px 0";

                    let updatedDateStr = "Unknown Date";
                    if (report.updatedAt && Array.isArray(report.updatedAt)) {
                        const d = report.updatedAt;
                        updatedDateStr = new Date(d[0], d[1] - 1, d[2], d[3] || 0, d[4] || 0).toLocaleString();
                    } else if (report.createdAt && Array.isArray(report.createdAt)) {
                        const d = report.createdAt;
                        updatedDateStr = new Date(d[0], d[1] - 1, d[2], d[3] || 0, d[4] || 0).toLocaleString();
                    }

                    div.innerHTML = `
                        <strong>Update on: ${report.title || 'Untitled'}</strong><br/>
                        <small>Current Status: ${report.status}</small><br/>
                        <small style="color:#666;">Updated: ${updatedDateStr}</small>
                    `;
                    updatesContainer.appendChild(div);
                });
            }

        } else {
            reportsContainer.innerHTML = "<p style='color:red'>Error loading reports.</p>";
            updatesContainer.innerHTML = "<p style='color:red'>Error loading updates.</p>";
        }
    } catch (err) {
        console.error("Dashboard error:", err);
        reportsContainer.innerHTML = "<p style='color:red'>Failed to load data.</p>";
        updatesContainer.innerHTML = "<p style='color:red'>Failed to load data.</p>";
    }
}