document.addEventListener('DOMContentLoaded', () => {
    handleUserDropdown();
    showSection('user-home');
    const submenu = document.getElementById('videoSubmenu');
    if (submenu) submenu.style.display = 'none';
    initVideoPlayer();
});

// 用户下拉菜单显示与隐藏
function handleUserDropdown() {
    document.querySelector('.user-panel').addEventListener('click', (event) => {
        event.stopPropagation();
        const dropdown = event.currentTarget.querySelector('.dropdown-content');
        dropdown.style.display = (dropdown.style.display === 'flex') ? 'none' : 'flex';
    });

    document.addEventListener('click', () => {
        const dropdown = document.getElementById('userDropdown');
        if (dropdown) dropdown.style.display = 'none';
    });
}

//播放器初始化
function initVideoPlayer() {
    if (document.getElementById('video-player')) {
        try {
            window.player = videojs('video-player', {
                controls: true,
                preload: 'auto',
                fluid: false
            });
            console.log('播放器初始化成功');
        } catch (e) {
            console.error('VideoJS 初始化失败:', e);
        }
    }
}

//网页侧边栏显示
function showSection(sectionId, event) {
    document.querySelectorAll('.menu-item').forEach(item => {
        item.classList.remove('active');
    });

    if (event && event.currentTarget) {
        event.currentTarget.classList.add('active');
    } else {
        // 默认激活“系统首页”菜单项
        const defaultMenuItem = document.querySelector(`.menu-item[onclick*="${sectionId}"]`);
        if (defaultMenuItem) {
            defaultMenuItem.classList.add('active');
        }
    }

    document.querySelectorAll('.tab-section').forEach(card => {
        card.classList.remove('active');
    });

    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.classList.add('active');
    } else {
        console.warn('未找到 sectionId 对应的元素:', sectionId);
    }

    if (document.getElementById("video-manage")) {
        loadVideos(); // 初始加载视频列表
    }

    if (sectionId === 'video-share') {
        loadSharedVideos(); // 加载自己上传的视频（可以判断哪些是共享状态）
    }
    if (sectionId === 'share-management') {
        loadUserSharedStatus(); // 加载共享状态页面
    }

    const submenu = document.getElementById('videoSubmenu');
    if (submenu) submenu.style.display = 'none';
}

let hasLoadedDefaultCategory = false;

function showVideoView(event) {
    showSection('video-view', event); // 切换内容区和高亮主菜单

    const submenu = document.getElementById('videoSubmenu');
    submenu.style.display = 'block';

    if (!hasLoadedDefaultCategory) {
        const defaultCategoryItem = submenu.querySelector('[data-category="all"]');
        filterByCategory({ currentTarget: defaultCategoryItem });
        hasLoadedDefaultCategory = true;
    }
}

function filterByCategory(event) {
    const target = event.currentTarget;
    const category = target.getAttribute('data-category');
    console.log('当前选择分类:', category);

    document.querySelectorAll('.submenu-item').forEach(item => {
        item.classList.remove('active');
    });
    target.classList.add('active');

    fetchCategoryVideos(category);
}

// 拉取某个分类的视频
async function fetchCategoryVideos(category) {
    try {
        const response = await fetch(`/api/videos/category/${category}`);
        const videos = await response.json();
        renderVideoList(videos);
    } catch (error) {
        console.error('视频加载失败:', error);
    }
}

// 渲染视频列表
function renderVideoList(videos) {
    const container = document.getElementById('video-view');
    container.innerHTML = videos.map(video => `
        <div class="video-card" data-category="${video.category}">
            <div class="video-thumbnail" 
                 data-video-id="${video.id}">
                <img src="${video.cover}" 
                     alt="${video.title}" 
                     loading="lazy">
                <div class="play-overlay">▶</div>
            </div>
            <h3 class="video-title">${video.title}</h3>
            <p class="upload-time">${new Date(video.uploadTime).toLocaleDateString()}</p>
        </div>
    `).join('');

    document.querySelectorAll('.video-card').forEach(card => {
        card.addEventListener('click', () => {
            const thumbnail = card.querySelector('.video-thumbnail');
            const videoId = thumbnail.dataset.videoId;
            window.location.href = `/api/videos/play/${videoId}`;
        });
    });
}


// 注销按钮绑定方法
function handleLogout() {
    if (confirm('确定要注销吗')) {
        location.href = '/logout';
    }
}

function loadVideos() {
    fetch('/user/api/videos')
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

//封面提取
document.getElementById('coverForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const formData = new FormData(e.target);
    const statusEl = document.getElementById('coverStatus');
    statusEl.innerText = '正在生成封面，请稍候...';

    try {
        const response = await fetch('/api/cover/generate', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error('请求失败');

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'cover.jpg';
        document.body.appendChild(a);
        a.click();
        a.remove();
        statusEl.innerText = '封面已成功生成并下载到本地';
    } catch (error) {
        statusEl.innerText = '封面生成失败，请重试';
        console.error('封面生成错误:', error);
    }
});

document.getElementById('highlightForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const formData = new FormData(this);

    const status = document.getElementById("processStatus");
    status.textContent = "⏳ 正在上传并处理，请稍候...";

    try {
        const res = await fetch('/api/highlight/generate', {
            method: 'POST',
            body: formData
        });

        const contentType = res.headers.get("Content-Type");

        if (res.ok && contentType === "video/mp4") {
            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = "highlight_edited_video.mp4";
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
            status.textContent = "✅ 成功生成并下载精彩瞬间！";
        } else {
            const errorText = await res.text(); // 只读一次！
            status.textContent = "❌ 出错了：" + errorText;
        }


    } catch (err) {
        console.error(err);
        status.textContent = "❌ 请求失败：" + err.message;
    }
});


// 从后端加载带有“分享”标识的视频（你后端已经处理了 /api/videos/shared 接口）
async function loadSharedVideos() {
    try {
        const response = await fetch('/api/videos/shared');
        const videos = await response.json();
        renderSharedVideoList(videos);
    } catch (error) {
        console.error('获取共享视频失败:', error);
    }
}

// 渲染共享视频卡片
function renderSharedVideoList(videos) {
    const container = document.getElementById('sharedVideoList');
    container.innerHTML = videos.map(video => `
        <div class="shared-video-card" data-title="${video.videoName.toLowerCase()}"
             onclick="window.location.href='/api/videos/play/${video.videoId}'">
            <img src="${video.videoCover}" alt="${video.videoName}" style="width: 200px;">
            <h4>${video.videoName}</h4>
            <p>分类：${video.category}</p>
        </div>
    `).join('');
}


// 搜索共享视频
function searchSharedVideos() {
    const keyword = document.getElementById('sharedVideoSearchInput').value.toLowerCase();
    const cards = document.querySelectorAll('#sharedVideoList .shared-video-card');

    cards.forEach(card => {
        const title = card.getAttribute('data-title');
        card.style.display = title.includes(keyword) ? 'block' : 'none';
    });
}


async function loadUserSharedStatus() {
    try {
        const response = await fetch('/api/videos/sharedMine');
        if (!response.ok) {
            throw new Error(`服务器返回状态 ${response.status}`);
        }
        const sharedList = await response.json();
        renderUserSharedTable(sharedList);
    } catch (error) {
        console.error('获取用户共享状态失败：', error);
        const container = document.getElementById('userSharedTable').querySelector('tbody');
        container.innerHTML = `<tr><td colspan="5" style="color:red;">加载失败，请稍后重试。</td></tr>`;
    }
}

function renderUserSharedTable(sharedList) {
    const tbody = document.getElementById('userSharedTable').querySelector('tbody');
    if (!sharedList.length) {
        tbody.innerHTML = `<tr><td colspan="6">你还没有上传视频或设置共享状态。</td></tr>`;
        return;
    }

    tbody.innerHTML = sharedList.map(item => {


        const isShared = item.shared === true || item.shared === 1 || item.shared === 'true' || item.shared === '1';
        console.log('共享字段实际值:', isShared); // <-- 关键调试
        return `
            <tr>
                <td>${item.videoId}</td>
                <td>${item.videoName || '无'}</td>
                <td>${isShared ? '是' : '否'}</td>
                <td>${item.sharedAt ? new Date(item.sharedAt).toLocaleString() : '无'}</td>
                <td>用户ID ${item.sharedBy || '无'}</td>
                <td>
                    <button onclick="toggleShared(${item.videoId}, ${isShared})">
                        修改
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

async function toggleShared(videoId, currentStatus) {
    const confirmed = confirm(`确定要将共享状态修改为 ${!currentStatus ? '“是”' : '“否”'} 吗？`);
    if (!confirmed) return;

    try {
        const response = await fetch('/api/videos/shared/update', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                videoId: videoId,
                isShared: !currentStatus
            })
        });

        const result = await response.text();
        if (response.ok) {
            alert('更新成功');
            loadUserSharedStatus(); // 刷新表格
        } else {
            alert('更新失败：' + result);
        }
    } catch (error) {
        alert('请求异常：' + error.message);
    }
}

