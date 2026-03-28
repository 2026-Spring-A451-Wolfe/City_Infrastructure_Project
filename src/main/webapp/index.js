/* Author: Ellie Carroll
Purpose: Javascript functionality for the index page 
Last Modified: 3/9/2026 */

// handles clicks on the health check link, input real link when available
/*
document.getElementById('health-link').addEventListener('click', function(event) {
    event.preventDefault(); // prevent the default behavior (navigation)
    checkHealth();
});
*/

// handles clicks on the database check link, input real database link when available
/*
document.getElementById('db-link').addEventListener('click', function(event) {
    event.preventDefault(); // prevent the default behavior (navigation)
    checkDatabase();
});
*/

// function to simulate a health check
function checkHealth() {
    const statusMessage = document.getElementById('status-message');
    statusMessage.innerHTML = "Checking health...";

    // simulate a health check request
    setTimeout(function() {
        statusMessage.innerHTML = "Health check passed!";
    }, 2000); // simulate delay
}

// simulates a database check
function checkDatabase() {
    const statusMessage = document.getElementById('status-message');
    statusMessage.innerHTML = "Checking database...";

    // simulates a database check request
    setTimeout(function() {
        statusMessage.innerHTML = "Database check passed!";
    }, 2000); // simulate delay
}