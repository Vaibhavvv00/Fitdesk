// plans.js
let allPlans = [];

document.addEventListener('DOMContentLoaded', async () => {
    await fetchPlans();

    document.getElementById('planForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await savePlan();
    });
});

async function fetchPlans() {
    try {
        const res = await authFetch('/api/plans');
        if (res.ok) {
            allPlans = await res.json();
            renderPlans(allPlans);
        }
    } catch (e) {
        showToast('Failed to load plans', 'error');
    }
}

function renderPlans(plans) {
    const grid = document.getElementById('plansGrid');
    grid.innerHTML = '';
    
    plans.forEach(p => {
        const badgeClass = p.active ? 'success' : 'danger';
        const badgeText = p.active ? 'Active' : 'Inactive';
        
        const card = document.createElement('div');
        card.className = 'card plan-card';
        card.innerHTML = `
            <div class="badge plan-badge ${badgeClass}">${badgeText}</div>
            <h3>${p.name}</h3>
            <div class="plan-price">₹${p.price.toLocaleString()} <span>/ ${p.durationMonths} Months</span></div>
            <div class="plan-desc">${p.description || 'No description provided.'}</div>
            <div class="plan-actions">
                <button class="btn btn-outline" style="flex:1;" onclick="editPlan(${p.id})">Edit</button>
                <button class="btn btn-danger" onclick="deletePlan(${p.id})"><i class="fa-solid fa-trash"></i></button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function openPlanModal() {
    document.getElementById('planForm').reset();
    document.getElementById('planId').value = '';
    document.getElementById('modalTitle').textContent = 'Add Plan';
    document.getElementById('isActive').checked = true;
    document.getElementById('planModalOverlay').classList.add('active');
}

function closePlanModal() {
    document.getElementById('planModalOverlay').classList.remove('active');
}

async function editPlan(id) {
    const plan = allPlans.find(p => p.id === id);
    if (!plan) return;

    document.getElementById('planId').value = plan.id;
    document.getElementById('name').value = plan.name;
    document.getElementById('durationMonths').value = plan.durationMonths;
    document.getElementById('price').value = plan.price;
    document.getElementById('description').value = plan.description || '';
    document.getElementById('isActive').checked = plan.active !== false; // Check by default unless explicitly false
    
    document.getElementById('modalTitle').textContent = 'Edit Plan';
    document.getElementById('planModalOverlay').classList.add('active');
}

async function savePlan() {
    const id = document.getElementById('planId').value;
    
    const planData = {
        name: document.getElementById('name').value,
        durationMonths: parseInt(document.getElementById('durationMonths').value),
        price: parseFloat(document.getElementById('price').value),
        description: document.getElementById('description').value,
        isActive: document.getElementById('isActive').checked
    };

    const url = id ? `/api/plans/${id}` : '/api/plans';
    
    try {
        const res = await authFetch(url, {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(planData)
        });
        
        if (res.ok) {
            showToast(`Plan ${id ? 'updated' : 'added'} successfully`);
            closePlanModal();
            fetchPlans();
        } else {
            showToast('Failed to save plan', 'error');
        }
    } catch (e) {
        showToast('Error saving plan', 'error');
    }
}

async function deletePlan(id) {
    if (!confirm('Are you sure you want to delete this plan?')) return;
    
    try {
        const res = await authFetch(`/api/plans/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Plan deleted');
            fetchPlans();
        } else {
            showToast('Failed to delete plan', 'error');
        }
    } catch (e) {
        showToast('Error deleting plan', 'error');
    }
}
