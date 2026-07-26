// payments.js
let allPayments = [];
let editingPaymentId = null;

document.addEventListener('DOMContentLoaded', async () => {
    await fetchMembersForDropdown();
    await fetchPayments();

    document.getElementById('paymentForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await savePayment();
    });

    ['filterMemberId', 'filterFromDate', 'filterToDate'].forEach(id => {
        document.getElementById(id).addEventListener('change', applyFilters);
    });
});

async function fetchMembersForDropdown() {
    try {
        const res = await authFetch('/api/members');
        if (res.ok) {
            const members = await res.json();
            const selects = [
                document.getElementById('memberId'),
                document.getElementById('filterMemberId')
            ];
            selects.forEach(select => {
                const isFilter = select.id === 'filterMemberId';
                select.innerHTML = isFilter
                    ? '<option value="">All members</option>'
                    : '<option value="">Select Member</option>';
                members.forEach(m => {
                    select.innerHTML += `<option value="${m.id}">${m.fullName}</option>`;
                });
            });
        }
    } catch (e) {
        console.error('Failed to load members for payments');
    }
}

function buildPaymentsUrl() {
    const params = new URLSearchParams();
    const memberId = document.getElementById('filterMemberId').value;
    const fromDate = document.getElementById('filterFromDate').value;
    const toDate = document.getElementById('filterToDate').value;
    if (memberId) params.append('memberId', memberId);
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    const qs = params.toString();
    return qs ? `/api/payments?${qs}` : '/api/payments';
}

async function fetchPayments() {
    try {
        const res = await authFetch(buildPaymentsUrl());
        if (res.ok) {
            allPayments = await res.json();
            renderPayments(allPayments);
        }
    } catch (e) {
        showToast('Failed to load payments', 'error');
    }
}

function applyFilters() {
    fetchPayments();
}

function clearFilters() {
    document.getElementById('filterMemberId').value = '';
    document.getElementById('filterFromDate').value = '';
    document.getElementById('filterToDate').value = '';
    fetchPayments();
}

async function exportPaymentsCsv() {
    const token = getToken();
    if (!token) return;

    const params = new URLSearchParams();
    const memberId = document.getElementById('filterMemberId').value;
    const fromDate = document.getElementById('filterFromDate').value;
    const toDate = document.getElementById('filterToDate').value;
    if (memberId) params.append('memberId', memberId);
    if (fromDate) params.append('fromDate', fromDate);
    if (toDate) params.append('toDate', toDate);
    const qs = params.toString();
    const url = qs ? `/api/payments/export?${qs}` : '/api/payments/export';

    try {
        const res = await fetch(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            showToast('Export failed', 'error');
            return;
        }
        const blob = await res.blob();
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = `payments-${new Date().toISOString().slice(0, 10)}.csv`;
        link.click();
        URL.revokeObjectURL(link.href);
        showToast('CSV exported');
    } catch (e) {
        showToast('Export failed', 'error');
    }
}

function renderPayments(payments) {
    const tbody = document.querySelector('#paymentsTable tbody');
    tbody.innerHTML = '';

    if (!payments.length) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--text-muted);">No payments match your filters.</td></tr>';
        return;
    }

    payments.forEach(p => {
        const methodClass = p.paymentMethod ? p.paymentMethod.toLowerCase() : '';
        const statusUpper = (p.status || '').toUpperCase();
        const statusClass = statusUpper === 'COMPLETED' ? 'success' : (statusUpper === 'FAILED' ? 'danger' : 'warning');

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${p.member ? p.member.fullName : 'Unknown'}</td>
            <td>₹${Number(p.amount).toLocaleString()}</td>
            <td>${p.paymentDate}</td>
            <td><span class="badge ${methodClass}">${p.paymentMethod}</span></td>
            <td><span class="badge ${statusClass}">${p.status}</span></td>
            <td>
                <button class="btn btn-icon btn-outline" onclick="editPayment(${p.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                <button class="btn btn-icon btn-danger" onclick="deletePayment(${p.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openPaymentModal() {
    editingPaymentId = null;
    document.getElementById('paymentForm').reset();
    document.getElementById('paymentModalTitle').textContent = 'Record Payment';
    document.getElementById('paymentDate').valueAsDate = new Date();
    document.getElementById('paymentModalOverlay').classList.add('active');
}

function closePaymentModal() {
    editingPaymentId = null;
    document.getElementById('paymentModalOverlay').classList.remove('active');
}

function editPayment(id) {
    const payment = allPayments.find(p => p.id === id);
    if (!payment) return;

    editingPaymentId = id;
    document.getElementById('paymentModalTitle').textContent = 'Edit Payment';
    document.getElementById('memberId').value = payment.member ? payment.member.id : '';
    document.getElementById('amount').value = payment.amount;
    document.getElementById('paymentMethod').value = payment.paymentMethod;
    document.getElementById('paymentDate').value = payment.paymentDate;
    document.getElementById('status').value = payment.status;
    document.getElementById('paymentModalOverlay').classList.add('active');
}

async function savePayment() {
    const paymentData = {
        memberId: document.getElementById('memberId').value,
        amount: parseFloat(document.getElementById('amount').value),
        paymentMethod: document.getElementById('paymentMethod').value,
        paymentDate: document.getElementById('paymentDate').value,
        status: document.getElementById('status').value
    };

    const url = editingPaymentId ? `/api/payments/${editingPaymentId}` : '/api/payments';
    const method = editingPaymentId ? 'PUT' : 'POST';

    try {
        const res = await authFetch(url, {
            method,
            body: JSON.stringify(paymentData)
        });

        if (res.ok) {
            showToast(editingPaymentId ? 'Payment updated' : 'Payment recorded successfully');
            closePaymentModal();
            fetchPayments();
        } else {
            showToast('Failed to save payment', 'error');
        }
    } catch (e) {
        showToast('Error saving payment', 'error');
    }
}

async function deletePayment(id) {
    if (!confirm('Are you sure you want to delete this payment record?')) return;

    try {
        const res = await authFetch(`/api/payments/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Payment deleted');
            fetchPayments();
        } else {
            showToast('Failed to delete payment', 'error');
        }
    } catch (e) {
        showToast('Error deleting payment', 'error');
    }
}

window.openPaymentModal = openPaymentModal;
window.closePaymentModal = closePaymentModal;
window.editPayment = editPayment;
window.deletePayment = deletePayment;
window.clearFilters = clearFilters;
window.exportPaymentsCsv = exportPaymentsCsv;
