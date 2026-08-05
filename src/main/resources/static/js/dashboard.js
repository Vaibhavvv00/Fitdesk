// dashboard.js
document.addEventListener('DOMContentLoaded', async () => {
    // Current date
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('currentDate').textContent = new Date().toLocaleDateString('en-US', options);

    await fetchStats();
    await fetchTodayAttendance();
    await fetchRevenueTrend();
    await fetchPlanDistribution();
    await fetchRecentPayments();
});

async function fetchStats() {
    try {
        const response = await authFetch('/api/dashboard/stats');
        if (response.ok) {
            const stats = await response.json();
            document.getElementById('statActiveMembers').textContent = stats.activeMembers;
            document.getElementById('statTotalTrainers').textContent = stats.totalTrainers;
            document.getElementById('statMonthlyRevenue').textContent = `₹${stats.monthlyRevenue.toLocaleString()}`;
            document.getElementById('statTodayAttendance').textContent = stats.todayAttendance;
            document.getElementById('statExpiringSoon').textContent = stats.expiringSoon ?? 0;
            document.getElementById('statExpiredMemberships').textContent = stats.expiredMemberships ?? 0;
        }
    } catch (err) {
        console.error('Failed to load stats', err);
    }
}

async function fetchTodayAttendance() {
    try {
        const response = await authFetch('/api/attendance/today');
        if (response.ok) {
            const records = await response.json();
            const tbody = document.querySelector('#dashboardAttendanceTable tbody');
            if (!tbody) return;
            tbody.innerHTML = '';
            
            if (!records.length) {
                tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:var(--text-muted);">No check-ins recorded today yet.</td></tr>';
                return;
            }

            records.forEach(a => {
                const name = a.member ? a.member.fullName : 'Unknown';
                const checkIn = formatDateTime(a.checkIn);
                const checkOut = a.checkOut ? formatDateTime(a.checkOut) : '—';
                const inGym = !a.checkOut;
                const statusClass = inGym ? 'active' : 'inactive';
                const statusLabel = inGym ? 'In Gym' : 'Checked Out';

                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${name}</strong></td>
                    <td>${checkIn}</td>
                    <td>${checkOut}</td>
                    <td><span class="badge ${statusClass}">${statusLabel}</span></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error('Failed to load today attendance', err);
    }
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

async function fetchRevenueTrend() {
    try {
        const response = await authFetch('/api/dashboard/revenue-trend');
        if (response.ok) {
            const data = await response.json();
            const labels = data.map(d => d.month);
            const revenues = data.map(d => d.revenue);

            const ctx = document.getElementById('revenueChart').getContext('2d');
            
            // Create gradient
            let gradient = ctx.createLinearGradient(0, 0, 0, 400);
            gradient.addColorStop(0, 'rgba(185, 255, 102, 0.4)');
            gradient.addColorStop(1, 'rgba(185, 255, 102, 0.0)');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Revenue',
                        data: revenues,
                        borderColor: '#b9ff66',
                        backgroundColor: gradient,
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: '#b9ff66',
                        pointBorderColor: '#080a0f',
                        pointHoverBackgroundColor: '#fff',
                        pointHoverBorderColor: '#b9ff66'
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            grid: { color: 'rgba(255,255,255,0.05)' },
                            ticks: {
                                color: '#8892a4',
                                callback: function(value) { return '₹' + value; }
                            }
                        },
                        x: {
                            grid: { display: false },
                            ticks: { color: '#8892a4' }
                        }
                    }
                }
            });
        }
    } catch (err) {
        console.error('Failed to load revenue trend', err);
    }
}

async function fetchPlanDistribution() {
    try {
        const response = await authFetch('/api/dashboard/plan-distribution');
        if (response.ok) {
            const data = await response.json();
            const labels = data.map(d => d.plan);
            const counts = data.map(d => d.count);

            const ctx = document.getElementById('planChart').getContext('2d');
            new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: counts,
                        backgroundColor: ['#b9ff66', '#34d399', '#fbbf24', '#f87171', '#60a5fa'],
                        borderWidth: 0,
                        hoverOffset: 4
                    }]
                },
                options: {
                    responsive: true,
                    cutout: '70%',
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: { color: '#8892a4', padding: 20 }
                        }
                    }
                }
            });
        }
    } catch (err) {
        console.error('Failed to load plan distribution', err);
    }
}

async function fetchRecentPayments() {
    try {
        const response = await authFetch('/api/payments');
        if (response.ok) {
            const payments = await response.json();
            const tbody = document.querySelector('#dashboardPaymentsTable tbody');
            tbody.innerHTML = '';
            
            // Show only last 10
            payments.slice(0, 10).forEach(p => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${p.member ? p.member.fullName : 'Unknown'}</td>
                    <td>₹${p.amount.toLocaleString()}</td>
                    <td>${p.paymentDate}</td>
                    <td><span class="badge ${p.paymentMethod.toLowerCase()}">${p.paymentMethod}</span></td>
                    <td><span class="badge ${p.status === 'Completed' || p.status === 'COMPLETED' ? 'success' : (p.status === 'Failed' || p.status === 'FAILED' ? 'danger' : 'warning')}">${p.status}</span></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error('Failed to load recent payments', err);
    }
}
