/* changes made by Adin 3/27
 - Changed the sidebar menu colors to match the rest of the site    
  (was still using the old cream and brown theme)                    
  - Fixed the map showing on top of the menu when you open it */

(function () {
    const menuButton = document.querySelector(".top-bar__menu");
    if (!menuButton) {
        return;
    }

    const defaultLinks = [
        { label: "Home", href: "index.html" },
        { label: "Dashboard", href: "dashboard-page.html" },
        { label: "Submit Report", href: "submission-page.html" },
        { label: "Track Reports", href: "report-tracking-page.html" },
        { label: "Updates", href: "updates-page.html" },
        { label: "Admin", href: "admin-view.html" },
        { label: "Register", href: "userRegistration.html" },
        { label: "Login", href: "login-page.html" }
    ];

    const links = Array.isArray(window.APP_NAV_LINKS) && window.APP_NAV_LINKS.length > 0
        ? window.APP_NAV_LINKS
        : defaultLinks;

    injectStyles();

    menuButton.setAttribute("role", "button");
    menuButton.setAttribute("tabindex", "0");
    menuButton.setAttribute("aria-label", "Open navigation menu");
    menuButton.setAttribute("aria-expanded", "false");

    const overlay = document.createElement("div");
    overlay.className = "app-nav-overlay";
    overlay.hidden = true;

    const drawer = document.createElement("aside");
    drawer.className = "app-nav-drawer";
    drawer.setAttribute("aria-hidden", "true");

    const drawerHeader = document.createElement("div");
    drawerHeader.className = "app-nav-header";

    const drawerTitle = document.createElement("h2");
    drawerTitle.className = "app-nav-title";
    drawerTitle.textContent = "Navigation";

    const closeButton = document.createElement("button");
    closeButton.type = "button";
    closeButton.className = "app-nav-close";
    closeButton.setAttribute("aria-label", "Close navigation menu");
    closeButton.textContent = "×";

    const nav = document.createElement("nav");
    nav.className = "app-nav-links";
    nav.setAttribute("aria-label", "Site pages");

    links.forEach(function (item) {
        const anchor = document.createElement("a");
        anchor.className = "app-nav-link";
        anchor.href = item.href;
        anchor.textContent = item.label;

        if (isCurrentPage(item.href)) {
            anchor.classList.add("active");
            anchor.setAttribute("aria-current", "page");
        }

        nav.appendChild(anchor);
    });

    drawerHeader.appendChild(drawerTitle);
    drawerHeader.appendChild(closeButton);
    drawer.appendChild(drawerHeader);
    drawer.appendChild(nav);

    document.body.appendChild(overlay);
    document.body.appendChild(drawer);

    let isOpen = false;

    function openMenu() {
        isOpen = true;
        overlay.hidden = false;
        drawer.classList.add("open");
        drawer.setAttribute("aria-hidden", "false");
        menuButton.setAttribute("aria-expanded", "true");
        document.body.classList.add("app-nav-open");
        closeButton.focus();
    }

    function closeMenu() {
        isOpen = false;
        overlay.hidden = true;
        drawer.classList.remove("open");
        drawer.setAttribute("aria-hidden", "true");
        menuButton.setAttribute("aria-expanded", "false");
        document.body.classList.remove("app-nav-open");
        menuButton.focus();
    }

    function toggleMenu() {
        if (isOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    menuButton.addEventListener("click", toggleMenu);
    menuButton.addEventListener("keydown", function (event) {
        if (event.key === "Enter" || event.key === " ") {
            event.preventDefault();
            toggleMenu();
        }
    });

    closeButton.addEventListener("click", closeMenu);
    overlay.addEventListener("click", closeMenu);

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && isOpen) {
            closeMenu();
        }
    });

    function isCurrentPage(href) {
        const current = window.location.pathname.split("/").pop() || "index.html";
        return current.toLowerCase() === String(href).toLowerCase();
    }

    function injectStyles() {
        if (document.getElementById("app-nav-styles")) {
            return;
        }

        const style = document.createElement("style");
        style.id = "app-nav-styles";
        style.textContent = `
            .top-bar__menu { outline: none; }
            .top-bar__menu:focus-visible {
                box-shadow: 0 0 0 3px rgba(255,255,255,0.55);
                border-radius: 6px;
            }

            #map, .leaflet-container {
                z-index: 1 !important;
            }

            .app-nav-overlay {
                position: fixed;
                inset: 0;
                background: rgba(0,0,0,0.35);
                z-index: 1000;
            }

            .app-nav-drawer {
                position: fixed;
                top: 0;
                left: 0;
                width: min(320px, 84vw);
                height: 100vh;
                background: #f0f4f8;
                border-right: 3px solid #4a9ebb;
                box-shadow: 8px 0 24px rgba(0,0,0,0.2);
                transform: translateX(-105%);
                transition: transform 0.25s ease;
                z-index: 1001;
                display: flex;
                flex-direction: column;
            }

            .app-nav-drawer.open {
                transform: translateX(0);
            }

            .app-nav-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 14px 16px;
                border-bottom: 3px solid #4a9ebb;
                background: #1e3a5f;
            }

            .app-nav-title {
                margin: 0;
                font-size: 1.1rem;
                color: #ffffff;
                font-weight: 600;
            }

            .app-nav-close {
                border: 2px solid #4a9ebb;
                background: #2d6a9f;
                color: #ffffff;
                border-radius: 8px;
                font-size: 1.2rem;
                line-height: 1;
                width: 34px;
                height: 34px;
                cursor: pointer;
            }

            .app-nav-links {
                display: flex;
                flex-direction: column;
                gap: 8px;
                padding: 14px;
                overflow-y: auto;
            }

            .app-nav-link {
                text-decoration: none;
                color: #1a2b3c;
                border: 1.5px solid #dce4ec;
                border-radius: 8px;
                background: #ffffff;
                padding: 10px 12px;
                font-weight: 600;
                transition: transform 0.2s ease, background-color 0.2s ease;
            }

            .app-nav-link:hover {
                background: #e8f1f8;
                transform: translateY(-3px);
            }

            .app-nav-link.active {
                background: #2d6a9f;
                color: #ffffff;
                border-color: #4a9ebb;
            }

            body.app-nav-open {
                overflow: hidden;
            }
        `;

        document.head.appendChild(style);
    }
})();
