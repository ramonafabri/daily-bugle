document.addEventListener("DOMContentLoaded", () => {

    const user = JSON.parse(localStorage.getItem("user"));

    if (!user) {
        alert("Please login first");
        window.location.href = "login.html";
        return;
    }

    fetch("/daily-bugle/api/users/" + user.id)
        .then(response => {
            if (!response.ok) {
                throw new Error("Profile not found");
            }
            return response.json();
        })
        .then(data => renderProfile(data))
        .catch(error => {
            console.error(error);
            alert("Error loading profile");
        });

});

function renderProfile(data) {

    document.getElementById("profileName").innerText = data.displayName;
    document.getElementById("profileEmail").innerText = data.email;
    document.getElementById("profileRole").innerText = data.role;

    renderRatings(data.ratings);
    renderComments(data.comments);
    renderArticles(data.writtenArticles);
}

function renderRatings(ratings) {

    const container = document.getElementById("ratingsSection");
    container.innerHTML = `<h2>My Ratings</h2>`;

    if (!ratings || ratings.length === 0) {
        container.innerHTML += "<p>No ratings yet.</p>";
        return;
    }

    ratings.forEach(r => {
        container.innerHTML += `
            <div class="profile-item">
                ${r.articleTitle} – ⭐ ${r.value}
            </div>
        `;
    });
}

function renderComments(comments) {

    const container = document.getElementById("commentsSection");
    container.innerHTML = `<div class="profile-block">
        <h2>My Comments</h2>
    </div>`;

    if (!comments || comments.length === 0) {
        container.innerHTML += "<p>No comments yet.</p>";
        return;
    }

    comments.forEach(c => {
        container.innerHTML += `
            <div class="profile-item">
                ${c.articleTitle} – ${c.commentCount} comment(s)
            </div>
        `;
    });
}

function renderArticles(articles) {

    if (!articles) return;

    const container = document.getElementById("articlesSection");

    container.innerHTML = `<div class="profile-block">
        <h2>My Articles</h2>
    </div>`;

    if (articles.length === 0) {
        container.innerHTML += "<p>No articles written yet.</p>";
        return;
    }

    articles.forEach(a => {
        container.innerHTML += `
            <div class="profile-item">
                <a href="article.html?id=${a.articleId}">
                    ${a.title}
                </a>
            </div>
        `;
    });
}