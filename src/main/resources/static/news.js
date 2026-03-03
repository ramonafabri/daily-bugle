document.addEventListener("DOMContentLoaded", () => {

    fetch("/daily-bugle/api/home")
        .then(response => response.json())
        .then(data => {

            renderSection(data.latest, "latest");
            renderSection(data.topRated, "topRated");
            renderSection(data.topRatedLast3Days, "trending");

        })
        .catch(error => {
            console.error("Error loading homepage:", error);
        });

});

function renderSection(articles, containerId) {

    const container = document.getElementById(containerId);
    container.innerHTML = "";

    if (!articles || articles.length === 0) {
        container.innerHTML = "<p style='color: gray;'>No articles yet.</p>";
        return;
    }

    articles.forEach(article => {
        const card = createArticleCard(article);
        container.appendChild(card);
    });
}

function createArticleCard(article) {

    const card = document.createElement("div");
    card.className = "article-card";

    card.innerHTML = `
    <div class="article-title">${article.title}</div>
    <div class="article-meta">
        By ${article.author}<br>
        <span class="rating">★ ${article.averageRating}</span>
        (${article.ratingCount} votes)
    </div>
`;

    card.addEventListener("click", () => {
        window.location.href = "article.html?id=" + article.id;
    });

    return card;
}