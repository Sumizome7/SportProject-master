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
    } else if (tabId === 'system-logs') {
        loadAuditLogs(); // 加载日志
    }

}

function loadUsers() {
    fetch('/admin/api/users')
        .then(res => res.json())
        .then(users => {
            const tbody = document.querySelector('#userTable tbody');
            tbody.innerHTML = '';
            users.forEach(user => {
                const formattedTime = user.createAt
                    ? new Date(user.createAt).toLocaleString()
                    : '无';

                tbody.innerHTML += `
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.userName}</td>
                        <td>${user.password}</td>
                        <td>${user.isAdmin ? '是' : '否'}</td>
                        <td>${formattedTime}</td>
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

    const formData = new FormData();
    formData.append("videoName", document.getElementById("formVideoName").value);
    formData.append("videoFile", document.getElementById("formVideoFile").files[0]);
    formData.append("coverFile", document.getElementById("formVideoCoverFile").files[0]);
    formData.append("category", document.getElementById("formCategory").value);

    const xhr = new XMLHttpRequest();
    const progressText = document.getElementById('uploadStatus');
    progressText.innerText = '准备上传...';
    progressText.style.display = 'block';

    xhr.open("POST", "/api/videos/upload");

    // ✅ 上传进度监听
    xhr.upload.onprogress = function (event) {
        if (event.lengthComputable) {
            const percent = Math.round((event.loaded / event.total) * 100);
            progressText.innerText = `上传中... ${percent}%`;

            // ✅ 更新进度条
            const progressContainer = document.getElementById('uploadProgressContainer');
            const progressBar = document.getElementById('uploadProgressBar');
            progressContainer.style.display = 'block';
            progressBar.style.width = percent + '%';
        }
    };

    // ✅ 成功后
    xhr.onload = function () {
        if (xhr.status === 200) {
            alert('上传成功！');
            closeVideoModal();
            loadVideos();
        } else {
            alert('上传失败：' + xhr.statusText);
        }
        progressText.style.display = 'none';
        document.getElementById('uploadProgressContainer').style.display = 'none';
    };

    // ✅ 错误处理
    xhr.onerror = function () {
        alert('上传失败，请检查网络');
        progressText.style.display = 'none';
        document.getElementById('uploadProgressContainer').style.display = 'none';
    };

    xhr.send(formData);
}

function openVideoModal() {
    document.getElementById('videoModalTitle').innerText = "添加视频";
    document.getElementById('formVideoId').value = "";
    document.getElementById('formVideoName').value = "";
    document.getElementById('formVideoFile').value = "";
    document.getElementById('formVideoFile').value = "";
    document.getElementById('formCategory').value = "running";

    // ✅ 隐藏进度提示
    document.getElementById('uploadStatus').style.display = 'none';
    document.getElementById('uploadProgressContainer').style.display = 'none';

    document.getElementById('videoModal').style.display = 'block';
}

function closeVideoModal() {
    document.getElementById('videoModal').style.display = 'none';
}

function editVideo(video) {
    document.getElementById('videoModalTitle').innerText = "编辑视频";
    document.getElementById('formVideoId').value = video.videoId;
    document.getElementById('formVideoName').value = video.videoName;
    document.getElementById('formCategory').value = video.category;

    // 设置封面预览
    const preview = document.getElementById('coverPreview');
    preview.src = video.videoCover;
    preview.style.display = 'block';

    // 清空文件输入框（防止旧文件残留）
    document.getElementById('formVideoFile').value = '';
    document.getElementById('formVideoCoverFile').value = '';

    // 替换表单提交事件为编辑函数
    const form = document.querySelector("#videoModal form");
    form.onsubmit = updateVideo;

    // ✅ 隐藏进度提示
    document.getElementById('uploadStatus').style.display = 'none';
    document.getElementById('uploadProgressContainer').style.display = 'none';

    document.getElementById('videoModal').style.display = 'block';
}

function updateVideo(event) {
    event.preventDefault();

    const videoId = document.getElementById("formVideoId").value;
    const formData = new FormData();
    formData.append("videoId", videoId);
    formData.append("videoName", document.getElementById("formVideoName").value);
    formData.append("category", document.getElementById("formCategory").value);

    const videoFile = document.getElementById("formVideoFile").files[0];
    const coverFile = document.getElementById("formVideoCoverFile").files[0];
    if (videoFile) formData.append("videoFile", videoFile);
    if (coverFile) formData.append("coverFile", coverFile);

    const xhr = new XMLHttpRequest();
    const progressText = document.getElementById('uploadStatus');
    progressText.innerText = '准备上传...';
    progressText.style.display = 'block';

    xhr.open("POST", "/api/videos/update");

    xhr.upload.onprogress = function (event) {
        if (event.lengthComputable) {
            const percent = Math.round((event.loaded / event.total) * 100);
            progressText.innerText = `上传中... ${percent}%`;

            // ✅ 更新进度条
            const progressContainer = document.getElementById('uploadProgressContainer');
            const progressBar = document.getElementById('uploadProgressBar');
            progressContainer.style.display = 'block';
            progressBar.style.width = percent + '%';
        }
    };

    xhr.onload = function () {
        if (xhr.status === 200) {
            alert('修改成功！');
            closeVideoModal();
            loadVideos();
        } else {
            alert('修改失败：' + xhr.statusText);
        }
        progressText.style.display = 'none';
        document.getElementById('uploadProgressContainer').style.display = 'none';
    };

    xhr.onerror = function () {
        alert('修改失败，请检查网络');
        progressText.style.display = 'none';
        document.getElementById('uploadProgressContainer').style.display = 'none';
    };

    xhr.send(formData);
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
        fetch(`/user/api/videos/${videoId}`, {
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

function loadAuditLogs() {
    fetch('/admin/api/users/logs')
        .then(res => res.json())
        .then(logs => {
            const tbody = document.querySelector('#logTable tbody');
            tbody.innerHTML = '';
            logs.forEach(log => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${log.logId}</td>
                    <td>${log.userId}</td>
                    <td>${log.actionType}</td>
                    <td>${log.targetId}</td>
                    <td>${new Date(log.createdAt).toLocaleString()}</td>
                `;
                tbody.appendChild(row);
            });
        })
        .catch(error => {
            console.error('日志加载失败:', error);
        });
}
