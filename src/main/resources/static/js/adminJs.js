document.addEventListener('DOMContentLoaded', () => {

});

function toggleDropdown(event) {
    event.stopPropagation();
    document.getElementById("userDropdown").style.display =
        document.getElementById("userDropdown").style.display === "block" ? "none" : "block";
}

document.addEventListener("click", () => {
    document.getElementById("userDropdown").style.display = "none";
});

function logout() {
    if (confirm('确定要注销吗')) {
        location.href = '/logout';
    }
}

function showAdminTab(element, tabId) {
    // 更新菜单激活状态
    document.querySelectorAll('.profile-menu li').forEach(item => {
        item.classList.remove('active');
    });
    element.classList.add('active');

    // 显示对应的内容卡片
    document.querySelectorAll('.profile-card').forEach(card => {
        card.classList.remove('active');
    });
    document.getElementById(tabId).classList.add('active');

    if (tabId === 'user-controller') {
        loadUsers();
    } else if (tabId === 'video-controller') {
        loadVideos();
    }
}

function loadUsers() {
    fetch('/admin/api/users')
        .then(res => res.json())
        .then(users => {
            const tbody = document.querySelector('#userTable tbody');
            tbody.innerHTML = '';
            users.forEach(user => {
                tbody.innerHTML += `
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.userName}</td>
                        <td>${user.password}</td>
                        <td>${user.isAdmin ? '是' : '否'}</td>
                        <td>${user.createAt}</td>
                        <td>${user.createAt}</td>
                        <td>${user.phone}</td>
                        <td>${user.email}</td>
                        <td>${user.gender}</td>
                        <td>
                            <button onclick='editUser(${JSON.stringify(user)})'>修改</button>
                            <button onclick='deleteUser(${user.userId})'>删除</button>
                        </td>
                    </tr>`;
            });
        });
}

function searchUserById() {
    const id = document.getElementById('searchIdInput').value;
    if (!id) {
        loadUsers(); // 没输入就加载全部
        return;
    }

    fetch(`/admin/api/users/${id}`)
        .then(res => {
            if (!res.ok) throw new Error('找不到该用户');
            return res.json();
        })
        .then(user => {
            const tbody = document.querySelector('#userTable tbody');
            tbody.innerHTML = `
                <tr>
                    <td>${user.userId}</td>
                    <td>${user.userName}</td>
                    <td>${user.password}</td>
                    <td>${user.isAdmin ? '是' : '否'}</td>
                    <td>${user.createAt}</td>
                    <td>${user.phone}</td>
                    <td>${user.email}</td>
                    <td>${user.gender}</td>
                    <td>
                        <button onclick='editUser(${JSON.stringify(user)})'>修改</button>
                        <button onclick='deleteUser(${user.userId})'>删除</button>
                    </td>
                </tr>`;
        }).catch(err => {
        alert(err.message);
        loadUsers();
    });
}

function openAddUserModal() {
    document.getElementById('modalTitle').innerText = "添加用户";
    document.getElementById('formUserId').value = "";
    document.getElementById('formUserName').value = "";
    document.getElementById('formPassword').value = "";
    document.getElementById('formIsAdmin').checked = false;
    document.getElementById('formPhone').value = "";
    document.getElementById('formEmail').value = "";
    document.getElementById('formGender').value = "";
    document.getElementById('formAvatar').value = "";
    document.getElementById('userModal').style.display = 'block';
}

function closeUserModal() {
    document.getElementById('userModal').style.display = 'none';
}

function editUser(user) {
    document.getElementById('modalTitle').innerText = "编辑用户";
    document.getElementById('formUserId').value = user.userId;
    document.getElementById('formUserName').value = user.userName;
    document.getElementById('formPassword').value = user.password;
    document.getElementById('formIsAdmin').checked = user.isAdmin;
    document.getElementById('formPhone').value = user.phone;
    document.getElementById('formEmail').value = user.email;
    document.getElementById('formGender').value = user.gender;
    document.getElementById('formAvatar').value = user.avatarUrl;
    document.getElementById('createAtDisplay').innerText = `创建时间: ${user.createAt}`;
    document.getElementById('userModal').style.display = 'block';
}

function submitUserForm(event) {
    event.preventDefault();

    const userIdValue = document.getElementById('formUserId')?.value;

    const user = {
        userId: userIdValue ? parseInt(userIdValue) : undefined,
        userName: document.getElementById('formUserName').value,
        password: document.getElementById('formPassword').value,
        isAdmin: document.getElementById('formIsAdmin').checked,
        phone: document.getElementById('formPhone').value,
        email: document.getElementById('formEmail').value,
        gender: document.getElementById('formGender').value === 'true', // 确保是 boolean
        avatarUrl: document.getElementById('formAvatar').value,
        createAt: new Date().toISOString()
    };

    console.log("提交数据：", JSON.stringify(user)); // 👈调试

    const method = user.userId ? 'PUT' : 'POST';

    fetch('/admin/api/users', {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(user)
    }).then(response => {
        if (!response.ok) {
            return response.text().then(text => { throw new Error(text) });
        }
        closeUserModal();
        loadUsers();
    }).catch(error => {
        console.error("提交失败：", error.message);
    });
}


function deleteUser(id) {
    if (confirm("确定删除该用户？")) {
        fetch(`/admin/api/users/${id}`, { method: 'DELETE' })
            .then(() => loadUsers());
    }
}

function changePwd(userId) {
    const newPwd = prompt("请输入新密码：");
    if (!newPwd) return;

    fetch(`/admin/api/users/${userId}/password`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ password: newPwd })
    }).then(res => {
        if (res.ok) {
            alert("密码修改成功");
        } else {
            alert("修改失败");
        }
    });
}


function loadVideos() {
    fetch('/admin/api/videos')
        .then(res => res.json())
        .then(data => {
            const tbody = document.querySelector('#videoTable tbody');
            tbody.innerHTML = '';
            data.forEach(video => {
                const row = `<tr>
                    <td>${video.videoId}</td>
                    <td>${video.videoName}</td>
                    <td><img src="${video.videoCover}" width="50"/></td>
                    <td>${video.category}</td>
                    <td>
                        <button onclick='editVideo(${JSON.stringify(video)})'>编辑</button>
                        <button onclick='deleteVideo(${video.videoId})'>删除</button>
                    </td>
                </tr>`;
                tbody.innerHTML += row;
            });
        });
}

function submitVideoForm(event) {
    event.preventDefault();
    const video = {
        videoId: document.getElementById('formVideoId').value || undefined,
        videoName: document.getElementById('formVideoName').value,
        videoCover: document.getElementById('formVideoCover').value,
        videoUrl: document.getElementById('formVideoUrl').value,
        userId: document.getElementById('formUserId').value,
        category: document.getElementById('formCategory').value,
        uploadTime: new Date().toISOString()
    };
    const method = video.videoId ? 'PUT' : 'POST';

    console.log(video);

    fetch('/admin/api/videos', {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(video)
    }).then(() => {
        closeVideoModal();
        loadVideos();
    });
}

function openVideoModal() {
    document.getElementById('videoModalTitle').innerText = "添加视频";
    document.getElementById('formVideoId').value = "";
    document.getElementById('formVideoName').value = "";
    document.getElementById('formVideoCover').value = "";
    document.getElementById('formVideoUrl').value = "";
    document.getElementById('formUserId').value = "";
    document.getElementById('formCategory').value = "";

    loadUserOptions();

    document.getElementById('videoModal').style.display = 'block';
}

function closeVideoModal() {
    document.getElementById('videoModal').style.display = 'none';
}

function editVideo(video) {
    document.getElementById('videoModalTitle').innerText = "编辑视频";
    document.getElementById('formVideoId').value = video.videoId;
    document.getElementById('formVideoName').value = video.videoName;
    document.getElementById('formVideoCover').value = video.videoCover;
    document.getElementById('formVideoUrl').value = video.videoUrl;
    document.getElementById('formCategory').value = video.category;

    // 先清空用户下拉框，再重新加载并设置选中
    document.getElementById('formUserId').innerHTML = '<option value="">请选择用户</option>';

    fetch('/admin/api/users')
        .then(response => response.json())
        .then(users => {
            const select = document.getElementById('formUserId');
            users.forEach(user => {
                const option = document.createElement('option');
                option.value = user.userId;
                option.text = user.username;
                if (user.userId === video.userId) {
                    option.selected = true; // 选中当前的用户
                }
                select.appendChild(option);
            });
        })
        .catch(error => {
            console.error('加载用户列表失败:', error);
        });

    document.getElementById('videoModal').style.display = 'block';
}

function searchVideos() {
    const keyword = document.getElementById('videoSearchInput').value.toLowerCase();
    const rows = document.querySelectorAll('#videoTable tbody tr');

    rows.forEach(row => {
        const name = row.cells[1].innerText.toLowerCase();
        const category = row.cells[3].innerText.toLowerCase();
        if (name.includes(keyword) || category.includes(keyword)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
}

function deleteVideo(videoId) {
    if (confirm('确定要删除该视频吗？')) {
        fetch(`/admin/api/videos/${videoId}`, {
            method: 'DELETE'
        }).then(response => {
            if (!response.ok) {
                return response.text().then(text => { throw new Error(text) });
            }
            loadVideos(); // 重新加载视频列表
        }).catch(error => {
            alert('删除失败：' + error.message);
        });
    }
}

function uploadVideoFile(file) {
    const formData = new FormData();
    formData.append('file', file);

    fetch('/admin/api/upload/video', {
        method: 'POST',
        body: formData
    })
        .then(res => res.text())
        .then(url => {
            document.getElementById('formVideoUrl').value = url;
        })
        .catch(err => {
            console.error('上传失败:', err);
            alert('视频上传失败，请稍后重试');
        });
}

function loadUserOptions() {
    fetch('/admin/api/users')
        .then(res => res.json())
        .then(users => {
            const select = document.getElementById('formUserId');
            if (!select) {
                console.error("formUserId 下拉框未找到！");
                return;
            }
            select.innerHTML = '<option value="">选择用户</option>';
            users.forEach(user => {
                const option = document.createElement('option');
                option.value = user.userId;
                option.textContent = user.userName || "(无用户名)";
                console.log(`添加选项：value=${option.value}, text=${option.textContent}`);
                select.appendChild(option);
            });
        })
        .catch(err => console.error('加载用户列表失败:', err));
}