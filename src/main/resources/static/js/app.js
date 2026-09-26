/**
 * CareConnect - Frontend Application JavaScript
 * Connects to Spring Boot REST Endpoints (Indian Localization & INR Currency)
 */

// Application State
let currentUser = null;
let allDoctors = [];

// Department Icon Map
const DEPARTMENT_ICONS = {
  'Cardiology': 'fa-solid fa-heart-pulse text-rose-500 bg-rose-50',
  'Neurology': 'fa-solid fa-brain text-purple-500 bg-purple-50',
  'Dermatology': 'fa-solid fa-hand-dots text-amber-500 bg-amber-50',
  'Orthopedics': 'fa-solid fa-bone text-emerald-500 bg-emerald-50',
  'Pediatrics': 'fa-solid fa-baby text-sky-500 bg-sky-50',
  'General Medicine': 'fa-solid fa-stethoscope text-indigo-500 bg-indigo-50'
};

// Format currency into Indian Rupees (INR)
function formatINR(amount) {
  if (amount == null) return '₹0';
  return '₹' + Number(amount).toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

// Initialize Application on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
  // Check stored user session
  const stored = localStorage.getItem('careconnect_user');
  if (stored) {
    try {
      currentUser = JSON.parse(stored);
    } catch (e) {
      currentUser = null;
    }
  }

  updateAuthUI();
  loadSpecializations();
  loadDoctors();

  // Set default minimum booking date to today
  const todayStr = new Date().toISOString().split('T')[0];
  const bookDateEl = document.getElementById('bookDate');
  if (bookDateEl) {
    bookDateEl.min = todayStr;
  }
});

// Toast Notification System
function showToast(message, type = 'success') {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  
  const bg = type === 'success' ? 'bg-emerald-600' : 'bg-rose-600';
  const icon = type === 'success' ? 'fa-circle-check' : 'fa-triangle-exclamation';

  toast.className = `${bg} text-white px-4 py-3 rounded-2xl shadow-xl flex items-center space-x-3 text-sm font-medium transform transition-all duration-300 translate-y-4 opacity-0 pointer-events-auto`;
  toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;
  
  container.appendChild(toast);
  
  setTimeout(() => {
    toast.classList.remove('translate-y-4', 'opacity-0');
  }, 10);

  setTimeout(() => {
    toast.classList.add('opacity-0', 'translate-y-2');
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

// Navigation & Tab Switching
function showSection(sectionId) {
  const sections = ['sectionHome', 'sectionDoctors', 'sectionPatient', 'sectionDoctor', 'sectionAdmin'];
  const navBtns = {
    'home': 'navHome',
    'doctors': 'navDoctors',
    'patient-portal': 'navPatient',
    'doctor-portal': 'navDoctor',
    'admin-portal': 'navAdmin'
  };

  sections.forEach(id => {
    const el = document.getElementById(id);
    if (el) el.classList.add('hidden');
  });

  // Reset active navbar classes
  ['navHome', 'navDoctors', 'navPatient', 'navDoctor', 'navAdmin'].forEach(navId => {
    const btn = document.getElementById(navId);
    if (btn) {
      btn.className = 'px-3 py-2 rounded-lg text-sm font-medium text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition';
    }
  });

  // Show target section
  let activeNavId = 'navHome';
  if (sectionId === 'home') {
    document.getElementById('sectionHome').classList.remove('hidden');
    activeNavId = 'navHome';
  } else if (sectionId === 'doctors') {
    document.getElementById('sectionDoctors').classList.remove('hidden');
    activeNavId = 'navDoctors';
    filterDoctorsList();
  } else if (sectionId === 'patient-portal') {
    if (!requireAuth('PATIENT')) return;
    document.getElementById('sectionPatient').classList.remove('hidden');
    activeNavId = 'navPatient';
    loadPatientAppointments();
  } else if (sectionId === 'doctor-portal') {
    if (!requireAuth('DOCTOR')) return;
    document.getElementById('sectionDoctor').classList.remove('hidden');
    activeNavId = 'navDoctor';
    loadDoctorAppointments();
  } else if (sectionId === 'admin-portal') {
    if (!requireAuth('ADMIN')) return;
    document.getElementById('sectionAdmin').classList.remove('hidden');
    activeNavId = 'navAdmin';
    loadAdminDashboard();
  }

  const activeBtn = document.getElementById(activeNavId);
  if (activeBtn) {
    activeBtn.className = 'px-3 py-2 rounded-lg text-sm font-medium text-sky-700 bg-sky-50 transition';
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Authentication UI & Session Handling
function updateAuthUI() {
  const authGuest = document.getElementById('authGuest');
  const authUser = document.getElementById('authUser');
  const userFullName = document.getElementById('userFullName');
  const userRoleBadge = document.getElementById('userRoleBadge');

  const navPatient = document.getElementById('navPatient');
  const navDoctor = document.getElementById('navDoctor');
  const navAdmin = document.getElementById('navAdmin');

  if (currentUser) {
    authGuest.classList.add('hidden');
    authUser.classList.remove('hidden');
    userFullName.innerText = currentUser.fullName;
    userRoleBadge.innerText = currentUser.role;

    if (currentUser.role === 'PATIENT') {
      navPatient.classList.remove('hidden');
      navDoctor.classList.add('hidden');
      navAdmin.classList.add('hidden');
    } else if (currentUser.role === 'DOCTOR') {
      navPatient.classList.add('hidden');
      navDoctor.classList.remove('hidden');
      navAdmin.classList.add('hidden');
    } else if (currentUser.role === 'ADMIN') {
      navPatient.classList.add('hidden');
      navDoctor.classList.add('hidden');
      navAdmin.classList.remove('hidden');
    }
  } else {
    authGuest.classList.remove('hidden');
    authUser.classList.add('hidden');
    navPatient.classList.add('hidden');
    navDoctor.classList.add('hidden');
    navAdmin.classList.add('hidden');
  }
}

function requireAuth(expectedRole = null) {
  if (!currentUser) {
    openAuthModal('login');
    showToast('Please sign in to access this portal.', 'error');
    return false;
  }
  if (expectedRole && currentUser.role !== expectedRole && currentUser.role !== 'ADMIN') {
    showToast(`Access restricted. You need a ${expectedRole} account for this view.`, 'error');
    return false;
  }
  return true;
}

// Quick Preset Login for Evaluators & Interviews
async function quickLogin(email, password) {
  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) {
      showToast(data.message || 'Login failed', 'error');
      return;
    }
    currentUser = data;
    localStorage.setItem('careconnect_user', JSON.stringify(data));
    updateAuthUI();
    showToast(`Logged in as ${data.fullName} (${data.role})`);

    // Navigate to role view
    if (data.role === 'PATIENT') showSection('patient-portal');
    else if (data.role === 'DOCTOR') showSection('doctor-portal');
    else if (data.role === 'ADMIN') showSection('admin-portal');
    else showSection('home');
  } catch (err) {
    showToast('Could not connect to server. Ensure Spring Boot is running.', 'error');
  }
}

function logout() {
  currentUser = null;
  localStorage.removeItem('careconnect_user');
  updateAuthUI();
  showSection('home');
  showToast('Logged out successfully.');
}

// Modal Handlers
function openAuthModal(tab = 'login') {
  document.getElementById('authModal').classList.remove('hidden');
  switchAuthTab(tab);
}

function closeAuthModal() {
  document.getElementById('authModal').classList.add('hidden');
}

function switchAuthTab(tab) {
  const loginForm = document.getElementById('loginForm');
  const registerForm = document.getElementById('registerForm');
  const tabLoginBtn = document.getElementById('tabLoginBtn');
  const tabRegisterBtn = document.getElementById('tabRegisterBtn');

  if (tab === 'login') {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    tabLoginBtn.className = 'flex-1 py-4 text-center font-bold text-sm text-sky-600 border-b-2 border-sky-600 transition';
    tabRegisterBtn.className = 'flex-1 py-4 text-center font-bold text-sm text-slate-500 hover:text-slate-800 transition';
  } else {
    loginForm.classList.add('hidden');
    registerForm.classList.remove('hidden');
    tabRegisterBtn.className = 'flex-1 py-4 text-center font-bold text-sm text-teal-600 border-b-2 border-teal-600 transition';
    tabLoginBtn.className = 'flex-1 py-4 text-center font-bold text-sm text-slate-500 hover:text-slate-800 transition';
  }
}

// Auth Submissions
async function handleLoginSubmit(e) {
  e.preventDefault();
  const email = document.getElementById('loginEmail').value;
  const password = document.getElementById('loginPassword').value;

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    const data = await res.json();
    if (!res.ok) {
      showToast(data.message || 'Invalid credentials', 'error');
      return;
    }
    currentUser = data;
    localStorage.setItem('careconnect_user', JSON.stringify(data));
    updateAuthUI();
    closeAuthModal();
    showToast(data.message || 'Login successful!');

    if (data.role === 'PATIENT') showSection('patient-portal');
    else if (data.role === 'DOCTOR') showSection('doctor-portal');
    else if (data.role === 'ADMIN') showSection('admin-portal');
  } catch (err) {
    showToast('Failed to connect to backend server.', 'error');
  }
}

async function handleRegisterSubmit(e) {
  e.preventDefault();
  const payload = {
    fullName: document.getElementById('regFullName').value,
    email: document.getElementById('regEmail').value,
    phone: document.getElementById('regPhone').value,
    password: document.getElementById('regPassword').value,
    role: document.getElementById('regRole').value
  };

  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    const data = await res.json();
    if (!res.ok) {
      showToast(data.message || 'Registration failed', 'error');
      return;
    }
    currentUser = data;
    localStorage.setItem('careconnect_user', JSON.stringify(data));
    updateAuthUI();
    closeAuthModal();
    showToast('Account registered successfully! Welcome to CareConnect.');

    if (data.role === 'PATIENT') showSection('patient-portal');
    else if (data.role === 'DOCTOR') showSection('doctor-portal');
    else if (data.role === 'ADMIN') showSection('admin-portal');
  } catch (err) {
    showToast('Registration error occurred.', 'error');
  }
}

// Load Specializations and Doctors
async function loadSpecializations() {
  try {
    const res = await fetch('/api/doctors/specializations');
    const specs = await res.json();

    const heroSelect = document.getElementById('heroSpecialtySelect');
    const filterSelect = document.getElementById('doctorSpecialtyFilter');
    const specsGrid = document.getElementById('specialtiesGrid');

    specsGrid.innerHTML = '';

    specs.forEach(spec => {
      // Add to selects
      const opt1 = new Option(spec, spec);
      const opt2 = new Option(spec, spec);
      heroSelect.add(opt1);
      filterSelect.add(opt2);

      // Add to departments grid
      const iconClasses = DEPARTMENT_ICONS[spec] || 'fa-solid fa-stethoscope text-sky-500 bg-sky-50';
      const [faIcon, colorText, colorBg] = iconClasses.split(' ');

      const card = document.createElement('div');
      card.className = 'bg-white p-5 rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md hover:border-sky-300 transition cursor-pointer text-center group';
      card.onclick = () => {
        filterSelect.value = spec;
        showSection('doctors');
      };
      card.innerHTML = `
        <div class="w-12 h-12 rounded-2xl ${colorBg} ${colorText} flex items-center justify-center text-xl mx-auto mb-3 group-hover:scale-110 transition">
          <i class="${faIcon}"></i>
        </div>
        <div class="font-bold text-slate-800 text-sm">${spec}</div>
        <div class="text-[11px] text-slate-400 mt-1">Specialist OPD</div>
      `;
      specsGrid.appendChild(card);
    });
  } catch (err) {
    console.error('Error loading specializations:', err);
  }
}

async function loadDoctors() {
  try {
    const res = await fetch('/api/doctors');
    allDoctors = await res.json();

    renderFeaturedDoctors();
    renderAllDoctors(allDoctors);
  } catch (err) {
    console.error('Error loading doctors:', err);
  }
}

function renderFeaturedDoctors() {
  const container = document.getElementById('featuredDoctorsGrid');
  if (!container) return;

  container.innerHTML = '';
  // Show first 3 doctors as featured
  const featured = allDoctors.slice(0, 3);
  featured.forEach(doc => {
    container.appendChild(createDoctorCard(doc));
  });
}

function renderAllDoctors(doctors) {
  const container = document.getElementById('allDoctorsGrid');
  if (!container) return;

  container.innerHTML = '';
  if (doctors.length === 0) {
    container.innerHTML = `
      <div class="col-span-3 text-center py-16 bg-white rounded-3xl border border-slate-200">
        <i class="fa-solid fa-user-doctor text-4xl text-slate-300 mb-3"></i>
        <h3 class="text-base font-bold text-slate-700">No Doctors Found</h3>
        <p class="text-sm text-slate-400 mt-1">Try adjusting your search keywords or specialty filter.</p>
      </div>
    `;
    return;
  }

  doctors.forEach(doc => {
    container.appendChild(createDoctorCard(doc));
  });
}

function createDoctorCard(doc) {
  const card = document.createElement('div');
  card.className = 'doctor-card bg-white rounded-3xl border border-slate-200/80 overflow-hidden shadow-sm flex flex-col justify-between';

  const iconClass = DEPARTMENT_ICONS[doc.specialization] || 'fa-solid fa-stethoscope text-sky-500 bg-sky-50';
  const faIcon = iconClass.split(' ')[0];

  card.innerHTML = `
    <div class="p-6">
      <div class="flex items-start space-x-4">
        <img src="${doc.imageUrl}" alt="${doc.name}" class="w-16 h-16 rounded-2xl object-cover shadow-sm border border-slate-100 flex-shrink-0" onerror="this.onerror=null;this.src='https://ui-avatars.com/api/?name=' + encodeURIComponent('${doc.name}') + '&background=0284c7&color=fff&bold=true';">
        <div class="flex-grow">
          <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-sky-50 text-sky-700 border border-sky-200/60 mb-1">
            <i class="${faIcon} mr-1 text-[10px]"></i> ${doc.specialization}
          </span>
          <h3 class="text-base font-bold text-slate-900 leading-snug">${doc.name}</h3>
          <p class="text-xs text-slate-500 font-medium">${doc.qualification}</p>
        </div>
      </div>

      <p class="text-xs text-slate-600 mt-4 line-clamp-2 leading-relaxed">${doc.bio || 'Experienced clinical specialist offering patient-focused care.'}</p>

      <div class="mt-4 pt-4 border-t border-slate-100 grid grid-cols-2 gap-2 text-xs">
        <div>
          <span class="text-slate-400 block font-medium">Experience</span>
          <span class="font-bold text-slate-800">${doc.experienceYears}+ Years</span>
        </div>
        <div>
          <span class="text-slate-400 block font-medium">Available</span>
          <span class="font-bold text-slate-800 truncate block" title="${doc.availableDays}">${doc.availableDays}</span>
        </div>
      </div>
    </div>

    <div class="p-4 bg-slate-50 border-t border-slate-100 flex items-center justify-between">
      <div>
        <span class="text-[10px] uppercase font-bold text-slate-400">Consultation Fee</span>
        <div class="text-base font-extrabold text-slate-900">${formatINR(doc.consultationFee)}</div>
      </div>
      <button onclick="openBookingModal(${doc.id})" class="px-4 py-2 bg-sky-600 hover:bg-sky-700 text-white rounded-xl text-xs font-bold transition shadow-sm flex items-center gap-1.5">
        <i class="fa-regular fa-calendar-check"></i> Book OPD
      </button>
    </div>
  `;

  return card;
}

function handleHeroSearch() {
  const query = document.getElementById('heroSearchInput').value.trim();
  const spec = document.getElementById('heroSpecialtySelect').value;

  const filterInput = document.getElementById('doctorSearchInput');
  const filterSelect = document.getElementById('doctorSpecialtyFilter');

  if (filterInput) filterInput.value = query;
  if (filterSelect) filterSelect.value = spec;

  showSection('doctors');
}

function filterDoctorsList() {
  const query = (document.getElementById('doctorSearchInput')?.value || '').toLowerCase().trim();
  const spec = document.getElementById('doctorSpecialtyFilter')?.value || '';

  const filtered = allDoctors.filter(doc => {
    const matchesQuery = !query || doc.name.toLowerCase().includes(query) || doc.specialization.toLowerCase().includes(query);
    const matchesSpec = !spec || doc.specialization === spec;
    return matchesQuery && matchesSpec;
  });

  renderAllDoctors(filtered);
}

// Booking System
let currentBookingDoctor = null;

function openBookingModal(doctorId) {
  if (!currentUser) {
    openAuthModal('login');
    showToast('Please sign in as a patient to schedule an appointment.', 'error');
    return;
  }

  const doctor = allDoctors.find(d => d.id === doctorId);
  if (!doctor) return;

  currentBookingDoctor = doctor;

  document.getElementById('bookDoctorId').value = doctor.id;
  document.getElementById('bookDoctorNameSubtitle').innerText = `With ${doctor.name} (${doctor.specialization})`;
  const modalDocImg = document.getElementById('bookDoctorImg');
  modalDocImg.onerror = function() {
    this.onerror = null;
    this.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(doctor.name)}&background=0284c7&color=fff&bold=true`;
  };
  modalDocImg.src = doctor.imageUrl;
  document.getElementById('bookDoctorName').innerText = doctor.name;
  document.getElementById('bookDoctorSpec').innerText = `${doctor.specialization} &bull; ${doctor.qualification}`;
  document.getElementById('bookDoctorFee').innerText = formatINR(doctor.consultationFee);

  // Default to tomorrow
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const tomorrowStr = tomorrow.toISOString().split('T')[0];
  document.getElementById('bookDate').value = tomorrowStr;

  document.getElementById('bookSymptoms').value = '';
  document.getElementById('selectedTimeSlot').value = '';

  document.getElementById('bookingModal').classList.remove('hidden');

  // Load slots for tomorrow
  handleDateChange();
}

function closeBookingModal() {
  document.getElementById('bookingModal').classList.add('hidden');
}

async function handleDateChange() {
  const doctorId = document.getElementById('bookDoctorId').value;
  const dateVal = document.getElementById('bookDate').value;
  const slotsContainer = document.getElementById('slotsContainer');
  const slotLoadingText = document.getElementById('slotLoadingText');
  const selectedTimeSlot = document.getElementById('selectedTimeSlot');

  selectedTimeSlot.value = '';

  if (!doctorId || !dateVal) return;

  slotLoadingText.innerText = 'Checking availability...';
  slotsContainer.innerHTML = '';

  try {
    const res = await fetch(`/api/doctors/${doctorId}/availability?date=${dateVal}`);
    const data = await res.json();
    slotLoadingText.innerText = `OPD Days: ${data.availableDays}`;

    if (!data.slots || data.slots.length === 0) {
      slotsContainer.innerHTML = `<div class="text-xs text-rose-500 col-span-3 py-2 text-center">No time slots scheduled for this doctor.</div>`;
      return;
    }

    data.slots.forEach(item => {
      const pill = document.createElement('div');
      const isAvail = item.available;

      pill.className = `slot-pill text-xs p-2 text-center rounded-xl border font-medium ${
        isAvail ? 'border-slate-200 bg-white text-slate-700' : 'disabled'
      }`;
      pill.innerHTML = `<span>${item.slot}</span> ${!isAvail ? '<span class="block text-[9px] text-rose-500">Booked</span>' : ''}`;

      if (isAvail) {
        pill.onclick = () => {
          document.querySelectorAll('.slot-pill').forEach(p => p.classList.remove('selected'));
          pill.classList.add('selected');
          selectedTimeSlot.value = item.slot;
        };
      }

      slotsContainer.appendChild(pill);
    });

  } catch (err) {
    slotLoadingText.innerText = 'Failed to load slots.';
  }
}

async function submitBooking(e) {
  e.preventDefault();
  const doctorId = document.getElementById('bookDoctorId').value;
  const appointmentDate = document.getElementById('bookDate').value;
  const timeSlot = document.getElementById('selectedTimeSlot').value;
  const symptoms = document.getElementById('bookSymptoms').value.trim();

  if (!timeSlot) {
    showToast('Please select an available time slot.', 'error');
    return;
  }

  const payload = {
    patientId: currentUser.id,
    doctorId: parseInt(doctorId),
    appointmentDate,
    timeSlot,
    symptoms
  };

  try {
    const res = await fetch('/api/appointments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    const data = await res.json();
    if (!res.ok) {
      showToast(data.message || 'Slot already booked or unavailable.', 'error');
      return;
    }

    closeBookingModal();
    showToast('Consultation appointment booked successfully!');
    showSection('patient-portal');
  } catch (err) {
    showToast('Booking failed. Please try again.', 'error');
  }
}

// Patient Portal Appointments
async function loadPatientAppointments() {
  if (!currentUser) return;
  const container = document.getElementById('patientAppointmentsTable');
  container.innerHTML = `<div class="p-8 text-center text-slate-400 text-sm">Loading appointments...</div>`;

  try {
    const res = await fetch(`/api/appointments/patient/${currentUser.id}`);
    const list = await res.json();

    if (list.length === 0) {
      container.innerHTML = `
        <div class="p-12 text-center">
          <i class="fa-regular fa-calendar text-4xl text-slate-300 mb-3"></i>
          <h4 class="text-sm font-bold text-slate-700">No Consultations Scheduled</h4>
          <p class="text-xs text-slate-400 mt-1 mb-4">You have not scheduled any doctor visits yet.</p>
          <button onclick="showSection('doctors')" class="px-4 py-2 bg-sky-600 text-white rounded-xl text-xs font-semibold">
            Book OPD Visit
          </button>
        </div>
      `;
      return;
    }

    container.innerHTML = '';
    list.forEach(appt => {
      const row = document.createElement('div');
      row.className = 'p-5 flex flex-col md:flex-row md:items-center justify-between gap-4 hover:bg-slate-50/80 transition';

      const badgeClass = `badge-${appt.status.toLowerCase()}`;

      row.innerHTML = `
        <div class="space-y-1">
          <div class="flex items-center gap-2">
            <span class="text-base font-bold text-slate-900">${appt.doctorName}</span>
            <span class="text-xs px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider ${badgeClass}">
              ${appt.status}
            </span>
          </div>
          <div class="text-xs text-sky-700 font-semibold">${appt.doctorSpecialization} &bull; Fee: ${formatINR(appt.consultationFee)}</div>
          <div class="text-xs text-slate-500">
            <i class="fa-regular fa-clock mr-1 text-slate-400"></i> ${appt.appointmentDate} at <strong>${appt.timeSlot}</strong>
          </div>
          <div class="text-xs text-slate-600 bg-slate-100 p-2 rounded-lg mt-1 inline-block">
            <strong>Reason:</strong> ${appt.symptoms}
          </div>
        </div>

        <div class="flex items-center gap-2 flex-shrink-0">
          ${appt.status === 'COMPLETED' ? `
            <button onclick='viewPrescription(${JSON.stringify(appt)})' class="px-3.5 py-2 bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold rounded-xl transition flex items-center gap-1.5 shadow-sm">
              <i class="fa-solid fa-file-prescription"></i> View Prescription
            </button>
          ` : ''}

          ${appt.status !== 'CANCELLED' && appt.status !== 'COMPLETED' ? `
            <button onclick="cancelAppointment(${appt.id})" class="px-3.5 py-2 bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200 text-xs font-semibold rounded-xl transition">
              Cancel
            </button>
          ` : ''}
        </div>
      `;
      container.appendChild(row);
    });

  } catch (err) {
    container.innerHTML = `<div class="p-8 text-center text-rose-500 text-sm">Failed to load appointments.</div>`;
  }
}

// Doctor Portal Schedule
async function loadDoctorAppointments() {
  if (!currentUser || !currentUser.doctorId) {
    showToast('Doctor ID not linked to this user account.', 'error');
    return;
  }

  const container = document.getElementById('doctorAppointmentsTable');
  container.innerHTML = `<div class="p-8 text-center text-slate-400 text-sm">Loading schedule...</div>`;

  try {
    const res = await fetch(`/api/appointments/doctor/${currentUser.doctorId}`);
    const list = await res.json();

    if (list.length === 0) {
      container.innerHTML = `
        <div class="p-12 text-center text-slate-400">
          <i class="fa-solid fa-calendar-check text-4xl mb-3 text-slate-300"></i>
          <h4 class="text-sm font-bold text-slate-700">No Patient Visits Scheduled</h4>
          <p class="text-xs mt-1">Your consultation OPD queue is currently clear.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = '';
    list.forEach(appt => {
      const row = document.createElement('div');
      row.className = 'p-5 flex flex-col md:flex-row md:items-center justify-between gap-4 hover:bg-slate-50/80 transition';
      const badgeClass = `badge-${appt.status.toLowerCase()}`;

      row.innerHTML = `
        <div class="space-y-1">
          <div class="flex items-center gap-2">
            <span class="text-base font-bold text-slate-900">${appt.patientName}</span>
            <span class="text-xs px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider ${badgeClass}">
              ${appt.status}
            </span>
          </div>
          <div class="text-xs text-slate-500">
            <i class="fa-solid fa-phone mr-1 text-slate-400"></i> ${appt.patientPhone || 'N/A'} &bull; 
            <i class="fa-regular fa-envelope mr-1 text-slate-400"></i> ${appt.patientEmail}
          </div>
          <div class="text-xs text-slate-700">
            <i class="fa-regular fa-clock mr-1 text-slate-400"></i> Date: <strong>${appt.appointmentDate}</strong> | Slot: <strong>${appt.timeSlot}</strong>
          </div>
          <div class="text-xs text-slate-600 bg-slate-100 p-2 rounded-lg mt-1">
            <strong>Reported Symptoms:</strong> ${appt.symptoms}
          </div>
          ${appt.doctorNotes ? `<div class="text-xs text-teal-700 bg-teal-50 p-2 rounded-lg border border-teal-200"><strong>Notes:</strong> ${appt.doctorNotes}</div>` : ''}
        </div>

        <div class="flex flex-wrap items-center gap-2 flex-shrink-0">
          <button onclick='openDoctorPrescribeModal(${JSON.stringify(appt)})' class="px-3.5 py-2 bg-teal-600 hover:bg-teal-700 text-white text-xs font-bold rounded-xl transition flex items-center gap-1.5 shadow-sm">
            <i class="fa-solid fa-notes-medical"></i> ${appt.status === 'COMPLETED' ? 'Edit Prescription' : 'Prescribe & Complete'}
          </button>

          ${appt.status === 'PENDING' ? `
            <button onclick="updateAppointmentStatus(${appt.id}, 'CONFIRMED')" class="px-3 py-2 bg-sky-50 text-sky-700 hover:bg-sky-100 border border-sky-200 text-xs font-semibold rounded-xl transition">
              Confirm
            </button>
          ` : ''}

          ${appt.status !== 'CANCELLED' && appt.status !== 'COMPLETED' ? `
            <button onclick="cancelAppointment(${appt.id})" class="px-3 py-2 bg-rose-50 text-rose-700 hover:bg-rose-100 border border-rose-200 text-xs font-semibold rounded-xl transition">
              Cancel
            </button>
          ` : ''}
        </div>
      `;
      container.appendChild(row);
    });

  } catch (err) {
    container.innerHTML = `<div class="p-8 text-center text-rose-500 text-sm">Failed to load doctor appointments.</div>`;
  }
}

// Admin Dashboard & Analytics
async function loadAdminDashboard() {
  try {
    const res = await fetch('/api/dashboard/stats');
    const stats = await res.json();

    document.getElementById('kpiTotalAppts').innerText = stats.totalAppointments;
    document.getElementById('kpiTotalDoctors').innerText = stats.totalDoctors;
    document.getElementById('kpiTotalPatients').innerText = stats.totalPatients;
    document.getElementById('kpiTotalRevenue').innerText = formatINR(stats.estimatedRevenue);

    renderStatusBreakdown(stats);
    renderSpecialtyBreakdown(stats.specializationStats);
    renderAdminAppointments(stats.recentAppointments);
  } catch (err) {
    showToast('Failed to load clinic statistics.', 'error');
  }
}

function renderStatusBreakdown(stats) {
  const container = document.getElementById('statusBreakdownContainer');
  if (!container) return;
  const total = (stats.confirmedAppointments || 0) + (stats.completedAppointments || 0) + 
                (stats.pendingAppointments || 0) + (stats.cancelledAppointments || 0) || 1;

  const items = [
    { label: 'Confirmed', count: stats.confirmedAppointments || 0, color: '#0284c7', bg: '#e0f2fe' },
    { label: 'Completed', count: stats.completedAppointments || 0, color: '#16a34a', bg: '#dcfce7' },
    { label: 'Pending', count: stats.pendingAppointments || 0, color: '#d97706', bg: '#fef3c7' },
    { label: 'Cancelled', count: stats.cancelledAppointments || 0, color: '#dc2626', bg: '#fee2e2' }
  ];

  container.innerHTML = items.map(item => {
    const pct = Math.round((item.count / total) * 100);
    return `
      <div class="mb-3">
        <div class="flex justify-between items-center text-xs font-semibold mb-1">
          <span style="color: ${item.color}; font-weight: 700;">${item.label}</span>
          <span class="text-slate-600">${item.count} (${pct}%)</span>
        </div>
        <div style="background-color: ${item.bg}; height: 8px; border-radius: 9999px; overflow: hidden;">
          <div style="width: ${pct}%; background-color: ${item.color}; height: 100%; border-radius: 9999px; transition: width 0.4s ease;"></div>
        </div>
      </div>
    `;
  }).join('');
}

function renderSpecialtyBreakdown(specStats) {
  const container = document.getElementById('specialtyBreakdownContainer');
  if (!container) return;
  const entries = Object.entries(specStats || {});
  if (entries.length === 0) {
    container.innerHTML = '<div class="text-xs text-slate-400 py-4 text-center">No active doctors registered yet.</div>';
    return;
  }
  const maxCount = Math.max(...entries.map(e => e[1]), 1);

  container.innerHTML = entries.map(([spec, count]) => {
    const pct = Math.round((count / maxCount) * 100);
    return `
      <div class="mb-3">
        <div class="flex justify-between items-center text-xs font-semibold mb-1">
          <span class="text-slate-800 font-semibold">${spec}</span>
          <span class="text-sky-600 font-bold">${count} Doctor${count > 1 ? 's' : ''}</span>
        </div>
        <div style="background-color: #f1f5f9; height: 8px; border-radius: 9999px; overflow: hidden;">
          <div style="width: ${pct}%; background-color: #0284c7; height: 100%; border-radius: 9999px; transition: width 0.4s ease;"></div>
        </div>
      </div>
    `;
  }).join('');
}

function renderAdminAppointments(list) {
  const container = document.getElementById('adminAppointmentsTable');
  if (!container) return;

  if (!list || list.length === 0) {
    container.innerHTML = `<div class="p-8 text-center text-slate-400 text-sm">No appointment records found.</div>`;
    return;
  }

  container.innerHTML = '';
  list.forEach(appt => {
    const row = document.createElement('div');
    row.className = 'p-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs hover:bg-slate-50/80 transition';
    const badgeClass = `badge-${appt.status.toLowerCase()}`;

    row.innerHTML = `
      <div class="space-y-1">
        <div class="flex items-center gap-2">
          <span class="font-bold text-slate-900 text-sm">#${appt.id} &bull; ${appt.patientName}</span>
          <span class="px-2 py-0.5 rounded-full font-bold uppercase ${badgeClass}">
            ${appt.status}
          </span>
        </div>
        <div class="text-slate-500">
          Doctor: <strong>${appt.doctorName}</strong> (${appt.doctorSpecialization}) &bull; Date: <strong>${appt.appointmentDate}</strong> (${appt.timeSlot})
        </div>
        <div class="text-slate-600">
          Symptoms: ${appt.symptoms}
        </div>
      </div>

      <div class="flex items-center gap-2">
        <select onchange="updateAppointmentStatus(${appt.id}, this.value)" class="px-2.5 py-1.5 bg-white border border-slate-200 rounded-lg text-xs font-semibold focus:ring-2 focus:ring-sky-500">
          <option value="CONFIRMED" ${appt.status === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
          <option value="COMPLETED" ${appt.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
          <option value="CANCELLED" ${appt.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
          <option value="PENDING" ${appt.status === 'PENDING' ? 'selected' : ''}>PENDING</option>
        </select>
        ${appt.prescription ? `
          <button onclick='viewPrescription(${JSON.stringify(appt)})' class="px-2.5 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg font-semibold" title="View Rx">
            <i class="fa-solid fa-file-prescription"></i>
          </button>
        ` : ''}
      </div>
    `;
    container.appendChild(row);
  });
}

// Appointment Actions (Status update, cancellation, prescription)
async function updateAppointmentStatus(id, newStatus) {
  try {
    const res = await fetch(`/api/appointments/${id}/status`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: newStatus })
    });

    if (res.ok) {
      showToast(`Appointment status updated to ${newStatus}.`);
      if (currentUser?.role === 'DOCTOR') loadDoctorAppointments();
      if (currentUser?.role === 'ADMIN') loadAdminDashboard();
      if (currentUser?.role === 'PATIENT') loadPatientAppointments();
    } else {
      showToast('Failed to update status', 'error');
    }
  } catch (err) {
    showToast('Error updating appointment status', 'error');
  }
}

async function cancelAppointment(id) {
  if (!confirm('Are you sure you want to cancel this appointment?')) return;
  if (!currentUser) return;

  try {
    const res = await fetch(`/api/appointments/${id}/cancel?userId=${currentUser.id}`, {
      method: 'PUT'
    });

    if (res.ok) {
      showToast('Appointment successfully cancelled.');
      if (currentUser.role === 'PATIENT') loadPatientAppointments();
      else if (currentUser.role === 'DOCTOR') loadDoctorAppointments();
      else if (currentUser.role === 'ADMIN') loadAdminDashboard();
    } else {
      const err = await res.json();
      showToast(err.message || 'Failed to cancel appointment', 'error');
    }
  } catch (err) {
    showToast('Error cancelling appointment.', 'error');
  }
}

// Prescription Viewer Modal
function viewPrescription(appt) {
  document.getElementById('rxPatientName').innerText = appt.patientName || 'Patient';
  document.getElementById('rxDoctorName').innerText = appt.doctorName || 'Doctor';
  document.getElementById('rxDateSlot').innerText = `${appt.appointmentDate} (${appt.timeSlot})`;
  document.getElementById('rxSpecialty').innerText = appt.doctorSpecialization || 'Clinical Care';
  document.getElementById('rxSymptoms').innerText = appt.symptoms || 'None specified';
  document.getElementById('rxDoctorNotes').innerText = appt.doctorNotes || 'No additional doctor notes recorded.';
  document.getElementById('rxMedication').innerText = appt.prescription || 'No medication prescribed.';

  document.getElementById('prescriptionModal').classList.remove('hidden');
}

function closePrescriptionModal() {
  document.getElementById('prescriptionModal').classList.add('hidden');
}

// Doctor Add Prescription Modal
function openDoctorPrescribeModal(appt) {
  document.getElementById('prescribeApptId').value = appt.id;
  document.getElementById('prescribePatientDesc').innerText = `Patient: ${appt.patientName} &bull; Symptoms: "${appt.symptoms}"`;
  document.getElementById('prescribeNotes').value = appt.doctorNotes || '';
  document.getElementById('prescribeRx').value = appt.prescription || '';

  document.getElementById('doctorAddRxModal').classList.remove('hidden');
}

function closeDoctorAddRxModal() {
  document.getElementById('doctorAddRxModal').classList.add('hidden');
}

async function submitDoctorPrescription(e) {
  e.preventDefault();
  const id = document.getElementById('prescribeApptId').value;
  const doctorNotes = document.getElementById('prescribeNotes').value.trim();
  const prescription = document.getElementById('prescribeRx').value.trim();

  try {
    const res = await fetch(`/api/appointments/${id}/prescription`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ doctorNotes, prescription })
    });

    if (res.ok) {
      closeDoctorAddRxModal();
      showToast('Prescription saved and consultation marked COMPLETED!');
      loadDoctorAppointments();
    } else {
      showToast('Failed to save prescription', 'error');
    }
  } catch (err) {
    showToast('Error saving prescription', 'error');
  }
}

// Admin Add Doctor Modal
function openAddDoctorModal() {
  document.getElementById('addDoctorModal').classList.remove('hidden');
}

function closeAddDoctorModal() {
  document.getElementById('addDoctorModal').classList.add('hidden');
}

async function submitNewDoctor(e) {
  e.preventDefault();

  const payload = {
    name: document.getElementById('newDocName').value.trim(),
    email: document.getElementById('newDocEmail').value.trim(),
    specialization: document.getElementById('newDocSpecialization').value.trim(),
    qualification: document.getElementById('newDocQualification').value.trim(),
    experienceYears: parseInt(document.getElementById('newDocExperience').value),
    consultationFee: parseFloat(document.getElementById('newDocFee').value),
    availableDays: document.getElementById('newDocDays').value.trim(),
    timeSlots: document.getElementById('newDocSlots').value.trim(),
    bio: document.getElementById('newDocBio').value.trim(),
    imageUrl: document.getElementById('newDocImage').value.trim()
  };

  try {
    const res = await fetch('/api/doctors', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (res.ok) {
      closeAddDoctorModal();
      showToast('New doctor registered successfully!');
      loadSpecializations();
      loadDoctors();
      loadAdminDashboard();
    } else {
      const err = await res.json();
      showToast(err.message || 'Failed to add doctor', 'error');
    }
  } catch (err) {
    showToast('Error adding doctor.', 'error');
  }
}
