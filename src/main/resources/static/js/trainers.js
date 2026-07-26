// trainers.js
let allTrainers = [];

document.addEventListener('DOMContentLoaded', async () => {
    await fetchTrainers();

    document.getElementById('trainerSearch').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allTrainers.filter(t => 
            t.fullName.toLowerCase().includes(term) || 
            t.specialization.toLowerCase().includes(term)
        );
        renderTrainers(filtered);
    });

    document.getElementById('trainerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await saveTrainer();
    });
});

async function fetchTrainers() {
    try {
        const res = await authFetch('/api/trainers');
        if (res.ok) {
            allTrainers = await res.json();
            renderTrainers(allTrainers);
        }
    } catch (e) {
        showToast('Failed to load trainers', 'error');
    }
}

function renderTrainers(trainers) {
    const tbody = document.querySelector('#trainersTable tbody');
    tbody.innerHTML = '';
    
    trainers.forEach(t => {
        const statusClass = (t.status || '').toUpperCase() === 'ACTIVE' ? 'success' : 'danger';
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${t.fullName}</td>
            <td>${t.email}</td>
            <td>${t.phone}</td>
            <td>${t.specialization}</td>
            <td>₹${t.salary.toLocaleString()}</td>
            <td>${t.joinDate}</td>
            <td><span class="badge ${statusClass}">${t.status || 'Active'}</span></td>
            <td>
                <button class="btn btn-icon btn-outline" onclick="editTrainer(${t.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                <button class="btn btn-icon btn-danger" onclick="deleteTrainer(${t.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openTrainerModal() {
    document.getElementById('trainerForm').reset();
    document.getElementById('trainerId').value = '';
    document.getElementById('modalTitle').textContent = 'Add Trainer';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('trainerModalOverlay').classList.add('active');
}

function closeTrainerModal() {
    document.getElementById('trainerModalOverlay').classList.remove('active');
}

async function editTrainer(id) {
    const trainer = allTrainers.find(t => t.id === id);
    if (!trainer) return;

    document.getElementById('trainerId').value = trainer.id;
    document.getElementById('fullName').value = trainer.fullName;
    document.getElementById('email').value = trainer.email;
    document.getElementById('phone').value = trainer.phone;
    document.getElementById('specialization').value = trainer.specialization;
    document.getElementById('salary').value = trainer.salary;
    document.getElementById('joinDate').value = trainer.joinDate;
    document.getElementById('status').value = (trainer.status || 'ACTIVE').toUpperCase();
    
    document.getElementById('modalTitle').textContent = 'Edit Trainer';
    document.getElementById('statusGroup').style.display = 'block';
    document.getElementById('trainerModalOverlay').classList.add('active');
}

async function saveTrainer() {
    const id = document.getElementById('trainerId').value;
    
    const trainerData = {
        fullName: document.getElementById('fullName').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        specialization: document.getElementById('specialization').value,
        salary: parseFloat(document.getElementById('salary').value),
        joinDate: document.getElementById('joinDate').value,
        status: id ? document.getElementById('status').value.toUpperCase() : 'ACTIVE'
    };

    const url = id ? `/api/trainers/${id}` : '/api/trainers';
    
    try {
        const res = await authFetch(url, {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(trainerData)
        });
        
        if (res.ok) {
            showToast(`Trainer ${id ? 'updated' : 'added'} successfully`);
            closeTrainerModal();
            fetchTrainers();
        } else {
            showToast('Failed to save trainer', 'error');
        }
    } catch (e) {
        showToast('Error saving trainer', 'error');
    }
}

async function deleteTrainer(id) {
    if (!confirm('Are you sure you want to delete this trainer?')) return;
    
    try {
        const res = await authFetch(`/api/trainers/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Trainer deleted');
            fetchTrainers();
        } else {
            showToast('Failed to delete trainer', 'error');
        }
    } catch (e) {
        showToast('Error deleting trainer', 'error');
    }
}
