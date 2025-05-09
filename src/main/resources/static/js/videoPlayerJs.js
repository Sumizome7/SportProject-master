const player = videojs('video-player');
player.ready(() => {
    console.log('Video.js 初始化完成');
});

document.addEventListener('DOMContentLoaded', function () {
    const video = player.el().getElementsByTagName('video')[0];
    const generateBtn = document.getElementById('generate-highlights-btn');
    const container = document.getElementById('screenshot-container');

    generateBtn.addEventListener('click', async () => {
        container.innerHTML = ''; // 清空旧截图
        console.log('点击按钮：开始生成精彩瞬间');

        if (video.readyState < 1) {
            console.log('视频元数据未加载，等待 loadedmetadata...');
            await new Promise(resolve => {
                video.addEventListener('loadedmetadata', () => {
                    console.log('视频元数据加载完成');
                    resolve();
                }, { once: true });
            });
        }

        const moments = [
            { time: 12, label: '起跑' },
            { time: 27, label: '冲线' }
        ];

        // 静音避免跳帧时声音干扰
        const originalMuted = video.muted;
        video.muted = true;

        console.log('准备截图...');
        for (const moment of moments) {
            console.log('开始处理时间点：', moment.time);
            await captureFrameAt(video, moment.time, moment.label);
            console.log('已完成时间点：', moment.time);
        }

        video.muted = originalMuted;

        alert('精彩瞬间截图已生成！');
    });

    function captureFrameAt(video, time, label) {
        return new Promise((resolve) => {
            const handler = () => {
                console.log('触发 seeked，正在截图:', time, label);
                const canvas = document.createElement('canvas');
                canvas.width = video.videoWidth || 640;
                canvas.height = video.videoHeight || 360;

                const ctx = canvas.getContext('2d');
                ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

                // 添加文字
                ctx.fillStyle = 'rgba(255, 0, 0, 0.7)';
                ctx.font = 'bold 48px sans-serif';
                ctx.fillText(label, 50, 80);

                const img = document.createElement('img');
                img.src = canvas.toDataURL('image/png');
                img.alt = label + ' 截图';
                img.style.maxWidth = '480px';
                img.style.margin = '10px';

                container.appendChild(img);

                video.removeEventListener('seeked', handler);
                resolve();
            };

            video.addEventListener('seeked', handler);
            console.log('设置 currentTime:', time);
            video.currentTime = time;
        });
    }
});
