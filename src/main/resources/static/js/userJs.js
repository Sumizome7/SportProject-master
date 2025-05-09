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

async function submitVideoForm(event) {
    event.preventDefault();

    const formData = new FormData();
    formData.append("videoName", document.getElementById("formVideoName").value);
    formData.append("videoFile", document.getElementById("formVideoFile").files[0]);
    formData.append("coverFile", document.getElementById("formVideoCoverFile").files[0]);
    formData.append("category", document.getElementById("formCategory").value);

    try {
        const response = await fetch("/api/videos/upload", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error("上传失败，状态码：" + response.status);
        }

        alert("上传成功！");
        closeVideoModal();
        loadVideos(); // 重新加载视频列表
    } catch (error) {
        console.error("上传失败:", error);
        alert("上传失败，请检查网络或文件格式");
    }
}


function openVideoModal() {
    document.getElementById('videoModalTitle').innerText = "添加视频";
    document.getElementById('formVideoId').value = "";
    document.getElementById('formVideoName').value = "";
    document.getElementById('formVideoFile').value = "";
    document.getElementById('formVideoFile').value = "";
    document.getElementById('formCategory').value = "running";
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
                    option.selected = true;
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


