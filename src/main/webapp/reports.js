// Since this script is loaded at the end of the body, we can just call it immediately!
fetchRealReports();

async function fetchRealReports() {
    const listContainer = document.querySelector('.content') || document.body;

    try {
        const token = localStorage.getItem('jwt');

        const response = await fetch('/reports', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            
            // Wipe out placeholder report cards but keep the title and divider
            const title = document.querySelector('.content__title');
            const divider = document.querySelector('.content__divider');
            listContainer.innerHTML = '';
            if (title) listContainer.appendChild(title);
            if (divider) listContainer.appendChild(divider);

            if (data.length === 0) {
                const emptyMsg = document.createElement('p');
                emptyMsg.innerText = "No reports found in the database.";
                listContainer.appendChild(emptyMsg);
                return;
            }

            // Loop through the real database rows and render them
            data.forEach(report => {
                const div = document.createElement('div');
                div.className = 'report-card';
                div.innerHTML = `
                    <div class="report-card__image">
                        <!-- We can put an image here if needed, for now just a placeholder -->
                        ${report.imageUrl ? `<img src="${report.imageUrl}" width="100" />` : 'No Image'}
                    </div>

                    <div class="report-card__info">
                        <p><strong>${report.title || 'Untitled Report'}</strong></p>
                        <p>${report.description || 'No description provided.'}</p>
                        
                        <p><strong>Category:</strong> ${report.category || 'N/A'} | <strong>Severity:</strong> ${report.severity || 'N/A'}</p>

                        <div class="progress">
                            <div class="progress__labels">
                                <span style="color: black;">Reported: ${(function(d){if(!d)return new Date().toLocaleDateString();if(Array.isArray(d))return new Date(d[0],d[1]-1,d[2]).toLocaleDateString();return new Date(d).toLocaleDateString()})(report.createdAt)}</span>
                                <span style="font-weight: bold; color: #d32f2f;">Status: ${report.status || 'Open'}</span>
                            </div>
                        </div>

                        <button class="report-card__btn" onclick="alert('Viewing report ID: ${report.id}')">View Report</button>
                    </div>
                `;
                listContainer.appendChild(div);
            });

        } else {
            const errDiv = document.createElement('p');
            errDiv.style.color = 'red';
            errDiv.innerText = 'Failed to load reports. Are you logged in?';
            listContainer.appendChild(errDiv);
        }
    } catch (err) {
        console.error('Error fetching reports:', err);
    }
}