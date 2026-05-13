var BASE = window.location.pathname.split('/').slice(0,2).join('/');

async function loginUser(email, password) {
    const res = await fetch(`${BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email, password })
    });
    return { ok: res.ok, status: res.status, data: await res.json() };
}

async function registerUser(name, email, password, phone, address) {
    const res = await fetch(`${BASE}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ name, email, password, phone, address })
    });
    return { ok: res.ok, status: res.status, data: await res.json() };
}

async function logoutUser() {
    await fetch(`${BASE}/api/auth/logout`, {
        method: 'POST',
        credentials: 'include'
    });
    window.location.href = `${BASE}/login`;
}

function showAlert(el, message, type = 'error') {
    el.textContent = message;
    el.className = `alert alert-${type} show`;
}

const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const alertEl  = document.getElementById('loginAlert');
        const submitBtn = loginForm.querySelector('[type="submit"]');
        const email    = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner"></span> Ingresando...';

        const { ok, data } = await loginUser(email, password);

        if (ok) {
            showAlert(alertEl, '¡Bienvenido! Redirigiendo...', 'success');
            setTimeout(() => {
                window.location.href = data.role === 'admin'
                    ? `${BASE}/admin`
                    : `${BASE}/tienda`;
            }, 600);
        } else {
            showAlert(alertEl, data.error || 'Error al iniciar sesión');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Iniciar Sesión';
        }
    });
}

const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const alertEl   = document.getElementById('registerAlert');
        const submitBtn = registerForm.querySelector('[type="submit"]');

        const name     = document.getElementById('regName').value.trim();
        const email    = document.getElementById('regEmail').value.trim();
        const password = document.getElementById('regPassword').value;
        const confirm  = document.getElementById('regConfirm').value;
        const phone    = document.getElementById('regPhone').value.trim();
        const address  = document.getElementById('regAddress').value.trim();

        if (password !== confirm) {
            showAlert(alertEl, 'Las contraseñas no coinciden');
            return;
        }
        if (password.length < 6) {
            showAlert(alertEl, 'La contraseña debe tener al menos 6 caracteres');
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner"></span> Registrando...';

        const { ok, data } = await registerUser(name, email, password, phone, address);

        if (ok) {
            showAlert(alertEl, '¡Cuenta creada! Redirigiendo...', 'success');
            setTimeout(() => { window.location.href = `${BASE}/tienda`; }, 800);
        } else {
            showAlert(alertEl, data.error || 'Error al registrar');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Crear Cuenta';
        }
    });
}

document.querySelectorAll('.logout-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
        e.preventDefault();
        logoutUser();
    });
});
