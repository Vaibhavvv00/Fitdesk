// dashboard.js
document.addEventListener('DOMContentLoaded', async () => {
    // Current date
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    document.getElementById('currentDate').textContent = new Date().toLocaleDateString('en-US', options);

    await fetchStats();
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
            gradient.addColorStop(0, 'rgba(99, 102, 241, 0.5)');
            gradient.addColorStop(1, 'rgba(99, 102, 241, 0.0)');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Revenue',
                        data: revenues,
                        borderColor: '#6366f1',
                        backgroundColor: gradient,
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4,
                        pointBackgroundColor: '#6366f1',
                        pointBorderColor: '#fff',
                        pointHoverBackgroundColor: '#fff',
                        pointHoverBorderColor: '#6366f1'
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
                                color: '#94a3b8',
                                callback: function(value) { return '₹' + value; }
                            }
                        },
                        x: {
                            grid: { display: false },
                            ticks: { color: '#94a3b8' }
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
                        backgroundColor: ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#3b82f6'],
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
                            labels: { color: '#94a3b8', padding: 20 }
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
                    <td><span class="badge ${p.status === 'Completed' ? 'success' : (p.status === 'Failed' ? 'danger' : 'warning')}">${p.status}</span></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error('Failed to load recent payments', err);
    }
}
