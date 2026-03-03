document.addEventListener("DOMContentLoaded", () => {
    buildNavbar();
});

function buildNavbar() {

    const navbar = document.getElementById("navbar");
    if (!navbar) return;

    const user = JSON.parse(localStorage.getItem("user"));

    navbar.innerHTML = "";

    // Always visible
    navbar.appendChild(createLink("Home", "index.html"));
    navbar.appendChild(createLink("News", "news.html"));

    if (!user) {
        navbar.appendChild(createLink("Login", "login.html"));
        navbar.appendChild(createLink("Register", "register.html"));
        return;
    }

    // ROLE BASED
    if (user.role === "JOURNALIST") {
        navbar.appendChild(createLink("Create Article", "create.html"));
    }

    if (user.role === "ADMIN") {
        navbar.appendChild(createLink("Admin Panel", "admin.html"));
    }

    navbar.appendChild(createLink("Profile", "profile.html"));
    navbar.appendChild(createLogoutButton());
}

function createLink(text, href) {
    const link = document.createElement("a");
    link.href = href;
    link.innerText = text;
    return link;
}

function createLogoutButton() {
    const btn = document.createElement("a");
    btn.href = "#";
    btn.innerText = "Logout";

    btn.addEventListener("click", (e) => {
        e.preventDefault();
        localStorage.removeItem("user");
        window.location.href = "index.html";
    });

    return btn;
}