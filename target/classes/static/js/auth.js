// auth.js
function getToken() {
    return localStorage.getItem('fitdesk_token');
}

function logout() {
    localStorage.removeItem('fitdesk_token');
    window.location.href = 'index.html';
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;
    
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <i class="fa-solid fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideInRight 0.3s ease reverse forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

async function authFetch(url, options = {}) {
    const token = getToken();
    if (!options.headers) {
        options.headers = {};
    }
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }
    options.headers['Content-Type'] = 'application/json';

    const response = await fetch(url, options);
    if (response.status === 401) {
        logout();
        throw new Error('Unauthorized');
    }
    return response;
}

window.authFetch = authFetch;
window.logout = logout;
window.showToast = showToast;

document.addEventListener('DOMContentLoaded', () => {
    const isIndexPage = window.location.pathname.endsWith('index.html') || window.location.pathname === '/';
    const token = getToken();

    if (!isIndexPage && !token) {
        window.location.href = 'index.html';
    } else if (isIndexPage && token) {
        window.location.href = 'dashboard.html';
    }

    if (isIndexPage) {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const email = document.getElementById('loginEmail').value;
                const password = document.getElementById('loginPassword').value;
                const errorDiv = document.getElementById('loginError');
                const card = document.getElementById('loginCard');

                try {
                    const response = await fetch('/api/auth/login', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ email, password })
                    });
                    
                    if (response.ok) {
                        const data = await response.json();
                        localStorage.setItem('fitdesk_token', data.token);
                        window.location.href = 'dashboard.html';
                    } else {
                        errorDiv.style.display = 'block';
                        card.classList.remove('shake');
                        void card.offsetWidth; // trigger reflow
                        card.classList.add('shake');
                    }
                } catch (err) {
                    errorDiv.style.display = 'block';
                    errorDiv.textContent = 'Server error. Please try again later.';
                }
            });
        }
    }
});
