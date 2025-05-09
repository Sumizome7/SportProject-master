// ????????
function showTab(tabId) {
    // ????????
    document.querySelectorAll('.profile-menu li').forEach(item => {
        item.classList.remove('active');
    });
    event.currentTarget.classList.add('active');

    // ?????????
    document.querySelectorAll('.profile-card').forEach(card => {
        card.classList.remove('active');
    });
    document.getElementById(tabId).classList.add('active');
}

// ???????
function previewAvatar() {
    const file = document.getElementById('avatarInput').files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById('avatarPreview').src = e.target.result;
        }
        reader.readAsDataURL(file);
    }
}

// ??????
async function updateBasicInfo() {
    const userId = document.getElementById('userId').value;
    const userName = document.getElementById('userName').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;

    try {
        const response = await fetch('/user/profile/updateProfile', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({userId, userName, email, phone})
        });

        if (response.ok) {
            alert('????????');
        } else {
            const errorText = await response.text();
            console.error("??????????", errorText);
            alert(errorText);
        }
    } catch (error) {
        console.error('????:', error);
        alert(error.message);
    }
}

// ????
async function changePassword() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (newPassword !== confirmPassword) {
        alert('???????????');
        return;
    }

    try {
        const response = await fetch('/api/user/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({currentPassword, newPassword})
        });

        if (response.ok) {
            alert('????????????');
            window.location.href = '/logout';
        } else {
            const error = await response.json();
            throw new Error(error.message || '??????');
        }
    } catch (error) {
        console.error('??????:', error);
        alert(error.message);
    }
}

// ????
async function uploadAvatar() {
    const fileInput = document.getElementById('avatarInput');
    if (!fileInput.files || fileInput.files.length === 0) {
        alert('????????');
        return;
    }

    const formData = new FormData();
    formData.append('avatar', fileInput.files[0]);

    try {
        const response = await fetch('/api/user/avatar', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const result = await response.json();
            document.getElementById('avatarPreview').src = result.avatarUrl;
            alert('??????');
        } else {
            throw new Error('??????');
        }
    } catch (error) {
        console.error('??????:', error);
        alert(error.message);
    }
}