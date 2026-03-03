document.addEventListener("DOMContentLoaded", () => {

    const user = JSON.parse(localStorage.getItem("user"));

    if (!user || user.role !== "JOURNALIST") {
        alert("Access denied");
        window.location.href = "news.html";
        return;
    }

    document.getElementById("createForm")
        .addEventListener("submit", function(event) {

            event.preventDefault();

            const keywordsInput = document.getElementById("keywords").value;

            const data = {
                title: document.getElementById("title").value,
                synopsis: document.getElementById("synopsis").value,
                content: document.getElementById("content").value,
                publishAt: document.getElementById("publishAt").value || null,
                keywords: keywordsInput
                    ? keywordsInput.split(",").map(k => k.trim())
                    : []
            };

            fetch("/daily-bugle/api/articles", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-User-Id": user.id
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Failed to create article");
                    }
                    return response.text();
                })
                .then(() => {
                    window.location.href = "news.html";
                })
                .catch(error => {
                    document.getElementById("errorMessage").innerText = error.message;
                });

        });
});