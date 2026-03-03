document.addEventListener("DOMContentLoaded", () => {

    const params = new URLSearchParams(window.location.search);
    const articleId = params.get("id");

    if (!articleId) {
        alert("Article not found");
        return;
    }

    const user = JSON.parse(localStorage.getItem("user"));

    // Komment form csak ha be van jelentkezve
    if (user) {
        renderCommentForm(articleId, user);
    }

    fetch("/daily-bugle/api/articles/" + articleId)
        .then(response => {
            if (!response.ok) {
                throw new Error("Article not found");
            }
            return response.json();
        })
        .then(article => {
            renderArticle(article);

            // Rating widget csak ha user van
            if (user) {
                initRating(articleId);
            }
        })
        .catch(error => {
            console.error(error);
            alert("Error loading article");
        });

});

function renderCommentForm(articleId, user) {

    const container = document.getElementById("commentFormContainer");

    container.innerHTML = `
        <h3>Add Comment</h3>
        <textarea id="commentInput" rows="4" placeholder="Write your comment..."></textarea>
        <button id="submitComment" class="auth-btn">Post Comment</button>
        <p id="commentError" class="error-message"></p>
    `;

    document.getElementById("submitComment")
        .addEventListener("click", () => {

            const content = document.getElementById("commentInput").value;

            if (!content.trim()) {
                document.getElementById("commentError").innerText = "Comment cannot be empty";
                return;
            }

            fetch("/daily-bugle/api/comments", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": user.id
                },
                body: JSON.stringify({
                    articleId: parseInt(articleId),
                    content: content
                })
            })
                .then(response => {
                    if (response.status !== 201) {
                        throw new Error("Failed to post comment");
                    }
                })
                .then(() => {
                    location.reload();
                })
                .catch(error => {
                    document.getElementById("commentError").innerText = error.message;
                });

        });
}


function initRating(articleId) {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user) return;

    const container = document.getElementById("ratingSection");

    container.innerHTML = `
        <h3>Rate this article</h3>
        <div id="starsContainer" class="rating-section"></div>
        <p id="ratingError" class="error-message"></p>
    `;

    const starsContainer = document.getElementById("starsContainer");

    for (let i = 1; i <= 5; i++) {

        const star = document.createElement("span");
        star.innerText = "★";
        star.classList.add("star");
        star.dataset.value = i;

        star.addEventListener("click", () => {
            submitRating(articleId, user.id, i);
        });

        starsContainer.appendChild(star);
    }
}

function submitRating(articleId, userId, value) {

    fetch("/daily-bugle/api/ratings", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-User-Id": userId
        },
        body: JSON.stringify({
            value: value,
            articleId: parseInt(articleId)
        })
    })
        .then(response => {
            if (response.status === 201) {
                location.reload();
            } else {
                throw new Error("You have already rated this article.");
            }
        })
        .catch(error => {
            document.getElementById("ratingError").innerText = error.message;
        });


}