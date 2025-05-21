// 显示不同的标签页
function showTab(tabId) {
    // 更新菜单激活状态
    document.querySelectorAll('.profile-menu li').forEach(item => {
        item.classList.remove('active');
    });
    event.currentTarget.classList.add('active');

    // 显示对应的内容卡片
    document.querySelectorAll('.profile-card').forEach(card => {
        card.classList.remove('active');
    });
    document.getElementById(tabId).classList.add('active');
}

// 预览选择的头像
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

// 更新基本资料
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
            alert('个人资料更新成功');
        } else {
            const errorText = await response.text();
            console.error("服务器返回错误内容：", errorText);
            alert(errorText);
        }
    } catch (error) {
        console.error('更新错误:', error);
        alert(error.message);
    }
}

// 修改密码
async function changePassword() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    // 简单前端校验
    if (newPassword !== confirmPassword) {
        alert('两次输入的新密码不一致');
        return;
    }

    try {
        const response = await fetch('/user/profile/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        });

        const result = await response.json();

        if (response.ok) {
            alert(result.message || '密码修改成功，请重新登录');
            window.location.href = '/logout';
        } else {
            throw new Error(result.message || '密码修改失败');
        }
    } catch (error) {
        console.error('修改密码失败:', error);
        alert(error.message);
    }
}


// 上传头像
async function uploadAvatar() {
    const fileInput = document.getElementById('avatarInput');
    if (!fileInput.files || fileInput.files.length === 0) {
        alert('请先选择头像图片');
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
            alert('头像上传成功');
        } else {
            throw new Error('头像上传失败');
        }
    } catch (error) {
        console.error('上传头像错误:', error);
        alert(error.message);
    }
}