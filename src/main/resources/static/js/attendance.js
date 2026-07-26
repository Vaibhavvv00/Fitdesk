// attendance.js
let activeMembers = [];

document.addEventListener('DOMContentLoaded', async () => {
    await loadActiveMembers();
    await loadTodayAttendance();
});

async function loadActiveMembers() {
    try {
        const res = await authFetch('/api/members');
        if (!res.ok) return;
        const members = await res.json();
        activeMembers = members.filter(m => (m.status || '').toUpperCase() === 'ACTIVE');
        const select = document.getElementById('attendanceMemberSelect');
        select.innerHTML = '<option value="">Choose active member...</option>';
        activeMembers.forEach(m => {
            select.innerHTML += `<option value="${m.id}">${m.fullName}</option>`;
        });
    } catch (e) {
        showToast('Failed to load members', 'error');
    }
}

async function loadTodayAttendance() {
    try {
        const res = await authFetch('/api/attendance/today');
        if (res.ok) {
            const records = await res.json();
            renderAttendance(records);
        }
    } catch (e) {
        showToast('Failed to load attendance', 'error');
    }
}

function renderAttendance(records) {
    const tbody = document.querySelector('#attendanceTable tbody');
    tbody.innerHTML = '';

    if (!records.length) {
        tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:var(--text-muted);">No check-ins today yet.</td></tr>';
        return;
    }

    records.forEach(a => {
        const name = a.member ? a.member.fullName : 'Unknown';
        const checkIn = formatDateTime(a.checkIn);
        const checkOut = a.checkOut ? formatDateTime(a.checkOut) : '—';
        const inGym = !a.checkOut;
        const statusClass = inGym ? 'success' : 'warning';
        const statusLabel = inGym ? 'In gym' : 'Checked out';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${name}</td>
            <td>${checkIn}</td>
            <td>${checkOut}</td>
            <td><span class="badge ${statusClass}">${statusLabel}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

function formatDateTime(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    return d.toLocaleString('en-IN', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

function getSelectedMemberId() {
    const id = document.getElementById('attendanceMemberSelect').value;
    if (!id) {
        showToast('Please select a member', 'error');
        return null;
    }
    return parseInt(id, 10);
}

async function checkInMember() {
    const memberId = getSelectedMemberId();
    if (memberId == null) return;

    try {
        const res = await authFetch('/api/attendance/checkin', {
            method: 'POST',
            body: JSON.stringify({ memberId })
        });
        const data = await res.json().catch(() => ({}));
        if (res.ok) {
            showToast('Check-in recorded');
            loadTodayAttendance();
        } else {
            showToast(data.error || 'Check-in failed', 'error');
        }
    } catch (e) {
        showToast('Check-in failed', 'error');
    }
}

async function checkOutMember() {
    const memberId = getSelectedMemberId();
    if (memberId == null) return;

    try {
        const res = await authFetch('/api/attendance/checkout', {
            method: 'POST',
            body: JSON.stringify({ memberId })
        });
        const data = await res.json().catch(() => ({}));
        if (res.ok) {
            showToast('Check-out recorded');
            loadTodayAttendance();
        } else {
            showToast(data.error || 'Check-out failed', 'error');
        }
    } catch (e) {
        showToast('Check-out failed', 'error');
    }
}

window.checkInMember = checkInMember;
window.checkOutMember = checkOutMember;
window.loadTodayAttendance = loadTodayAttendance;
