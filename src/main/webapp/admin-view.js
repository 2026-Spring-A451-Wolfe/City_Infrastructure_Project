(function () {
    const API_BASE = "";

    const state = {
        reports: [],
        selectedReportId: null,
        originalForm: null
    };

    const dom = {
        queueSection: document.querySelector(".admin-reports-list"),
        queueEmpty: document.querySelector(".admin-reports-list__empty"),
        submitter: document.querySelector(".admin-view-submitter"),
        picture: document.querySelector(".admin-view-picture"),
        map: document.querySelector(".admin-view-map"),
        descriptionText: document.querySelector(".admin-view-description__text"),
        statusSelect: document.getElementById("status-select"),
        departmentSelect: document.getElementById("department-select"),
        budgetInput: document.getElementById("budget"),
        saveButton: document.querySelector(".admin-report-update")
    };

    const queueItemsContainer = document.createElement("div");
    queueItemsContainer.className = "admin-reports-list__items";
    dom.queueSection.appendChild(queueItemsContainer);

    const pageMessage = document.createElement("p");
    pageMessage.className = "admin-panel-subtext";
    pageMessage.setAttribute("role", "status");
    pageMessage.style.marginBottom = "0";
    pageMessage.style.display = "none";
    dom.saveButton.insertAdjacentElement("beforebegin", pageMessage);

    dom.saveButton.disabled = true;

    init();

    async function init() {
        bindEvents();
        await Promise.all([loadDepartments(), loadReports()]);
    }

    function bindEvents() {
        dom.statusSelect.addEventListener("change", onFormChanged);
        dom.departmentSelect.addEventListener("change", onFormChanged);
        dom.budgetInput.addEventListener("input", onFormChanged);

        dom.saveButton.addEventListener("click", async function () {
            await saveCurrentReport();
        });
    }

    async function loadDepartments() {
        try {
            const departments = await apiGet(`/departments/`);
            const normalized = Array.isArray(departments)
                ? departments
                : Array.isArray(departments?.items)
                    ? departments.items
                    : [];

            dom.departmentSelect.innerHTML = "<option value=\"\">Select department</option>";
            normalized.forEach(function (department) {
                const option = document.createElement("option");
                option.value = String(getFirst(department, ["id", "departmentId", "name"], ""));
                option.textContent = String(getFirst(department, ["name", "departmentName", "id"], "Unknown Department"));
                dom.departmentSelect.appendChild(option);
            });
        } catch (error) {
            showMessage(getErrorMessage(error, "Could not load departments."), true);
        }
    }

    async function loadReports() {
        setQueueLoading(true);
        try {
            const response = await apiGet(`/reports`);
            state.reports = normalizeReportList(response);
            renderQueue();

            if (state.reports.length > 0) {
                await selectReport(getReportId(state.reports[0]));
            } else {
                clearReportDetail();
            }
        } catch (error) {
            setQueueStatus("Failed to load reports. Please check server connection.", true);
            clearReportDetail();
        }
    }

    function normalizeReportList(response) {
        if (Array.isArray(response)) return response;
        if (Array.isArray(response?.items)) return response.items;
        if (Array.isArray(response?.reports)) return response.reports;
        return [];
    }

    function renderQueue() {
        queueItemsContainer.innerHTML = "";

        if (!state.reports.length) {
            setQueueStatus("No reports found.", false);
            return;
        }

        dom.queueEmpty.style.display = "none";

        state.reports.forEach(function (report) {
            const reportId = getReportId(report);
            const item = document.createElement("button");
            item.type = "button";
            item.className = "admin-reports-item";
            item.textContent = getReportTitle(report);

            if (String(reportId) === String(state.selectedReportId)) {
                item.classList.add("selected");
            }

            item.addEventListener("click", async function () {
                await selectReport(reportId);
            });

            queueItemsContainer.appendChild(item);
        });
    }

    async function selectReport(reportId) {
        if (!reportId) {
            showMessage("Invalid report selected.", true);
            return;
        }

        showMessage("Loading report...", false);

        try {
            const report = state.reports.find(r => String(getReportId(r)) === String(reportId));
            if (!report) throw new Error("Report not found in loaded list.");

            state.selectedReportId = getReportId(report) || reportId;
            applyReportDetail(report);
            renderQueue();
            showMessage("", false);
        } catch (error) {
            showMessage(getErrorMessage(error, "Could not load selected report."), true);
        }
    }

    function applyReportDetail(report) {
        const submitterName = String(getFirst(report, ["submitterName", "submittedBy", "username", "reporterName"], "Unknown"));
        const title = getReportTitle(report);
        const description = String(getFirst(report, ["description", "problemDescription", "details"], "No description available."));
        const statusValue = String(getFirst(report, ["status"], "submitted")).toLowerCase();
        const departmentValue = String(getFirst(report, ["departmentId", "department"], ""));
        const budgetValue = getFirst(report, ["budget"], "");
        const lat = getFirst(report, ["latitude", "lat"], null);
        const lng = getFirst(report, ["longitude", "lng"], null);
        const imageUrl = getFirst(report, ["imageUrl", "photoUrl"], "");

        dom.submitter.textContent = getInitials(submitterName);
        dom.submitter.title = submitterName;
        dom.descriptionText.textContent = description;

        dom.map.textContent = (lat !== null && lng !== null)
            ? `${Number(lat).toFixed(5)}, ${Number(lng).toFixed(5)}`
            : "Map location unavailable";

        if (imageUrl) {
            dom.picture.innerHTML = "";
            const image = document.createElement("img");
            image.src = imageUrl;
            image.style.width = "100%";
            image.style.height = "100%";
            image.style.objectFit = "cover";
            dom.picture.appendChild(image);
        } else {
            dom.picture.innerHTML = "No image attached";
        }

        dom.statusSelect.value = statusValue;
        dom.departmentSelect.value = departmentValue || "";
        dom.budgetInput.value = budgetValue ?? "";

        state.originalForm = getFormSnapshot();
        setSaveButtonState();
    }

    async function saveCurrentReport() {
        if (!state.selectedReportId) {
            showMessage("Select a report first.", true);
            return;
        }

        const payload = {
            status: dom.statusSelect.value,
            departmentId: dom.departmentSelect.value || null,
            budget: dom.budgetInput.value === "" ? null : Number(dom.budgetInput.value)
        };

        if (!Number.isFinite(payload.budget) && payload.budget !== null) {
            showMessage("Budget must be a valid number.", true);
            return;
        }

        if (payload.budget !== null && payload.budget < 0) {
            showMessage("Budget cannot be negative.", true);
            return;
        }

        dom.saveButton.disabled = true;
        dom.saveButton.textContent = "Saving...";
        showMessage("Saving updates...", false);

        try {
            const updated = await apiPatch(`/reports/${encodeURIComponent(state.selectedReportId)}`, payload);
            applyReportDetail(updated);
            showMessage("Report updated successfully.", false);
        } catch (error) {
            showMessage(getErrorMessage(error, "Could not save report updates."), true);
            setSaveButtonState();
        }

        dom.saveButton.textContent = "Save Updates";
    }

    function onFormChanged() {
        setSaveButtonState();
    }

    function setSaveButtonState() {
        if (!state.selectedReportId || !state.originalForm) {
            dom.saveButton.disabled = true;
            return;
        }

        const current = getFormSnapshot();
        const changed = current.status !== state.originalForm.status
            || current.departmentId !== state.originalForm.departmentId
            || current.budget !== state.originalForm.budget;

        dom.saveButton.disabled = !changed;
    }

    function getFormSnapshot() {
        return {
            status: dom.statusSelect.value,
            departmentId: dom.departmentSelect.value,
            budget: dom.budgetInput.value.trim()
        };
    }

    function clearReportDetail() {
        state.selectedReportId = null;
        state.originalForm = null;
        dom.submitter.textContent = "--";
        dom.picture.innerHTML = "No image attached";
        dom.map.textContent = "Map location preview";
        dom.descriptionText.textContent = "Select a report to view details.";
        dom.statusSelect.value = "submitted";
        dom.departmentSelect.value = "";
        dom.budgetInput.value = "";
        dom.saveButton.disabled = true;
        renderQueue();
    }

    function setQueueLoading() {
        dom.queueEmpty.style.display = "flex";
        dom.queueEmpty.textContent = "Loading reports...";
    }

    function setQueueStatus(message, isError) {
        dom.queueEmpty.textContent = message;
        dom.queueEmpty.style.display = "flex";
        dom.queueEmpty.style.color = isError ? "#7a1f1f" : "#385064"; // conflict 1: kept servlet-refactor
    }

    function showMessage(message, isError) {
        if (!message) {
            pageMessage.style.display = "none";
            return;
        }

        pageMessage.textContent = message;
        pageMessage.style.display = "block";
        pageMessage.style.color = isError ? "#7a1f1f" : "#385064"; // conflict 2: kept servlet-refactor
    }

    function getReportId(report) {
        return getFirst(report, ["id", "reportId"], "");
    }

    function getReportTitle(report) {
        return getFirst(report, ["title", "name"], "Untitled Report");
    }

    function getFirst(source, keys, fallback) {
        for (const key of keys) {
            if (source && source[key] !== undefined) return source[key];
        }
        return fallback;
    }

    function getInitials(name) {
        return name ? name.split(" ").map(n => n[0]).join("").toUpperCase() : "--";
    }

    async function apiGet(url) {
        // conflict 3: kept servlet-refactor (adds JWT auth header)
        const token = localStorage.getItem('jwt') || '';
        const response = await fetch(url, {
            method: "GET",
            credentials: "omit",
            headers: {
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        return handleJsonResponse(response);
    }

    async function apiPatch(url, payload) {
        // conflict 4: kept servlet-refactor (adds JWT auth header)
        const token = localStorage.getItem('jwt') || '';
        const response = await fetch(url, {
            method: "PATCH",
            credentials: "omit",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });
        return handleJsonResponse(response);
    }

    function handleJsonResponse(response) {
        if (!response.ok) throw new Error("Request failed");
        return response.json();
    }

})();