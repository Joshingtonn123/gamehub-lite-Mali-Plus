import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const PORT = 3000;

// Resolve logo path
let logoBuffer = null;
let logoMime = 'image/webp';
const possibleLogoPaths = [
  { path: path.join(__dirname, 'patches/binary_replacements/res/mipmap-xxxhdpi/ic_launcher.webp'), mime: 'image/webp' },
  { path: path.join(__dirname, 'patches/binary_replacements/res/mipmap-xxhdpi/ic_launcher.webp'), mime: 'image/webp' },
  { path: path.join(__dirname, 'src/assets/images/gamehub_mali_user_logo_1789756065331.jpg'), mime: 'image/jpeg' }
];

for (const p of possibleLogoPaths) {
  if (fs.existsSync(p.path)) {
    logoBuffer = fs.readFileSync(p.path);
    logoMime = p.mime;
    break;
  }
}

const HTML_CONTENT = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gamehub Mali+ (1.0 Alpha)</title>
  <link rel="icon" type="image/png" href="/logo.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">
  <style>
    :root {
      --bg-base: #060813;
      --bg-surface: #0c1022;
      --bg-card: #121832;
      --bg-card-hover: #182042;
      --border: #1f2a52;
      --border-accent: #334480;
      --neon-magenta: #e028b0;
      --neon-blue: #00d2ff;
      --neon-purple: #8f00ff;
      --text-main: #f0f4fc;
      --text-dim: #8b99bb;
      --text-muted: #556688;
      --accent-grad: linear-gradient(135deg, #e028b0 0%, #7928ca 50%, #00d2ff 100%);
      --card-radius: 14px;
    }

    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }

    body {
      background-color: var(--bg-base);
      color: var(--text-main);
      font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif;
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      line-height: 1.5;
      overflow-x: hidden;
    }

    header {
      background: rgba(12, 16, 34, 0.85);
      backdrop-filter: blur(12px);
      border-bottom: 1px solid var(--border);
      position: sticky;
      top: 0;
      z-index: 50;
      padding: 14px 24px;
    }

    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .brand {
      display: flex;
      align-items: center;
      gap: 14px;
      text-decoration: none;
    }

    .brand-logo {
      width: 42px;
      height: 42px;
      border-radius: 10px;
      box-shadow: 0 0 16px rgba(0, 210, 255, 0.35);
      object-fit: cover;
    }

    .brand-text h1 {
      font-size: 18px;
      font-weight: 800;
      letter-spacing: -0.02em;
      background: var(--accent-grad);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .badge-pill {
      font-size: 10px;
      text-transform: uppercase;
      font-weight: 700;
      letter-spacing: 0.05em;
      background: rgba(224, 40, 176, 0.18);
      color: #ff68d6;
      border: 1px solid rgba(224, 40, 176, 0.4);
      padding: 2px 8px;
      border-radius: 999px;
      vertical-align: middle;
      -webkit-text-fill-color: initial;
    }

    .brand-sub {
      font-size: 12px;
      color: var(--text-dim);
    }

    .status-pill {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      background: rgba(0, 210, 255, 0.08);
      border: 1px solid rgba(0, 210, 255, 0.25);
      color: var(--neon-blue);
      padding: 6px 14px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;
    }

    .status-dot {
      width: 8px;
      height: 8px;
      background: #00ff88;
      border-radius: 50%;
      box-shadow: 0 0 8px #00ff88;
      animation: pulse 2s infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; transform: scale(1); }
      50% { opacity: 0.5; transform: scale(0.85); }
    }

    main {
      max-width: 1200px;
      margin: 0 auto;
      padding: 28px 24px 60px;
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 28px;
    }

    .hero-banner {
      background: linear-gradient(135deg, rgba(224, 40, 176, 0.08) 0%, rgba(0, 210, 255, 0.08) 100%), var(--bg-surface);
      border: 1px solid var(--border-accent);
      border-radius: var(--card-radius);
      padding: 28px;
      display: grid;
      grid-template-columns: auto 1fr auto;
      align-items: center;
      gap: 24px;
    }

    .hero-logo-large {
      width: 84px;
      height: 84px;
      border-radius: 18px;
      box-shadow: 0 0 28px rgba(224, 40, 176, 0.4), 0 0 32px rgba(0, 210, 255, 0.2);
    }

    .hero-text h2 {
      font-size: 24px;
      font-weight: 700;
      margin-bottom: 6px;
      color: #fff;
    }

    .hero-text p {
      font-size: 14px;
      color: var(--text-dim);
      max-width: 600px;
    }

    .quick-stats {
      display: flex;
      gap: 16px;
    }

    .stat-box {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 12px 18px;
      text-align: center;
    }

    .stat-val {
      font-size: 20px;
      font-weight: 800;
      color: var(--neon-blue);
      font-family: 'JetBrains Mono', monospace;
    }

    .stat-label {
      font-size: 11px;
      color: var(--text-muted);
      text-transform: uppercase;
      font-weight: 600;
      margin-top: 2px;
    }

    .grid-2 {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
      gap: 24px;
    }

    @media (max-width: 640px) {
      .grid-2 {
        grid-template-columns: 1fr;
      }
      .hero-banner {
        grid-template-columns: 1fr;
        text-align: center;
      }
      .quick-stats {
        justify-content: center;
      }
    }

    .card {
      background: var(--bg-surface);
      border: 1px solid var(--border);
      border-radius: var(--card-radius);
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid var(--border);
      padding-bottom: 14px;
    }

    .card-title {
      font-size: 17px;
      font-weight: 700;
      display: flex;
      align-items: center;
      gap: 10px;
      color: #fff;
    }

    .card-title-icon {
      width: 28px;
      height: 28px;
      border-radius: 7px;
      background: rgba(0, 210, 255, 0.12);
      color: var(--neon-blue);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: bold;
    }

    .icon-pink {
      background: rgba(224, 40, 176, 0.12);
      color: var(--neon-magenta);
    }

    .control-row {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .control-label-row {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
    }

    .control-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-main);
    }

    .control-val {
      font-family: 'JetBrains Mono', monospace;
      font-size: 13px;
      font-weight: 700;
      color: var(--neon-blue);
    }

    .control-desc {
      font-size: 12px;
      color: var(--text-dim);
    }

    .multiplier-buttons {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 10px;
    }

    .btn-mult {
      background: var(--bg-card);
      border: 1px solid var(--border);
      color: var(--text-main);
      padding: 12px 14px;
      border-radius: 10px;
      font-weight: 700;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.2s ease;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;
    }

    .btn-mult span {
      font-size: 10px;
      font-weight: 500;
      color: var(--text-dim);
    }

    .btn-mult:hover {
      background: var(--bg-card-hover);
      border-color: var(--border-accent);
    }

    .btn-mult.active {
      background: linear-gradient(135deg, rgba(224, 40, 176, 0.25) 0%, rgba(0, 210, 255, 0.25) 100%);
      border: 1px solid var(--neon-blue);
      color: #fff;
      box-shadow: 0 0 14px rgba(0, 210, 255, 0.3);
    }

    .btn-mult.active span {
      color: var(--neon-blue);
    }

    .toggle-row {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 14px 16px;
      cursor: pointer;
      user-select: none;
    }

    .toggle-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .switch {
      position: relative;
      display: inline-block;
      width: 44px;
      height: 24px;
    }

    .switch input {
      opacity: 0;
      width: 0;
      height: 0;
    }

    .slider {
      position: absolute;
      cursor: pointer;
      top: 0; left: 0; right: 0; bottom: 0;
      background-color: #242c4c;
      transition: .3s;
      border-radius: 24px;
    }

    .slider:before {
      position: absolute;
      content: "";
      height: 18px;
      width: 18px;
      left: 3px;
      bottom: 3px;
      background-color: white;
      transition: .3s;
      border-radius: 50%;
    }

    input:checked + .slider {
      background: linear-gradient(90deg, var(--neon-magenta), var(--neon-blue));
      box-shadow: 0 0 10px rgba(0, 210, 255, 0.4);
    }

    input:checked + .slider:before {
      transform: translateX(20px);
    }

    .canvas-container {
      background: #04060d;
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 14px;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    canvas {
      width: 100%;
      height: 140px;
      background: #080b18;
      border-radius: 8px;
      display: block;
    }

    .canvas-legend {
      display: flex;
      justify-content: center;
      gap: 20px;
      font-size: 11px;
      color: var(--text-dim);
    }

    .legend-item {
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .legend-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
    }

    .preset-pill {
      background: var(--bg-card);
      border: 1px solid var(--border);
      color: var(--text-main);
      padding: 8px 14px;
      border-radius: 8px;
      font-size: 12px;
      font-weight: 600;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: all 0.15s ease;
    }

    .preset-pill:hover {
      border-color: var(--neon-magenta);
      color: #fff;
    }

    .preset-pill.active {
      border-color: var(--neon-magenta);
      background: rgba(224, 40, 176, 0.15);
      color: #ff76d9;
    }

    .presets-row {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .variant-list {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .variant-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 10px;
      padding: 12px 16px;
    }

    .variant-left {
      display: flex;
      align-items: center;
      gap: 14px;
    }

    .variant-icon {
      font-family: 'JetBrains Mono', monospace;
      font-size: 11px;
      font-weight: 700;
      padding: 4px 8px;
      background: rgba(0, 210, 255, 0.1);
      color: var(--neon-blue);
      border-radius: 6px;
      border: 1px solid rgba(0, 210, 255, 0.3);
    }

    .variant-title {
      font-size: 13px;
      font-weight: 600;
    }

    .variant-desc {
      font-size: 11px;
      color: var(--text-dim);
    }

    .badge-apk {
      font-size: 11px;
      font-weight: 600;
      color: #a0aec0;
      background: rgba(255, 255, 255, 0.05);
      padding: 4px 10px;
      border-radius: 6px;
      border: 1px solid rgba(255, 255, 255, 0.1);
    }

    footer {
      margin-top: auto;
      border-top: 1px solid var(--border);
      padding: 20px 24px;
      text-align: center;
      color: var(--text-muted);
      font-size: 12px;
      background: var(--bg-surface);
    }
  </style>
</head>
<body>

  <header>
    <div class="nav-container">
      <a href="/" class="brand">
        <img src="/logo.png" alt="Gamehub Mali+ Logo" class="brand-logo" onerror="this.style.display='none'">
        <div class="brand-text">
          <h1>Gamehub Mali+ <span class="badge-pill">v1.0 Alpha</span></h1>
          <div class="brand-sub">Mali GPU Edition &bull; Frame Generation Suite</div>
        </div>
      </a>
      <div class="status-pill">
        <span class="status-dot"></span>
        <span>Mali Optimizer Active</span>
      </div>
    </div>
  </header>

  <main>
    <section class="hero-banner">
      <img src="/logo.png" alt="Gamehub Mali+ Icon" class="hero-logo-large" onerror="this.style.display='none'">
      <div class="hero-text">
        <h2>Optimized GameHub Edition for Mali &amp; Dimensity</h2>
        <p>
          Configured specifically for Arm Mali &amp; Immortalis GPUs. Featuring 2x, 3x, and 4x Frame Generation, 
          Low-FPS Anti-Lag (Anti-Stall) queue management, and hardware profile spoofing variants.
        </p>
      </div>
      <div class="quick-stats">
        <div class="stat-box">
          <div class="stat-val" id="stat-multiplier">3x</div>
          <div class="stat-label">Frame Multiplier</div>
        </div>
        <div class="stat-box">
          <div class="stat-val" id="stat-fps">120 FPS</div>
          <div class="stat-label">Interpolated Output</div>
        </div>
      </div>
    </section>

    <div class="grid-2">
      <!-- Frame Gen Module -->
      <section class="card">
        <div class="card-header">
          <div class="card-title">
            <div class="card-title-icon icon-pink">&#9889;</div>
            <span>Frame Generation Controls</span>
          </div>
          <span class="badge-pill">gamescope.control</span>
        </div>

        <div class="control-row">
          <div class="control-label-row">
            <span class="control-title">Interpolation Multiplier</span>
            <span class="control-val" id="lbl-mult">3x (High)</span>
          </div>
          <div class="multiplier-buttons">
            <button type="button" class="btn-mult" onclick="setMultiplier(2)">
              2x
              <span>Balanced</span>
            </button>
            <button type="button" class="btn-mult active" onclick="setMultiplier(3)">
              3x
              <span>High</span>
            </button>
            <button type="button" class="btn-mult" onclick="setMultiplier(4)">
              4x
              <span>Ultra</span>
            </button>
          </div>
        </div>

        <div class="toggle-row" onclick="toggleAntiLag()">
          <div class="toggle-info">
            <span class="control-title">Low-FPS Anti-Lag (Anti-Stall)</span>
            <span class="control-desc">Eliminates slow-motion and input backlog when native FPS drops below 30.</span>
          </div>
          <label class="switch" onclick="event.stopPropagation()">
            <input type="checkbox" id="chk-antilag" checked onchange="onAntiLagChange(this.checked)">
            <span class="slider"></span>
          </label>
        </div>

        <!-- Real-time frame pacing simulator canvas -->
        <div class="canvas-container">
          <div class="control-label-row">
            <span class="control-title" style="font-size:12px;">Real-Time Frame Pacing Visualizer</span>
            <span class="control-val" id="sim-status" style="font-size:11px;">Simulating 40 FPS -> 120 FPS</span>
          </div>
          <canvas id="pacingCanvas" width="500" height="140"></canvas>
          <div class="canvas-legend">
            <div class="legend-item"><div class="legend-dot" style="background:#00d2ff"></div> Native Frame</div>
            <div class="legend-item"><div class="legend-dot" style="background:#e028b0"></div> Generated Frame</div>
            <div class="legend-item"><div class="legend-dot" style="background:#00ff88"></div> Anti-Lag Flush</div>
          </div>
        </div>
      </section>

      <!-- Mali GPU Tuning Suite -->
      <section class="card">
        <div class="card-header">
          <div class="card-title">
            <div class="card-title-icon">&#9881;</div>
            <span>Mali GPU Hardware Tuning</span>
          </div>
          <span class="badge-pill">Vulkan / Box64</span>
        </div>

        <div class="control-row">
          <div class="control-label-row">
            <span class="control-title">Target GPU Profile</span>
            <span class="control-val" id="lbl-profile">Dimensity 9300+ (Immortalis)</span>
          </div>
          <div class="presets-row">
            <button class="preset-pill active" onclick="setProfile(this, 'Immortalis G720/G925')">Dimensity 9300/9400</button>
            <button class="preset-pill" onclick="setProfile(this, 'Mali-G615')">Dimensity 8300</button>
            <button class="preset-pill" onclick="setProfile(this, 'Mali-G77/G78')">Dimensity 1200</button>
            <button class="preset-pill" onclick="setProfile(this, 'Exynos Xclipse/Mali')">Exynos Mali</button>
          </div>
        </div>

        <div class="toggle-row" onclick="toggleOption('chk-dxvk')">
          <div class="toggle-info">
            <span class="control-title">DXVK Async Pipeline Compilation</span>
            <span class="control-desc">Eliminates tile-based shader compile stuttering on Mali.</span>
          </div>
          <label class="switch" onclick="event.stopPropagation()">
            <input type="checkbox" id="chk-dxvk" checked>
            <span class="slider"></span>
          </label>
        </div>

        <div class="toggle-row" onclick="toggleOption('chk-tess')">
          <div class="toggle-info">
            <span class="control-title">D3D11 Tessellation Factor Limiter (16x)</span>
            <span class="control-desc">Prevents vertex pipeline stalls and crashes in UE4/UE5 titles.</span>
          </div>
          <label class="switch" onclick="event.stopPropagation()">
            <input type="checkbox" id="chk-tess" checked>
            <span class="slider"></span>
          </label>
        </div>

        <div class="toggle-row" onclick="toggleOption('chk-bigcore')">
          <div class="toggle-info">
            <span class="control-title">Box64 BigCore Affinity &amp; FastMath</span>
            <span class="control-desc">Locks dynarec threads to Cortex-X &amp; Cortex-A big cores.</span>
          </div>
          <label class="switch" onclick="event.stopPropagation()">
            <input type="checkbox" id="chk-bigcore" checked>
            <span class="slider"></span>
          </label>
        </div>
      </section>
    </div>

    <!-- Release Variants -->
    <section class="card">
      <div class="card-header">
        <div class="card-title">
          <div class="card-title-icon icon-pink">&#128230;</div>
          <span>Package Variants &amp; OEM Throttling Bypass</span>
        </div>
        <span class="badge-pill">GitHub Actions Release</span>
      </div>
      <p class="control-desc">
        Mali and MediaTek devices apply aggressive thermal throttles to standard emulators. 
        Gamehub Mali+ builds multiple spoofed APK signatures to trigger vendor high-performance game boost modes:
      </p>

      <div class="variant-list">
        <div class="variant-item">
          <div class="variant-left">
            <div class="variant-icon">BASE</div>
            <div>
              <div class="variant-title">Gamehub-Mali-Plus.apk</div>
              <div class="variant-desc">Standard signature &bull; com.antutu.ABenchMark spoof for generic Mali devices</div>
            </div>
          </div>
          <span class="badge-apk">1.0-Alpha</span>
        </div>

        <div class="variant-item">
          <div class="variant-left">
            <div class="variant-icon">PUBG</div>
            <div>
              <div class="variant-title">Gamehub-Mali-Plus-v1.0-Alpha-pubg.apk</div>
              <div class="variant-desc">Spoofs com.tencent.ig &bull; Unlocks 120 FPS high-refresh mode on Xiaomi, Vivo &amp; Oppo</div>
            </div>
          </div>
          <span class="badge-apk">1.0-Alpha</span>
        </div>

        <div class="variant-item">
          <div class="variant-left">
            <div class="variant-icon">GENSHIN</div>
            <div>
              <div class="variant-title">Gamehub-Mali-Plus-v1.0-Alpha-genshin.apk</div>
              <div class="variant-desc">Spoofs com.miHoYo.GenshinImpact &bull; Maxes out GPU power limit on Dimensity phones</div>
            </div>
          </div>
          <span class="badge-apk">1.0-Alpha</span>
        </div>

        <div class="variant-item">
          <div class="variant-left">
            <div class="variant-icon">GEEKBENCH</div>
            <div>
              <div class="variant-title">Gamehub-Mali-Plus-v1.0-Alpha-geekbench.apk</div>
              <div class="variant-desc">Spoofs com.primatelabs.geekbench6 &bull; Unlocks maximum sustained Cortex-X core clocks</div>
            </div>
          </div>
          <span class="badge-apk">1.0-Alpha</span>
        </div>
      </div>
    </section>
  </main>

  <footer>
    Gamehub Mali+ Edition (v1.0 Alpha) &bull; Built for MediaTek Dimensity, Exynos Mali &amp; Arm Immortalis Architectures
  </footer>

  <script>
    let currentMultiplier = 3;
    let antiLagEnabled = true;

    function setMultiplier(mult) {
      currentMultiplier = mult;
      document.querySelectorAll('.btn-mult').forEach((b, i) => {
        b.classList.toggle('active', [2, 3, 4][i] === mult);
      });
      const labels = { 2: '2x (Balanced)', 3: '3x (High)', 4: '4x (Ultra)' };
      document.getElementById('lbl-mult').textContent = labels[mult];
      document.getElementById('stat-multiplier').textContent = mult + 'x';
      document.getElementById('stat-fps').textContent = (40 * mult) + ' FPS';
      document.getElementById('sim-status').textContent = 'Simulating 40 FPS -> ' + (40 * mult) + ' FPS';
    }

    function toggleAntiLag() {
      const chk = document.getElementById('chk-antilag');
      chk.checked = !chk.checked;
      onAntiLagChange(chk.checked);
    }

    function onAntiLagChange(val) {
      antiLagEnabled = val;
    }

    function toggleOption(id) {
      const chk = document.getElementById(id);
      chk.checked = !chk.checked;
    }

    function setProfile(el, name) {
      document.querySelectorAll('.preset-pill').forEach(p => p.classList.remove('active'));
      el.classList.add('active');
      document.getElementById('lbl-profile').textContent = name;
    }

    // Canvas Frame Pacing Visualizer
    const canvas = document.getElementById('pacingCanvas');
    const ctx = canvas.getContext('2d');
    let frameHistory = [];
    let tick = 0;

    function drawVisualizer() {
      tick++;
      const w = canvas.width;
      const h = canvas.height;
      ctx.clearRect(0, 0, w, h);

      // Grid lines
      ctx.strokeStyle = '#121832';
      ctx.lineWidth = 1;
      for (let y = 20; y < h; y += 30) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(w, y);
        ctx.stroke();
      }

      // Add frames based on multiplier and time
      if (tick % 4 === 0) {
        frameHistory.push({ type: 'native', height: 75 + Math.sin(tick * 0.1) * 15 });
        for (let i = 1; i < currentMultiplier; i++) {
          frameHistory.push({ type: 'gen', height: 60 + Math.sin(tick * 0.1 + i) * 10 });
        }
        if (antiLagEnabled && Math.random() < 0.08) {
          frameHistory.push({ type: 'flush', height: 40 });
        }
      }

      while (frameHistory.length > 50) {
        frameHistory.shift();
      }

      const barWidth = 6;
      const gap = 4;
      const startX = w - (frameHistory.length * (barWidth + gap)) - 10;

      frameHistory.forEach((f, idx) => {
        const x = startX + idx * (barWidth + gap);
        const barH = f.height;
        const y = h - barH - 10;

        if (f.type === 'native') {
          ctx.fillStyle = '#00d2ff';
          ctx.shadowColor = 'rgba(0, 210, 255, 0.4)';
          ctx.shadowBlur = 6;
        } else if (f.type === 'gen') {
          ctx.fillStyle = '#e028b0';
          ctx.shadowColor = 'rgba(224, 40, 176, 0.4)';
          ctx.shadowBlur = 6;
        } else {
          ctx.fillStyle = '#00ff88';
          ctx.shadowColor = 'rgba(0, 255, 136, 0.6)';
          ctx.shadowBlur = 8;
        }

        ctx.fillRect(x, y, barWidth, barH);
        ctx.shadowBlur = 0;
      });

      requestAnimationFrame(drawVisualizer);
    }

    requestAnimationFrame(drawVisualizer);
  </script>
</body>
</html>`;

const server = http.createServer((req, res) => {
  const url = req.url || '/';

  // Serve custom Gamehub Mali+ logo
  if (url === '/logo.png' || url === '/favicon.ico') {
    if (logoBuffer) {
      res.writeHead(200, {
        'Content-Type': logoMime,
        'Cache-Control': 'public, max-age=3600'
      });
      res.end(logoBuffer);
      return;
    } else {
      res.writeHead(404);
      res.end();
      return;
    }
  }

  // Health and Status API
  if (url === '/api/status') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({
      name: 'Gamehub Mali+',
      version: '1.0-alpha',
      devServer: 'running',
      port: PORT,
      framegen: {
        multiplierSupported: [2, 3, 4],
        defaultMultiplier: 3,
        antiLagSupported: true
      },
      mali: {
        dxvkAsync: true,
        tessellationLimit: true,
        box64BigCore: true
      }
    }));
    return;
  }

  // Default: Serve interactive Gamehub Mali+ Web Portal
  res.writeHead(200, {
    'Content-Type': 'text/html; charset=utf-8',
    'Cache-Control': 'no-cache'
  });
  res.end(HTML_CONTENT);
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[Gamehub Mali+] Dev server running on http://0.0.0.0:${PORT}`);
});
