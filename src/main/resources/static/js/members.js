// members.js
let allMembers = [];

document.addEventListener('DOMContentLoaded', async () => {
    await loadPlansAndTrainers();
    await fetchMembers();

    document.getElementById('memberSearch').addEventListener('keyup', (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = allMembers.filter(m => 
            m.fullName.toLowerCase().includes(term) || 
            m.email.toLowerCase().includes(term)
        );
        renderMembers(filtered);
    });

    document.getElementById('memberForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        await saveMember();
    });
});

async function loadPlansAndTrainers() {
    try {
        const [plansRes, trainersRes] = await Promise.all([
            authFetch('/api/plans'),
            authFetch('/api/trainers')
        ]);
        
        if (plansRes.ok) {
            const plans = await plansRes.json();
            const planSelect = document.getElementById('planSelect');
            planSelect.innerHTML = '<option value="">Select Plan</option>';
            plans.forEach(p => {
                planSelect.innerHTML += `<option value="${p.id}">${p.name} (₹${p.price})</option>`;
            });
        }
        
        if (trainersRes.ok) {
            const trainers = await trainersRes.json();
            const trainerSelect = document.getElementById('trainerSelect');
            trainerSelect.innerHTML = '<option value="">Select Trainer</option>';
            trainers.forEach(t => {
                trainerSelect.innerHTML += `<option value="${t.id}">${t.fullName}</option>`;
            });
        }
    } catch (e) {
        showToast('Failed to load dropdowns', 'error');
    }
}

async function fetchMembers() {
    try {
        const res = await authFetch('/api/members');
        if (res.ok) {
            allMembers = await res.json();
            renderMembers(allMembers);
        }
    } catch (e) {
        showToast('Failed to load members', 'error');
    }
}

function renderMembers(members) {
    const tbody = document.querySelector('#membersTable tbody');
    tbody.innerHTML = '';
    
    members.forEach(m => {
        const planName = m.plan ? m.plan.name : 'N/A';
        const trainerName = m.trainer ? m.trainer.fullName : 'None';
        const statusClass = (m.status || '').toUpperCase() === 'ACTIVE' ? 'success' : 'danger';
        const planEnd = m.planEndDate || '—';
        let planEndCell = planEnd;
        if (m.planEndDate && (m.status || '').toUpperCase() === 'ACTIVE') {
            const end = new Date(m.planEndDate);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const inSeven = new Date(today);
            inSeven.setDate(inSeven.getDate() + 7);
            if (end < today) {
                planEndCell = `<span class="badge danger">${planEnd}</span>`;
            } else if (end <= inSeven) {
                planEndCell = `<span class="badge warning">${planEnd}</span>`;
            }
        }
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${m.fullName}</td>
            <td>${m.email}</td>
            <td>${m.phone}</td>
            <td>${planName}</td>
            <td>${trainerName}</td>
            <td>${planEndCell}</td>
            <td>${m.joinDate || '-'}</td>
            <td><span class="badge ${statusClass}">${m.status || 'Active'}</span></td>
            <td>
                <button class="btn btn-icon btn-outline" onclick="editMember(${m.id})" title="Edit"><i class="fa-solid fa-pen"></i></button>
                <button class="btn btn-icon btn-danger" onclick="deleteMember(${m.id})" title="Delete"><i class="fa-solid fa-trash"></i></button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function openMemberModal() {
    document.getElementById('memberForm').reset();
    document.getElementById('memberId').value = '';
    document.getElementById('modalTitle').textContent = 'Add Member';
    document.getElementById('statusGroup').style.display = 'none';
    document.getElementById('memberModalOverlay').classList.add('active');
}

function closeMemberModal() {
    document.getElementById('memberModalOverlay').classList.remove('active');
}

async function editMember(id) {
    const member = allMembers.find(m => m.id === id);
    if (!member) return;

    document.getElementById('memberId').value = member.id;
    document.getElementById('fullName').value = member.fullName;
    document.getElementById('email').value = member.email;
    document.getElementById('phone').value = member.phone;
    document.getElementById('gender').value = member.gender;
    document.getElementById('dateOfBirth').value = member.dateOfBirth;
    
    if (member.plan) document.getElementById('planSelect').value = member.plan.id;
    if (member.trainer) document.getElementById('trainerSelect').value = member.trainer.id;
    
    document.getElementById('status').value = (member.status || 'ACTIVE').toUpperCase();
    
    document.getElementById('modalTitle').textContent = 'Edit Member';
    document.getElementById('statusGroup').style.display = 'block';
    document.getElementById('memberModalOverlay').classList.add('active');
}

async function saveMember() {
    const id = document.getElementById('memberId').value;
    const planId = document.getElementById('planSelect').value;
    const trainerId = document.getElementById('trainerSelect').value;
    
    const memberData = {
        fullName: document.getElementById('fullName').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        gender: document.getElementById('gender').value,
        dateOfBirth: document.getElementById('dateOfBirth').value
    };
    
    if (id) {
        memberData.status = document.getElementById('status').value.toUpperCase();
    }

    let url = id ? `/api/members/${id}` : '/api/members';
    
    // Add query params
    const params = new URLSearchParams();
    if (planId) params.append('planId', planId);
    if (trainerId) params.append('trainerId', trainerId);
    if (params.toString()) {
        url += '?' + params.toString();
    }

    try {
        const res = await authFetch(url, {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(memberData)
        });
        
        if (res.ok) {
            showToast(`Member ${id ? 'updated' : 'added'} successfully`);
            closeMemberModal();
            fetchMembers();
        } else {
            showToast('Failed to save member', 'error');
        }
    } catch (e) {
        showToast('Error saving member', 'error');
    }
}

async function deleteMember(id) {
    if (!confirm('Are you sure you want to delete this member?')) return;
    
    try {
        const res = await authFetch(`/api/members/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Member deleted');
            fetchMembers();
        } else {
            showToast('Failed to delete member', 'error');
        }
    } catch (e) {
        showToast('Error deleting member', 'error');
    }
}
