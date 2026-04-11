<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>SafeYatra — Live Ride</title>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600&family=DM+Sans:wght@300;400;500;600&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
  <link rel="stylesheet" href="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css"/>
  <script src="https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.min.js"></script>
  <style>
    :root{
      --pk1:#fff0f6;--pk2:#ffd6e8;--pk3:#ffadd2;--pk4:#ff85bc;--pk5:#f759a0;
      --bl1:#f0f8ff;--bl2:#d6eaff;--bl3:#aed6f1;--bl5:#4db8f5;
      --mn1:#f0fff8;--mn2:#c6f6e3;--mn3:#81e6be;--mn4:#38c998;
      --rd:#ff4757;--rd1:#fff0f1;--pc3:#ffaa55;
      --td:#2d1b33;--tm:#5c4a66;--tl:#9c85a8;--tu:#c4b0cc;
      --bg:#fdf8ff;
      --sh:0 8px 32px rgba(180,100,200,.13);
      --rm:14px;--rl:22px;--rf:9999px;
      --tr:all .28s cubic-bezier(.4,0,.2,1);
      --bo:all .38s cubic-bezier(.34,1.56,.64,1);
    }
    *,*::before,*::after{box-sizing:border-box;margin:0;padding:0;}
    body{font-family:'DM Sans',sans-serif;background:var(--bg);color:var(--td);height:100vh;overflow:hidden;-webkit-font-smoothing:antialiased;}

    .nav{position:fixed;top:0;left:0;right:0;z-index:500;height:60px;padding:0 18px;display:flex;align-items:center;justify-content:space-between;background:rgba(255,255,255,.95);backdrop-filter:blur(18px);border-bottom:1px solid rgba(255,200,230,.32);}
    .logo{display:flex;align-items:center;gap:9px;text-decoration:none;}
    .li{width:34px;height:34px;background:linear-gradient(135deg,#ff85bc,#7ec8f4);border-radius:9px;display:flex;align-items:center;justify-content:center;font-size:17px;animation:lp 3s ease-in-out infinite;}
    @keyframes lp{0%,100%{box-shadow:0 3px 12px rgba(247,89,160,.28);}50%{box-shadow:0 3px 22px rgba(247,89,160,.5);}}
    .lt{font-family:'Playfair Display',serif;font-size:1.2rem;font-weight:600;background:linear-gradient(135deg,#f759a0,#1a9fe0);-webkit-background-clip:text;-webkit-text-fill-color:transparent;background-clip:text;}
    .nav-mid{display:flex;align-items:center;gap:10px;}
    .live-pill{display:flex;align-items:center;gap:6px;padding:5px 13px;background:linear-gradient(135deg,rgba(56,201,152,.12),rgba(77,184,245,.1));border:1.5px solid var(--mn3);border-radius:var(--rf);font-size:.78rem;font-weight:600;color:#1a7a5e;}
    .live-dot{width:8px;height:8px;border-radius:50%;background:var(--mn4);animation:livepulse 1.2s ease-in-out infinite;}
    @keyframes livepulse{0%,100%{transform:scale(1);box-shadow:0 0 0 0 rgba(56,201,152,.5);}50%{transform:scale(1.2);box-shadow:0 0 0 5px rgba(56,201,152,0);}}
    .ride-status-pill{display:flex;align-items:center;gap:5px;padding:4px 11px;background:var(--pk1);border:1px solid var(--pk2);border-radius:var(--rf);font-size:.74rem;font-weight:600;color:var(--pk5);}
    .sos-nav{display:flex;align-items:center;gap:7px;padding:7px 16px;background:linear-gradient(135deg,#ff4757,#ff6b81);color:#fff;border:none;border-radius:var(--rf);font-family:'DM Sans',sans-serif;font-size:.84rem;font-weight:700;cursor:pointer;transition:var(--bo);animation:sospulse 2s ease-in-out infinite;}
    @keyframes sospulse{0%,100%{box-shadow:0 4px 14px rgba(255,71,87,.38);}50%{box-shadow:0 4px 28px rgba(255,71,87,.7);}}
    .sos-nav:hover{transform:scale(1.06);}

    .layout{display:flex;height:100vh;padding-top:60px;}
    .side{width:355px;min-width:320px;height:100%;overflow-y:auto;background:rgba(255,255,255,.94);backdrop-filter:blur(16px);border-right:1px solid rgba(255,200,230,.26);display:flex;flex-direction:column;z-index:10;}
    .side::-webkit-scrollbar{width:4px;}
    .side::-webkit-scrollbar-thumb{background:var(--pk2);border-radius:4px;}
    .blk{padding:14px 14px 12px;border-bottom:1px solid rgba(255,200,230,.22);}
    .blk-title{font-family:'Playfair Display',serif;font-size:.87rem;font-weight:600;color:var(--td);display:flex;align-items:center;gap:6px;margin-bottom:10px;}

    .drv-row{display:flex;align-items:center;gap:12px;}
    .drv-av{width:52px;height:52px;border-radius:50%;background:linear-gradient(135deg,var(--pk2),var(--bl2));display:flex;align-items:center;justify-content:center;font-size:24px;border:3px solid var(--pk3);flex-shrink:0;}
    .drv-name{font-family:'Playfair Display',serif;font-size:1rem;font-weight:600;color:var(--td);}
    .drv-sub{font-size:.73rem;color:var(--tl);margin-top:2px;}
    .drv-veh{display:inline-flex;align-items:center;gap:5px;margin-top:6px;padding:3px 10px;background:var(--bl1);border:1px solid var(--bl3);border-radius:var(--rf);font-family:'DM Mono',monospace;font-size:.73rem;color:#1a5fa0;font-weight:500;}

    .speedbox{background:linear-gradient(135deg,#1a0a24,#0d1a2e);border-radius:var(--rl);padding:16px;text-align:center;position:relative;overflow:hidden;}
    .speedbox::before{content:'';position:absolute;inset:0;background:radial-gradient(circle at 50% 110%,rgba(247,89,160,.18),transparent 65%);pointer-events:none;}
    .gauge-wrap{position:relative;width:120px;height:64px;margin:0 auto 4px;overflow:visible;}
    .spd-val{font-family:'DM Mono',monospace;font-size:2.8rem;font-weight:500;color:#fff;line-height:1;margin-bottom:2px;text-shadow:0 0 20px rgba(247,89,160,.5);}
    .spd-unit{font-size:.78rem;color:rgba(255,255,255,.4);letter-spacing:2px;text-transform:uppercase;}
    .spd-status{display:inline-flex;align-items:center;gap:5px;margin-top:8px;padding:4px 12px;border-radius:var(--rf);font-size:.72rem;font-weight:600;}
    .spd-status.normal{background:rgba(56,201,152,.15);color:var(--mn4);border:1px solid rgba(56,201,152,.3);}
    .spd-status.fast{background:rgba(255,170,85,.15);color:#e07a00;border:1px solid rgba(255,170,85,.3);}
    .spd-status.over{background:rgba(255,71,87,.15);color:var(--rd);border:1px solid rgba(255,71,87,.3);}
    .stopped{background:rgba(174,214,241,.15);color:#1a5fa0;border:1px solid rgba(174,214,241,.3);}

    .journey-row{display:flex;align-items:flex-start;gap:9px;margin-bottom:7px;}
    .j-dot{width:10px;height:10px;border-radius:50%;flex-shrink:0;margin-top:4px;}
    .j-dot.src{background:var(--mn4);box-shadow:0 0 0 3px rgba(56,201,152,.2);}
    .j-dot.dst{background:var(--pk5);box-shadow:0 0 0 3px rgba(247,89,160,.2);}
    .j-conn{width:2px;height:20px;background:linear-gradient(180deg,var(--mn3),var(--pk3));margin-left:4px;margin-bottom:2px;}
    .j-addr{font-size:.81rem;color:var(--td);font-weight:500;line-height:1.3;}
    .j-lbl{font-size:.67rem;color:var(--tl);margin-top:1px;}
    .stats-row{display:grid;grid-template-columns:1fr 1fr 1fr;gap:7px;margin-top:10px;}
    .stat-card{background:linear-gradient(135deg,rgba(255,240,246,.7),rgba(240,248,255,.7));border:1.5px solid var(--pk2);border-radius:var(--rm);padding:9px 7px;text-align:center;}
    .stat-val{font-family:'DM Mono',monospace;font-size:.94rem;font-weight:500;color:var(--td);}
    .stat-lbl{font-size:.63rem;color:var(--tl);margin-top:2px;font-weight:500;}

    .map-click-hint{background:linear-gradient(135deg,rgba(77,184,245,.12),rgba(56,201,152,.08));border:1.5px dashed var(--bl3);border-radius:var(--rm);padding:9px 11px;font-size:.73rem;color:#1a5fa0;margin-bottom:8px;display:flex;align-items:center;gap:7px;}
    .time-ctx-badge{display:flex;align-items:center;gap:6px;padding:5px 11px;border-radius:var(--rf);font-size:.72rem;font-weight:600;margin-bottom:8px;}
    .time-ctx-day{background:#fffde7;border:1px solid #fdd835;color:#7a5800;}
    .time-ctx-night{background:#1a0a24;border:1px solid #5c2d80;color:#e0b0ff;}

    .route-full-panel{background:linear-gradient(135deg,rgba(255,240,246,.6),rgba(240,248,255,.6));border:1.5px solid var(--pk2);border-radius:var(--rl);padding:12px;}
    .rfp-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:10px;}
    .rfp-title{font-size:.8rem;font-weight:700;color:var(--td);}
    .rfp-badge{font-size:.7rem;font-weight:600;padding:2px 8px;border-radius:var(--rf);}
    .rfp-safe{background:var(--mn1);color:#1a7a5e;border:1px solid var(--mn3);}
    .rfp-mod{background:#fffbf0;color:#92400e;border:1px solid rgba(255,170,85,.4);}
    .rfp-danger{background:var(--rd1);color:var(--rd);border:1px solid rgba(255,71,87,.3);}
    .rfp-row{display:flex;align-items:center;gap:8px;padding:5px 0;border-bottom:1px solid rgba(255,200,230,.2);font-size:.74rem;}
    .rfp-row:last-child{border-bottom:none;}
    .rfp-label{color:var(--tl);width:90px;flex-shrink:0;}
    .rfp-value{font-weight:600;color:var(--td);flex:1;}
    .rfp-value.safe{color:#1a7a5e;}.rfp-value.warn{color:#92400e;}.rfp-value.danger{color:var(--rd);}
    .danger-seg-list{display:flex;flex-direction:column;gap:5px;margin-top:6px;max-height:160px;overflow-y:auto;}
    .danger-seg{display:flex;align-items:flex-start;gap:7px;padding:7px 9px;border-radius:var(--rm);font-size:.72rem;line-height:1.5;}
    .danger-seg.safe{background:var(--mn1);border:1px solid var(--mn3);color:#1a7a5e;}
    .danger-seg.moderate{background:#fffbf0;border:1px solid rgba(255,170,85,.3);color:#92400e;}
    .danger-seg.danger{background:var(--rd1);border:1px solid rgba(255,71,87,.25);color:var(--rd);}
    .seg-progress{width:100%;height:6px;background:rgba(255,200,230,.3);border-radius:3px;margin:8px 0 4px;overflow:hidden;}
    .seg-fill{height:100%;background:linear-gradient(90deg,#22c55e,#38c998);border-radius:3px;transition:width .4s ease;}
    .seg-status{font-size:.67rem;color:var(--tl);text-align:center;}

    .risk-badge{display:flex;align-items:flex-start;gap:10px;padding:12px 13px;border-radius:var(--rm);font-size:.82rem;font-weight:600;margin-bottom:10px;transition:var(--tr);}
    .risk-safe{background:var(--mn1);border:1.5px solid var(--mn3);color:#1a7a5e;}
    .risk-moderate{background:#fffbf0;border:1.5px solid rgba(255,170,85,.5);color:#92400e;}
    .risk-danger{background:var(--rd1);border:1.5px solid rgba(255,71,87,.35);color:var(--rd);animation:riskFlash .9s ease-in-out infinite;}
    .risk-unknown{background:#f8f4ff;border:1.5px solid var(--tu);color:var(--tm);}
    @keyframes riskFlash{0%,100%{opacity:1;box-shadow:0 0 0 0 rgba(255,71,87,.3);}50%{opacity:.75;box-shadow:0 0 0 8px rgba(255,71,87,0);}}
    .risk-icon{font-size:22px;flex-shrink:0;margin-top:1px;}
    .risk-factors{margin-top:6px;display:flex;flex-direction:column;gap:3px;}
    .risk-factor-item{font-size:.67rem;opacity:.85;display:flex;align-items:center;gap:4px;font-weight:500;}

    .risk-score-bar{margin:8px 0 6px;background:rgba(255,200,230,.2);border-radius:4px;height:8px;overflow:hidden;position:relative;}
    .risk-score-fill{height:100%;border-radius:4px;transition:width .5s ease,background .5s ease;}
    .risk-score-labels{display:flex;justify-content:space-between;font-size:.6rem;color:var(--tl);margin-top:2px;}

    .road-analysis{margin-bottom:8px;display:flex;flex-wrap:wrap;gap:3px;}
    .road-tag{display:inline-flex;align-items:center;gap:4px;padding:3px 9px;border-radius:var(--rf);font-size:.69rem;font-weight:600;margin:2px;}
    .rt-good{background:#dcfce7;color:#166534;border:1px solid #86efac;}
    .rt-warn{background:#fef3c7;color:#92400e;border:1px solid #fcd34d;}
    .rt-bad{background:#fee2e2;color:#991b1b;border:1px solid #fca5a5;}
    .rt-info{background:#e0f2fe;color:#075985;border:1px solid #7dd3fc;}
    .rt-night{background:#1a0a24;color:#e0b0ff;border:1px solid #5c2d80;}
    .overpass-status{font-size:.68rem;color:var(--tl);margin-top:5px;display:flex;align-items:center;gap:4px;}
    .ov-spinner{width:8px;height:8px;border:1.5px solid var(--pk3);border-top-color:var(--pk5);border-radius:50%;animation:spin .7s linear infinite;display:inline-block;}
    @keyframes spin{to{transform:rotate(360deg);}}

    .legend{display:flex;flex-direction:column;gap:7px;margin-top:8px;}
    .leg-row{display:flex;align-items:center;gap:9px;font-size:.75rem;color:var(--tm);}
    .leg-line{width:24px;height:5px;border-radius:3px;flex-shrink:0;}
    .lg{background:#22c55e;box-shadow:0 0 6px rgba(34,197,94,.5);}
    .lo{background:#f97316;box-shadow:0 0 6px rgba(249,115,22,.5);}
    .lr{background:#ef4444;box-shadow:0 0 6px rgba(239,68,68,.5);}

    .alert-list{display:flex;flex-direction:column;gap:6px;max-height:180px;overflow-y:auto;}
    .alert-item{display:flex;align-items:flex-start;gap:7px;padding:8px 10px;border-radius:var(--rm);font-size:.74rem;line-height:1.5;}
    .alert-item.safe{background:var(--mn1);border:1px solid var(--mn3);color:#1a7a5e;}
    .alert-item.warn{background:#fffbf0;border:1px solid rgba(255,170,85,.4);color:#92400e;}
    .alert-item.danger{background:var(--rd1);border:1px solid rgba(255,71,87,.28);color:var(--rd);}
    .alert-time{font-family:'DM Mono',monospace;font-size:.63rem;color:var(--tl);white-space:nowrap;margin-left:auto;}

    .wa-section{padding:13px 14px;background:linear-gradient(135deg,rgba(37,211,102,.06),rgba(37,211,102,.02));border-bottom:1px solid rgba(37,211,102,.2);}
    .wa-title{font-size:.8rem;font-weight:600;color:#1a5c2e;margin-bottom:9px;display:flex;align-items:center;gap:6px;}
    .wa-contacts{display:flex;flex-direction:column;gap:6px;margin-bottom:10px;max-height:120px;overflow-y:auto;}
    .wa-contact-row{display:flex;align-items:center;justify-content:space-between;padding:7px 10px;background:rgba(255,255,255,.8);border:1px solid rgba(37,211,102,.25);border-radius:10px;}
    .wa-contact-info{display:flex;align-items:center;gap:8px;}
    .wa-av{width:28px;height:28px;border-radius:50%;background:linear-gradient(135deg,#dcfce7,#bbf7d0);display:flex;align-items:center;justify-content:center;font-size:14px;flex-shrink:0;}
    .wa-cname{font-size:.78rem;font-weight:600;color:var(--td);}
    .wa-cphone{font-size:.68rem;color:var(--tl);}
    .wa-send-one{padding:5px 10px;background:linear-gradient(135deg,#25d366,#128c7e);color:#fff;border:none;border-radius:var(--rf);font-family:'DM Sans',sans-serif;font-size:.7rem;font-weight:600;cursor:pointer;transition:var(--bo);display:flex;align-items:center;gap:4px;white-space:nowrap;}
    .wa-send-one:hover{transform:scale(1.05);box-shadow:0 4px 12px rgba(37,211,102,.4);}
    .wa-send-all{width:100%;padding:11px;background:linear-gradient(135deg,#25d366,#128c7e);color:#fff;border:none;border-radius:var(--rm);font-family:'DM Sans',sans-serif;font-size:.86rem;font-weight:700;cursor:pointer;transition:var(--bo);display:flex;align-items:center;justify-content:center;gap:8px;box-shadow:0 6px 20px rgba(37,211,102,.35);}
    .wa-send-all:hover{transform:translateY(-2px);box-shadow:0 10px 28px rgba(37,211,102,.5);}
    .wa-no-contacts{font-size:.76rem;color:#92400e;background:#fffbf0;border:1px solid rgba(255,170,85,.3);padding:8px 11px;border-radius:var(--rm);text-align:center;}

    .actions{display:grid;grid-template-columns:1fr 1fr;gap:7px;}
    .act-btn{padding:10px 8px;border:none;border-radius:var(--rm);font-family:'DM Sans',sans-serif;font-size:.77rem;font-weight:600;cursor:pointer;transition:var(--bo);display:flex;align-items:center;justify-content:center;gap:5px;}
    .act-btn:hover{transform:translateY(-2px);}
    .ab-share{background:linear-gradient(135deg,rgba(77,184,245,.12),rgba(56,201,152,.1));border:1.5px solid var(--bl3);color:#1a5fa0;}
    .ab-maps{background:linear-gradient(135deg,rgba(56,201,152,.12),rgba(56,201,152,.06));border:1.5px solid var(--mn3);color:#1a7a5e;}
    .ab-report{background:linear-gradient(135deg,rgba(255,170,85,.1),rgba(255,170,85,.06));border:1.5px solid rgba(255,170,85,.4);color:#92400e;}
    .ab-end{background:linear-gradient(135deg,rgba(255,71,87,.1),rgba(255,100,100,.07));border:1.5px solid rgba(255,71,87,.3);color:var(--rd);grid-column:1/-1;}

    #map{flex:1;height:100%;cursor:crosshair;}
    .map-pill{position:absolute;z-index:400;background:rgba(255,255,255,.96);backdrop-filter:blur(10px);border:1.5px solid var(--pk2);border-radius:var(--rm);padding:8px 13px;font-size:.75rem;color:var(--tm);box-shadow:var(--sh);display:flex;align-items:center;gap:7px;}
    #pill-top{top:70px;right:14px;flex-direction:column;align-items:flex-end;gap:4px;}
    .pill-row{display:flex;align-items:center;gap:6px;font-size:.74rem;}

    #click-dest-banner{position:absolute;top:70px;left:50%;transform:translateX(-50%);z-index:450;background:rgba(77,184,245,.96);color:#fff;border-radius:var(--rf);padding:8px 18px;font-size:.79rem;font-weight:700;box-shadow:0 6px 20px rgba(77,184,245,.4);display:none;align-items:center;gap:8px;white-space:nowrap;animation:bannerPop .4s cubic-bezier(.34,1.56,.64,1);}
    @keyframes bannerPop{from{transform:translateX(-50%) scale(.8);opacity:0;}to{transform:translateX(-50%) scale(1);opacity:1;}}
    #click-dest-banner.show{display:flex;}

    #risk-overlay{position:absolute;bottom:90px;left:14px;z-index:400;padding:10px 14px;border-radius:var(--rm);font-size:.76rem;font-weight:600;backdrop-filter:blur(10px);box-shadow:var(--sh);display:none;align-items:center;gap:8px;min-width:200px;}
    #risk-overlay.safe{background:rgba(240,255,248,.97);border:1.5px solid var(--mn3);color:#1a7a5e;display:flex;}
    #risk-overlay.moderate{background:rgba(255,251,240,.97);border:1.5px solid rgba(255,170,85,.5);color:#92400e;display:flex;}
    #risk-overlay.danger{background:rgba(255,240,241,.97);border:1.5px solid rgba(255,71,87,.4);color:var(--rd);display:flex;animation:riskFlash .9s ease-in-out infinite;}
    #risk-overlay.unknown{background:rgba(248,244,255,.97);border:1.5px solid var(--tu);color:var(--tm);display:flex;}

    #night-mode-badge{position:absolute;bottom:140px;left:14px;z-index:400;padding:8px 13px;border-radius:var(--rm);font-size:.73rem;font-weight:600;backdrop-filter:blur(10px);box-shadow:var(--sh);display:none;align-items:center;gap:6px;}
    #night-mode-badge.show{display:flex;}
    #night-mode-badge.night{background:rgba(26,10,36,.97);border:1.5px solid #5c2d80;color:#e0b0ff;}
    #night-mode-badge.day{background:rgba(255,253,231,.97);border:1.5px solid #fdd835;color:#7a5800;}

    .modal-bg{position:fixed;inset:0;z-index:900;background:rgba(45,27,51,.45);backdrop-filter:blur(6px);display:none;align-items:center;justify-content:center;padding:20px;}
    .modal-bg.show{display:flex;animation:mfade .25s ease both;}
    @keyframes mfade{from{opacity:0;}to{opacity:1;}}
    .modal{background:#fff;border-radius:var(--rl);padding:26px 22px;max-width:400px;width:100%;text-align:center;box-shadow:0 24px 60px rgba(45,27,51,.22);animation:mslide .3s cubic-bezier(.34,1.56,.64,1) both;}
    @keyframes mslide{from{transform:translateY(30px);opacity:0;}to{transform:translateY(0);opacity:1;}}
    .modal-ic{font-size:42px;margin-bottom:10px;}
    .modal-title{font-family:'Playfair Display',serif;font-size:1.15rem;font-weight:600;margin-bottom:6px;color:var(--td);}
    .modal-sub{font-size:.82rem;color:var(--tm);margin-bottom:18px;line-height:1.6;}
    .modal-actions{display:flex;gap:10px;justify-content:center;flex-wrap:wrap;}
    .ride-status-card{background:linear-gradient(135deg,var(--pk1),var(--bl1));border:1.5px solid var(--pk2);border-radius:var(--rm);padding:14px;text-align:left;margin-bottom:16px;}
    .rs-row{display:flex;justify-content:space-between;align-items:center;padding:5px 0;border-bottom:1px solid var(--pk2);font-size:.8rem;}
    .rs-row:last-child{border-bottom:none;padding-bottom:0;}
    .rs-label{color:var(--tl);font-weight:500;}.rs-value{color:var(--td);font-weight:600;}
    .btn{display:inline-flex;align-items:center;justify-content:center;gap:7px;padding:10px 20px;border:none;border-radius:var(--rf);font-family:'DM Sans',sans-serif;font-size:.86rem;font-weight:500;cursor:pointer;text-decoration:none;transition:var(--bo);}
    .btn-g{background:transparent;color:var(--tm);border:1.5px solid var(--pk2);}
    .btn-g:hover{background:var(--pk1);color:var(--pk5);}
    .btn-danger{background:linear-gradient(135deg,#ff4757,#ff6b81);color:#fff;box-shadow:0 6px 18px rgba(255,71,87,.35);}
    .btn-danger:hover{transform:translateY(-2px);}
    .btn-wa{background:linear-gradient(135deg,#25d366,#128c7e);color:#fff;box-shadow:0 6px 18px rgba(37,211,102,.35);}
    .btn-wa:hover{transform:translateY(-2px);}

    .toasts{position:fixed;top:70px;right:14px;z-index:9999;display:flex;flex-direction:column;gap:8px;max-width:300px;}
    .toast{background:rgba(255,255,255,.97);border-radius:var(--rm);padding:10px 13px;display:flex;align-items:flex-start;gap:9px;box-shadow:0 10px 28px rgba(180,100,200,.17);border-left:4px solid var(--pk4);animation:tin .38s cubic-bezier(.34,1.56,.64,1) both;}
    .toast.ok{border-left-color:var(--mn4);}.toast.err{border-left-color:var(--rd);}.toast.warn{border-left-color:#ffaa55;}.toast.wa{border-left-color:#25d366;}
    @keyframes tin{from{transform:translateX(110%);opacity:0;}to{transform:translateX(0);opacity:1;}}
    .t-ic{font-size:16px;flex-shrink:0;}.t-tt{font-weight:600;font-size:.8rem;color:var(--td);margin-bottom:1px;}.t-ms{font-size:.73rem;color:var(--tm);}
    .t-x{background:none;border:none;cursor:pointer;color:var(--tu);font-size:13px;margin-left:auto;flex-shrink:0;}

    @media(max-width:768px){
      .side{width:100%;min-width:unset;max-height:55vh;border-right:none;border-bottom:1px solid rgba(255,200,230,.26);}
      .layout{flex-direction:column;}
      #map{height:45vh;}
      #pill-top{top:auto;bottom:calc(45vh + 8px);right:8px;}
      #risk-overlay,#night-mode-badge{display:none!important;}
    }
    .leaflet-routing-container{display:none!important;}
    .dest-popup .leaflet-popup-content-wrapper{border-radius:14px;border:1.5px solid #ffd6e8;box-shadow:0 8px 28px rgba(247,89,160,.2);padding:12px 14px;}
    .dest-popup .leaflet-popup-tip{background:#fff;}
  </style>
</head>
<body>

<nav class="nav">
  <a href="index.html" class="logo"><div class="li">🛡️</div><span class="lt">SafeYatra</span></a>
  <div class="nav-mid">
    <div class="live-pill"><div class="live-dot"></div>LIVE RIDE</div>
    <div class="ride-status-pill" id="nav-status-pill">🚗 ONGOING</div>
  </div>
  <button class="sos-nav" onclick="triggerSOS()">🚨 RED ALERT</button>
</nav>

<div class="layout">
  <div class="side">

    <div class="blk">
      <div class="blk-title">🧑‍✈️ Your Driver</div>
      <div class="drv-row">
        <div class="drv-av" id="drv-av">👩</div>
        <div>
          <div class="drv-name" id="drv-name">—</div>
          <div class="drv-sub"  id="drv-phone">—</div>
          <div class="drv-veh"  id="drv-veh">—</div>
        </div>
        <button onclick="showRideStatus()" style="margin-left:auto;padding:5px 10px;background:var(--pk1);border:1px solid var(--pk2);border-radius:var(--rf);font-size:.72rem;font-weight:600;color:var(--pk5);cursor:pointer;">📋 Status</button>
      </div>
    </div>

    <div class="blk">
      <div class="blk-title">⚡ Vehicle Speed</div>
      <div class="speedbox">
        <div class="gauge-wrap">
          <svg width="120" height="64" viewBox="0 0 120 64" style="overflow:visible;">
            <path d="M10,60 A50,50,0,0,1,110,60" fill="none" stroke="rgba(255,255,255,.1)" stroke-width="7" stroke-linecap="round"/>
            <path id="gauge-arc" d="M10,60 A50,50,0,0,1,110,60" fill="none" stroke="url(#gaug)" stroke-width="7" stroke-linecap="round" stroke-dasharray="157" stroke-dashoffset="157" style="transition:stroke-dashoffset .6s ease;"/>
            <defs><linearGradient id="gaug" x1="0" y1="0" x2="1" y2="0"><stop offset="0%" stop-color="#38c998"/><stop offset="60%" stop-color="#f97316"/><stop offset="100%" stop-color="#ef4444"/></linearGradient></defs>
          </svg>
        </div>
        <div class="spd-val" id="spd-val">0</div>
        <div class="spd-unit">km / h</div>
        <div class="spd-status normal" id="spd-status">● Vehicle Stopped</div>
      </div>
    </div>

    <div class="blk">
      <div class="blk-title">📍 Journey</div>
      <div class="map-click-hint">🖱 <span>Click anywhere on map to set / change destination</span></div>
      <div class="journey-row"><div class="j-dot src"></div><div><div class="j-addr" id="j-src">Detecting…</div><div class="j-lbl">Your Location</div></div></div>
      <div class="journey-row"><div class="j-conn"></div></div>
      <div class="journey-row"><div class="j-dot dst"></div><div><div class="j-addr" id="j-dst">Tap map to set destination</div><div class="j-lbl">Destination</div></div></div>
      <div class="stats-row">
        <div class="stat-card"><div class="stat-val" id="st-time">00:00</div><div class="stat-lbl">Elapsed</div></div>
        <div class="stat-card"><div class="stat-val" id="st-dist">0 km</div><div class="stat-lbl">Distance</div></div>
        <div class="stat-card"><div class="stat-val" id="st-spd">0</div><div class="stat-lbl">km/h avg</div></div>
      </div>
    </div>

    <div class="blk">
      <div class="blk-title">🗺 Full Route Safety Analysis</div>
      <div id="time-ctx-badge" class="time-ctx-badge time-ctx-day">☀️ Daytime — Roads are safe by default</div>
      <div class="route-full-panel" id="rfp">
        <div class="rfp-header">
          <span class="rfp-title" id="rfp-title">Set a destination to scan route…</span>
          <span class="rfp-badge rfp-safe" id="rfp-overall-badge">⏳ Waiting</span>
        </div>
        <div class="rfp-row"><span class="rfp-label">Origin</span><span class="rfp-value" id="rfp-origin">—</span></div>
        <div class="rfp-row"><span class="rfp-label">Destination</span><span class="rfp-value" id="rfp-dest">Tap map to set</span></div>
        <div class="rfp-row"><span class="rfp-label">Road Types</span><span class="rfp-value" id="rfp-roads">—</span></div>
        <div class="rfp-row"><span class="rfp-label">Lighting</span><span class="rfp-value" id="rfp-lighting">—</span></div>
        <div class="rfp-row"><span class="rfp-label">Crowd Level</span><span class="rfp-value" id="rfp-crowd">—</span></div>
        <div class="rfp-row"><span class="rfp-label">Risk Level</span><span class="rfp-value" id="rfp-risk">—</span></div>
        <div style="margin-top:10px;">
          <div style="font-size:.7rem;font-weight:600;color:var(--tm);margin-bottom:4px;" id="seg-progress-label">Waiting for destination…</div>
          <div class="seg-progress"><div class="seg-fill" id="seg-fill" style="width:0%"></div></div>
          <div class="seg-status" id="seg-status">Click map to start scan</div>
        </div>
        <div style="font-size:.76rem;font-weight:600;color:var(--td);margin:8px 0 5px;">⚠️ Route Segment Alerts</div>
        <div class="danger-seg-list" id="danger-seg-list">
          <div class="danger-seg safe">🗺 Tap the map to set destination and begin route analysis.</div>
        </div>
      </div>
    </div>

    <div class="blk">
      <div class="blk-title">🛡 Real-Time Road Risk</div>
      <div class="risk-badge risk-unknown" id="risk-badge">
        <span class="risk-icon">⚪</span>
        <div style="flex:1;">
          <div style="font-size:.82rem;font-weight:700;" id="risk-title">Analysing…</div>
          <div style="font-size:.7rem;font-weight:400;opacity:.8;" id="risk-reason">Fetching road data…</div>
          <div class="risk-factors" id="risk-factors"></div>
        </div>
      </div>
      <div id="risk-score-section" style="margin-bottom:10px;display:none;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:4px;">
          <span style="font-size:.7rem;font-weight:600;color:var(--tm);">Risk Score</span>
          <span style="font-family:'DM Mono',monospace;font-size:.74rem;font-weight:700;" id="risk-score-num">0/10</span>
        </div>
        <div class="risk-score-bar">
          <div class="risk-score-fill" id="risk-score-fill" style="width:0%;background:#22c55e;"></div>
        </div>
        <div class="risk-score-labels"><span>Safe</span><span>Moderate</span><span>Danger</span></div>
      </div>
      <div class="road-analysis" id="road-tags"></div>
      <div class="overpass-status" id="overpass-status">
        <span class="ov-spinner"></span> Querying road data…
      </div>
      <div class="legend">
        <div class="leg-row"><div class="leg-line lg"></div><span>Safe — daytime OR lit + busy road</span></div>
        <div class="leg-row"><div class="leg-line lo"></div><span>Caution — night + dim / semi-isolated</span></div>
        <div class="leg-row"><div class="leg-line lr"></div><span>Danger — night + no lights + no crowd</span></div>
      </div>
    </div>

    <div class="blk">
      <div class="blk-title">🚨 Safety Alerts</div>
      <div class="alert-list" id="alert-list">
        <div class="alert-item safe">✅ Driver verified — identity confirmed<span class="alert-time">Just now</span></div>
        <div class="alert-item safe">📡 Live location shared with contacts<span class="alert-time">Just now</span></div>
      </div>
    </div>

    <div class="wa-section">
      <div class="wa-title">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="#25d366"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/></svg>
        Send WhatsApp Alert
      </div>
      <div class="wa-contacts" id="wa-contacts-list"></div>
      <button class="wa-send-all" onclick="sendWhatsAppAll()">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="white"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/></svg>
        Send to ALL Contacts
      </button>
    </div>

    <div class="blk">
      <div class="blk-title">⚡ Quick Actions</div>
      <div class="actions">
        <button class="act-btn ab-share" onclick="copyLocationLink()">📋 Copy Location</button>
        <button class="act-btn ab-maps"  onclick="openGoogleMaps()">🗺 Open Maps</button>
        <button class="act-btn ab-report" onclick="reportDriver()">⚠️ Report Driver</button>
        <button class="act-btn ab-share" onclick="showRideStatus()">📋 Ride Status</button>
        <button class="act-btn ab-end"   onclick="showEndModal()">🏁 End Ride</button>
      </div>
    </div>

  </div>

  <div style="flex:1;position:relative;">
    <div id="map"></div>
    <div id="click-dest-banner" class="show">🖱 Click anywhere on map to set destination</div>
    <div class="map-pill" id="pill-top">
      <div class="pill-row"><span>⏱</span><span id="pill-eta-txt">Click map to set destination</span></div>
      <div class="pill-row" style="font-size:.68rem;color:var(--tl);"><span id="pill-coords">Getting location…</span></div>
    </div>
    <div id="night-mode-badge">
      <span id="night-ic">🌙</span>
      <span id="night-txt">Night Safety Mode</span>
    </div>
    <div id="risk-overlay" class="unknown">
      <span id="risk-ov-ic">⚪</span>
      <div>
        <div id="risk-ov-txt" style="font-weight:700;">Analysing…</div>
        <div id="risk-ov-sub" style="font-size:.65rem;opacity:.8;margin-top:1px;"></div>
      </div>
    </div>
  </div>
</div>

<!-- MODALS -->
<div class="modal-bg" id="end-modal">
  <div class="modal">
    <div class="modal-ic">🏁</div>
    <div class="modal-title">End This Ride?</div>
    <div class="modal-sub">Your ride will be completed and contacts will receive a safe arrival notification.</div>
    <div class="modal-actions">
      <button class="btn btn-g" onclick="hideModal('end-modal')">Cancel</button>
      <button class="btn btn-danger" onclick="endRide()">Yes, End Ride</button>
    </div>
  </div>
</div>

<div class="modal-bg" id="status-modal">
  <div class="modal">
    <div class="modal-ic">📋</div>
    <div class="modal-title">Ride Status</div>
    <div class="ride-status-card" id="ride-status-card"></div>
    <div class="modal-actions">
      <button class="btn btn-g" onclick="hideModal('status-modal')">Close</button>
      <button class="btn btn-wa" onclick="sendWhatsAppAll();hideModal('status-modal');">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="white"><path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.148-.67.15-.197.297-.767.966-.94 1.164-.173.199-.347.223-.644.075-.297-.15-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.298-.347.446-.52.149-.174.198-.298.298-.497.099-.198.05-.371-.025-.52-.075-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.371-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413-.074-.124-.272-.198-.57-.347m-5.421 7.403h-.004a9.87 9.87 0 01-5.031-1.378l-.361-.214-3.741.982.998-3.648-.235-.374a9.86 9.86 0 01-1.51-5.26c.001-5.45 4.436-9.884 9.888-9.884 2.64 0 5.122 1.03 6.988 2.898a9.825 9.825 0 012.893 6.994c-.003 5.45-4.437 9.884-9.885 9.884m8.413-18.297A11.815 11.815 0 0012.05 0C5.495 0 .16 5.335.157 11.892c0 2.096.547 4.142 1.588 5.945L.057 24l6.305-1.654a11.882 11.882 0 005.683 1.448h.005c6.554 0 11.89-5.335 11.893-11.893a11.821 11.821 0 00-3.48-8.413z"/></svg>
        Send to Contacts
      </button>
    </div>
  </div>
</div>

<div class="modal-bg" id="sos-modal">
  <div class="modal">
    <div class="modal-ic">🚨</div>
    <div class="modal-title" style="color:var(--rd);">RED ALERT!</div>
    <div class="modal-sub">Emergency alert will be sent to all contacts via WhatsApp with your live location and driver details.</div>
    <div class="modal-actions">
      <button class="btn btn-g" onclick="hideModal('sos-modal')">Cancel</button>
      <button class="btn btn-danger" onclick="confirmSOS()">🚨 Confirm SOS</button>
    </div>
  </div>
</div>

<div class="toasts" id="toasts"></div>

<script>
/* ═══════════════════════════════════════════════════════════
   STORAGE
═══════════════════════════════════════════════════════════ */
const U   = JSON.parse(localStorage.getItem('user')              || '{}');
const DRV = JSON.parse(localStorage.getItem('selectedDriver')    || '{}');
const ECS = JSON.parse(localStorage.getItem('emergencyContacts') || '[]');

document.getElementById('drv-av').textContent    = DRV.emoji   || '👩';
document.getElementById('drv-name').textContent  = DRV.full    || DRV.name  || '—';
document.getElementById('drv-phone').textContent = DRV.phone   || '—';
document.getElementById('drv-veh').textContent   = DRV.vehicle || '—';

let DEST = 'Not set', DEST_LAT = null, DEST_LNG = null;

(function seedFromStorage() {
  const sLat  = parseFloat(localStorage.getItem('rideDestLat')   || '');
  const sLng  = parseFloat(localStorage.getItem('rideDestLng')   || '');
  const sName = localStorage.getItem('rideDestination') || '';
  if (sLat && sLng) { DEST_LAT = sLat; DEST_LNG = sLng; DEST = sName || `${sLat.toFixed(4)}, ${sLng.toFixed(4)}`; }
})();

/* ═══════════════════════════════════════════════════════════
   WHATSAPP MESSAGE BUILDERS
   — Normal: Driver info → "This is a safe update" → Passenger info
   — SOS:    🚨 RED ALERT header → Driver info → Passenger info
═══════════════════════════════════════════════════════════ */
function buildNormalWAMessage() {
  const now    = new Date();
  const timeStr = now.toLocaleTimeString('en-IN', { hour:'2-digit', minute:'2-digit' });
  const dateStr = now.toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' });
  const locLink = (userLL)
    ? `https://maps.google.com/?q=${userLL.lat.toFixed(6)},${userLL.lng.toFixed(6)}`
    : 'Location unavailable';

  const drvName  = DRV.full || DRV.name  || 'Unknown Driver';
  const drvPhone = DRV.phone   || 'N/A';
  const drvVeh   = DRV.vehicle || 'N/A';
  const drvEmoji = DRV.emoji   || '🚗';

  const userName  = U.name  || 'Passenger';
  const userPhone = U.phone || 'N/A';

  return (
`🚗 *RIDE UPDATE — SafeYatra*
━━━━━━━━━━━━━━━━━━━━━━
🧑‍✈️ *DRIVER DETAILS*
• Name    : ${drvEmoji} ${drvName}
• Phone   : ${drvPhone}
• Vehicle : ${drvVeh}
━━━━━━━━━━━━━━━━━━━━━━
✅ *This is a normal, safe check-in message.*
No action required — everything is fine!
━━━━━━━━━━━━━━━━━━━━━━
👤 *PASSENGER DETAILS*
• Name    : ${userName}
• Phone   : ${userPhone}
• To      : ${DEST}
━━━━━━━━━━━━━━━━━━━━━━
📍 *Live Location*
${locLink}

🕐 Sent at ${timeStr} on ${dateStr}
_Powered by SafeYatra_`
  );
}

function buildSOSWAMessage() {
  const now    = new Date();
  const timeStr = now.toLocaleTimeString('en-IN', { hour:'2-digit', minute:'2-digit' });
  const dateStr = now.toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' });
  const locLink = (userLL)
    ? `https://maps.google.com/?q=${userLL.lat.toFixed(6)},${userLL.lng.toFixed(6)}`
    : 'Location unavailable';

  const drvName  = DRV.full || DRV.name  || 'Unknown Driver';
  const drvPhone = DRV.phone   || 'N/A';
  const drvVeh   = DRV.vehicle || 'N/A';
  const drvEmoji = DRV.emoji   || '🚗';

  const userName  = U.name  || 'Passenger';
  const userPhone = U.phone || 'N/A';

  return (
`🚨🚨 *RED ALERT — EMERGENCY SOS* 🚨🚨
⚠️ *IMMEDIATE HELP NEEDED!* ⚠️
━━━━━━━━━━━━━━━━━━━━━━
🧑‍✈️ *DRIVER DETAILS*
• Name    : ${drvEmoji} ${drvName}
• Phone   : ${drvPhone}
• Vehicle : ${drvVeh}
━━━━━━━━━━━━━━━━━━━━━━
👤 *PASSENGER IN DANGER*
• Name    : ${userName}
• Phone   : ${userPhone}
• Heading : ${DEST}
━━━━━━━━━━━━━━━━━━━━━━
📍 *LIVE LOCATION — ACT NOW*
${locLink}

🕐 Alert triggered at ${timeStr} on ${dateStr}
🚔 Please call police: 100 | Ambulance: 108
_SafeYatra Emergency System_`
  );
}

/* ═══════════════════════════════════════════════════════════
   WHATSAPP SEND FUNCTIONS
═══════════════════════════════════════════════════════════ */
function sendWhatsAppOne(phone, isSOS = false) {
  const msg  = isSOS ? buildSOSWAMessage() : buildNormalWAMessage();
  const clean = phone.replace(/\D/g,'');
  const num   = clean.startsWith('91') ? clean : '91' + clean;
  const url   = `https://wa.me/${num}?text=${encodeURIComponent(msg)}`;
  window.open(url,'_blank');
}

function sendWhatsAppAll(isSOS = false) {
  if (!ECS.length) { toast('warn','⚠️ No contacts','Add emergency contacts first.'); return; }
  ECS.forEach((c, i) => setTimeout(() => sendWhatsAppOne(c.phone, isSOS), i * 600));
  const label = isSOS ? '🚨 SOS Alert sent' : '✅ Safe update sent';
  toast('wa', label, `Sent to ${ECS.length} contact(s) via WhatsApp`);
  addAlert(isSOS ? 'danger' : 'safe', `${label} to ${ECS.length} contact(s)`);
}

/* ═══════════════════════════════════════════════════════════
   TIME & NIGHT WINDOW  (18:30 → 06:00)
═══════════════════════════════════════════════════════════ */
function isDangerWindow() {
  const now = new Date(), m = now.getHours() * 60 + now.getMinutes();
  return m >= 1110 || m < 360;
}

function timeLabel() {
  if (!isDangerWindow()) return { text:'☀️ Daytime — Safe mode active', cls:'time-ctx-day' };
  const h = new Date().getHours();
  const lbl = h >= 18 ? 'Evening / Night' : 'Early Morning';
  return { text:`🌙 ${lbl} (6:30 PM–6 AM) — Night safety ON`, cls:'time-ctx-night' };
}

function updateTimeCtxBadge() {
  const { text, cls } = timeLabel();
  const el = document.getElementById('time-ctx-badge');
  el.textContent = text; el.className = `time-ctx-badge ${cls}`;
  const nb = document.getElementById('night-mode-badge');
  if (isDangerWindow()) {
    nb.className = 'night-mode-badge show night';
    document.getElementById('night-ic').textContent = '🌙';
    document.getElementById('night-txt').textContent = 'Night Safety Mode Active';
  } else {
    nb.className = 'night-mode-badge show day';
    document.getElementById('night-ic').textContent = '☀️';
    document.getElementById('night-txt').textContent = 'Daytime — Safe';
  }
}

/* ═══════════════════════════════════════════════════════════
   ROAD TYPE SETS
═══════════════════════════════════════════════════════════ */
const HIGH_RISK  = new Set(['track','path','footway','bridleway','cycleway','unclassified','service','road']);
const SAFE_HW    = new Set(['motorway','trunk','primary','secondary','tertiary','residential','living_street']);
const MAJOR_HW   = new Set(['motorway','trunk','primary']);

/* ═══════════════════════════════════════════════════════════
   CROWD DETECTION
═══════════════════════════════════════════════════════════ */
async function queryCrowdData(lat, lng) {
  const q = `[out:json][timeout:10];
(
  node["amenity"~"restaurant|cafe|shop|hospital|school|bank|market|bus_stop|fuel"](around:300,${lat},${lng});
  node["shop"](around:300,${lat},${lng});
  node["public_transport"](around:200,${lat},${lng});
);
out count;`;
  try {
    const res = await fetch('https://overpass-api.de/api/interpreter',{
      method:'POST', body:'data='+encodeURIComponent(q), signal:AbortSignal.timeout(10000)
    });
    if (!res.ok) return 0;
    const json = await res.json();
    return (json.elements && json.elements[0] && json.elements[0].tags)
      ? parseInt(json.elements[0].tags.total || '0')
      : (json.elements ? json.elements.length : 0);
  } catch(_){ return 0; }
}

/* ═══════════════════════════════════════════════════════════
   MAP GLOBALS
═══════════════════════════════════════════════════════════ */
let map, userMarker, destMarker, routeControl;
let userLL = null, destLL = null;
let lastLat = null, lastLng = null, lastTime = null;
let totalDist = 0, speedSamples = [];
let rideStartedAt = Date.now();
let currentRisk = 'unknown';
let stoppedSince = null;
const STOP_ALERT_MS = 5 * 60 * 1000;
let lastOverpassLL = null;
let routeSegments = [], segmentResults = [], segScanRunning = false;
let coloredRouteLayers = [];

const sleep = ms => new Promise(r => setTimeout(r, ms));
function getDistance(lat1,lng1,lat2,lng2){
  const R=6371000,dLat=(lat2-lat1)*Math.PI/180,dLng=(lng2-lng1)*Math.PI/180;
  const a=Math.sin(dLat/2)**2+Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dLng/2)**2;
  return R*2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
}

/* ═══════════════════════════════════════════════════════════
   INIT
═══════════════════════════════════════════════════════════ */
window.addEventListener('DOMContentLoaded', () => {
  map = L.map('map', { zoomControl: true }).setView([20.5937, 78.9629], 5);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 19
  }).addTo(map);

  map.on('click', async function(e) {
    const { lat, lng } = e.latlng;
    toast('ok','📍 Setting destination…',`${lat.toFixed(5)}, ${lng.toFixed(5)}`);
    DEST_LAT = lat; DEST_LNG = lng;
    localStorage.setItem('rideDestLat', lat);
    localStorage.setItem('rideDestLng', lng);
    const addr = await reverseGeocode(lat, lng);
    DEST = addr;
    localStorage.setItem('rideDestination', addr);
    document.getElementById('j-dst').textContent    = addr;
    document.getElementById('rfp-dest').textContent = addr;
    destLL = { lat, lng };
    document.getElementById('click-dest-banner').classList.remove('show');
    placeDestMarker(lat, lng, addr);
    if (userLL) {
      drawColoredOSRMRoute(userLL.lat, userLL.lng, lat, lng);
      routeSegments = interpolatePoints(userLL.lat, userLL.lng, lat, lng, 10);
      if (!segScanRunning) startFullRouteScan();
    } else {
      addAlert('warn','⚠️ Destination saved — route will draw once GPS locks');
    }
    toast('ok','✅ Destination Set', addr);
    addAlert('safe',`📍 Destination: ${addr}`);
  });

  updateTimeCtxBadge();
  setInterval(updateTimeCtxBadge, 60000);
  startElapsedTimer();
  buildWAContactsList();
  startTracking();
});

/* ═══════════════════════════════════════════════════════════
   GEOLOCATION
═══════════════════════════════════════════════════════════ */
function startTracking() {
  if (!navigator.geolocation) { addAlert('warn','⚠️ Geolocation not supported.'); return; }
  navigator.geolocation.getCurrentPosition(
    pos => initRideMap(pos.coords.latitude, pos.coords.longitude, pos.coords.accuracy),
    async () => {
      toast('warn','GPS unavailable','Trying IP location…');
      const ip = await ipGeolocate();
      initRideMap(ip.lat, ip.lng, null);
    },
    { enableHighAccuracy: true, timeout: 10000 }
  );
  navigator.geolocation.watchPosition(
    onPositionUpdate,
    err => console.warn('GPS watch', err.code),
    { enableHighAccuracy: true, maximumAge: 2000, timeout: 15000 }
  );
}

async function ipGeolocate() {
  try {
    const r = await fetch('https://ipapi.co/json/',{signal:AbortSignal.timeout(5000)});
    if (!r.ok) throw 0;
    const d = await r.json();
    if (d.latitude && d.longitude) return { lat: d.latitude, lng: d.longitude };
  } catch(_){}
  try {
    const r2 = await fetch('https://ip-api.com/json/?fields=lat,lon',{signal:AbortSignal.timeout(5000)});
    const d2 = await r2.json();
    if (d2.lat && d2.lon) return { lat: d2.lat, lng: d2.lon };
  } catch(_){}
  return { lat: 12.6920, lng: 79.9767 };
}

async function reverseGeocode(lat, lng) {
  try {
    const url = `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json&addressdetails=1&accept-language=en`;
    const res = await fetch(url, { headers:{'Accept-Language':'en'}, signal:AbortSignal.timeout(8000) });
    if (!res.ok) throw 0;
    const r = await res.json();
    const a = r.address || {};
    const parts = [
      a.road||a.pedestrian||a.footway||'',
      a.suburb||a.neighbourhood||'',
      a.city||a.town||a.village||'',
      a.state||''
    ].filter(Boolean);
    return parts.join(', ') || r.display_name || `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
  } catch(_){ return `${lat.toFixed(5)}, ${lng.toFixed(5)}`; }
}

function placeDestMarker(dlat, dlng, addr) {
  if (destMarker) { map.removeLayer(destMarker); destMarker = null; }
  const shortLabel = (addr || DEST).split(',')[0] || 'Destination';
  const html = `
    <div style="display:flex;flex-direction:column;align-items:center;">
      <div style="position:relative;width:28px;height:28px;">
        <div style="width:28px;height:28px;background:linear-gradient(135deg,#f759a0,#ff3d7f);
          border-radius:50% 50% 50% 0;transform:rotate(-45deg);border:3px solid #fff;
          box-shadow:0 4px 14px rgba(247,89,160,.55);"></div>
        <div style="position:absolute;top:4px;left:5px;font-size:11px;">🏁</div>
      </div>
      <div style="margin-top:6px;background:rgba(255,255,255,.97);border:1.5px solid #f759a0;
        border-radius:8px;padding:4px 9px;font-family:'DM Sans',sans-serif;
        font-size:11px;font-weight:700;color:#2d1b33;white-space:nowrap;
        box-shadow:0 3px 10px rgba(247,89,160,.25);max-width:180px;
        overflow:hidden;text-overflow:ellipsis;text-align:center;">🏁 ${shortLabel}</div>
    </div>`;
  const dIcon = L.divIcon({ html, className:'', iconSize:[200,70], iconAnchor:[100,34] });
  destMarker = L.marker([dlat, dlng], { icon: dIcon, zIndexOffset: 1000 }).addTo(map)
    .bindPopup(`<div style="font-family:'DM Sans',sans-serif;min-width:190px;">
      <div style="font-size:13px;font-weight:700;color:#2d1b33;margin-bottom:5px;">🏁 Destination</div>
      <div style="font-size:12px;font-weight:600;color:#f759a0;margin-bottom:4px;">${addr || DEST}</div>
      <div style="font-size:10px;color:#9c85a8;font-family:monospace;margin-bottom:6px;">${dlat.toFixed(6)}, ${dlng.toFixed(6)}</div>
      <a href="https://www.google.com/maps?q=${dlat},${dlng}" target="_blank" style="font-size:11px;font-weight:600;color:#1a5fa0;text-decoration:none;">🗺 Open in Google Maps ↗</a>
    </div>`, { maxWidth:240, className:'dest-popup' });
}

function initRideMap(lat, lng, acc) {
  userLL = { lat, lng };
  lastLat = lat; lastLng = lng; lastTime = Date.now();

  reverseGeocode(lat, lng).then(addr => {
    document.getElementById('j-src').textContent      = addr;
    document.getElementById('rfp-origin').textContent = addr;
  });
  document.getElementById('pill-coords').textContent = `${lat.toFixed(5)}, ${lng.toFixed(5)}`;

  const uIcon = L.divIcon({
    html:`<div style="width:18px;height:18px;background:linear-gradient(135deg,#f759a0,#4db8f5);
         border-radius:50%;border:3px solid white;box-shadow:0 0 0 6px rgba(247,89,160,.25);"></div>`,
    className:'', iconSize:[18,18], iconAnchor:[9,9]
  });
  userMarker = L.marker([lat, lng], { icon: uIcon }).addTo(map)
    .bindPopup(`<b>📍 ${U.name || 'You'}</b>${acc ? `<br/><small>Accuracy: ±${Math.round(acc)} m</small>` : ''}`);
  map.setView([lat, lng], 15, { animate: true });

  if (DEST_LAT !== null && DEST_LNG !== null) {
    destLL = { lat: DEST_LAT, lng: DEST_LNG };
    placeDestMarker(DEST_LAT, DEST_LNG, DEST);
    drawColoredOSRMRoute(lat, lng, DEST_LAT, DEST_LNG);
    routeSegments = interpolatePoints(lat, lng, DEST_LAT, DEST_LNG, 10);
    startFullRouteScan();
    document.getElementById('j-dst').textContent    = DEST;
    document.getElementById('rfp-dest').textContent = DEST;
    document.getElementById('click-dest-banner').classList.remove('show');
  }

  queryOverpassAndAssess(lat, lng);
  toast('ok','📍 Location found',`${lat.toFixed(4)}, ${lng.toFixed(4)} — tap map to set destination`);
}

function interpolatePoints(fLat, fLng, tLat, tLng, n) {
  return Array.from({ length: n + 1 }, (_, i) => {
    const t = i / n;
    return { lat: fLat + (tLat - fLat) * t, lng: fLng + (tLng - fLng) * t };
  });
}

function drawColoredOSRMRoute(lat1, lng1, lat2, lng2) {
  coloredRouteLayers.forEach(l => map.removeLayer(l));
  coloredRouteLayers = [];
  if (routeControl) { try { map.removeControl(routeControl); } catch(_){} routeControl = null; }

  const url = `https://router.project-osrm.org/route/v1/driving/${lng1},${lat1};${lng2},${lat2}?overview=full&geometries=geojson&steps=false`;

  fetch(url, { signal: AbortSignal.timeout(12000) })
    .then(r => r.json())
    .then(data => {
      if (!data.routes || !data.routes[0]) { fallbackRoute(lat1,lng1,lat2,lng2); return; }
      const coords = data.routes[0].geometry.coordinates;
      const distKm = (data.routes[0].distance / 1000).toFixed(1);
      const durMin = Math.round(data.routes[0].duration / 60);
      document.getElementById('pill-eta-txt').textContent = `${distKm} km · ~${durMin} min`;
      colorAndDrawRoute(coords);
    })
    .catch(() => fallbackRoute(lat1, lng1, lat2, lng2));
}

async function colorAndDrawRoute(coords) {
  const step   = Math.max(1, Math.floor(coords.length / 12));
  const samples = [];
  for (let i = 0; i < coords.length; i += step) samples.push(i);

  const riskMap = {};
  for (const idx of samples) {
    const [lng, lat] = coords[idx];
    const risk = await getSegmentRisk(lat, lng);
    riskMap[idx] = risk;
    await sleep(400);
  }

  const colorOf = r => r === 'safe' ? '#22c55e' : r === 'moderate' ? '#f97316' : '#ef4444';
  let prevIdx = null;
  for (const idx of samples) {
    if (prevIdx !== null) {
      const segCoords = coords.slice(prevIdx, idx + 1).map(c => [c[1], c[0]]);
      const line = L.polyline(segCoords, {
        color: colorOf(riskMap[idx]), weight: 6, opacity: 0.9,
        lineCap: 'round', lineJoin: 'round'
      }).addTo(map);
      coloredRouteLayers.push(line);
    }
    prevIdx = idx;
  }

  const latLngs = coords.map(c => [c[1], c[0]]);
  if (latLngs.length) map.fitBounds(L.latLngBounds(latLngs), { padding: [40, 40] });
}

function fallbackRoute(lat1, lng1, lat2, lng2) {
  const line = L.polyline([[lat1,lng1],[lat2,lng2]], {
    color: isDangerWindow() ? '#f97316' : '#22c55e',
    weight: 5, opacity: 0.75, dashArray: '10,6'
  }).addTo(map);
  coloredRouteLayers.push(line);
  map.fitBounds([[lat1,lng1],[lat2,lng2]], { padding: [40,40] });
}

async function getSegmentRisk(lat, lng) {
  const q = `[out:json][timeout:8];
(
  way["highway"](around:80,${lat},${lng});
  node["highway"="street_lamp"](around:80,${lat},${lng});
);
out tags;`;
  try {
    const res = await fetch('https://overpass-api.de/api/interpreter',{
      method:'POST', body:'data='+encodeURIComponent(q), signal:AbortSignal.timeout(10000)
    });
    if (!res.ok) return isDangerWindow() ? 'danger' : 'safe';
    const json = await res.json();
    return classifyRisk(json.elements || [], null).level;
  } catch(_){ return isDangerWindow() ? 'danger' : 'safe'; }
}

function classifyRisk(elements, crowdCount) {
  if (!isDangerWindow()) {
    return { level:'safe', score:0, icon:'🟢', label:'Safe (Daytime)', factors:['☀️ Daytime — safe by default'], hwTypes:[], lampCount:0, crowdLabel:'N/A', hasLights:true, noLights:false, isIsolated:false };
  }

  const roads = elements.filter(e => e.type==='way' && e.tags?.highway).map(e => e.tags);
  const lamps = elements.filter(e => e.type==='node' && e.tags?.highway==='street_lamp');

  const hwTypes    = [...new Set(roads.map(r => r.highway).filter(Boolean))];
  const litVals    = roads.map(r => r.lit).filter(Boolean);
  const surfaces   = roads.map(r => r.surface).filter(Boolean);

  const isMajor       = hwTypes.some(t => MAJOR_HW.has(t));
  const isSafeHw      = hwTypes.some(t => SAFE_HW.has(t));
  const isIsolated    = hwTypes.some(t => HIGH_RISK.has(t)) && !isSafeHw;
  const isResidential = hwTypes.some(t => ['residential','living_street'].includes(t));

  const taggedLit   = litVals.some(v => v==='yes');
  const taggedUnlit = litVals.length > 0 && litVals.every(v => v==='no');
  const lampCount   = lamps.length;
  const hasLights   = taggedLit || lampCount >= 2;
  const noLights    = taggedUnlit || (lampCount===0 && !taggedLit);

  let crowdScore = 0, crowdLabel = 'Unknown';
  if (crowdCount !== null) {
    if      (crowdCount >= 10) { crowdScore=0; crowdLabel=`High crowd (${crowdCount} places)`; }
    else if (crowdCount >= 4)  { crowdScore=1; crowdLabel=`Moderate crowd (${crowdCount} places)`; }
    else if (crowdCount >= 1)  { crowdScore=2; crowdLabel=`Low crowd (${crowdCount} places)`; }
    else                       { crowdScore=3; crowdLabel='No crowd detected'; }
  } else {
    if (isMajor)         { crowdScore=0; crowdLabel='Busy (major road)'; }
    else if (isSafeHw)   { crowdScore=1; crowdLabel='Moderate traffic'; }
    else if (isIsolated) { crowdScore=3; crowdLabel='Likely isolated'; }
    else                 { crowdScore=2; crowdLabel='Unknown crowd'; }
  }

  let score = 0;
  const factors = ['🌙 Night window active (6:30 PM–6 AM)'];

  if (roads.length===0)       { score+=4; factors.push('⚠️ No road data — unmapped area'); }
  else if (isMajor)           { factors.push('✅ Major highway — high visibility'); }
  else if (isResidential)     { score+=1; factors.push('Residential road'); }
  else if (isIsolated)        { score+=3; factors.push('⚠️ Isolated / rural road'); }

  if (hasLights)                         { factors.push(`✅ Street lighting (${lampCount} lamps)`); }
  else if (noLights && isIsolated)       { score+=4; factors.push('🌑 DARK + ISOLATED — no lights!'); }
  else if (noLights)                     { score+=3; factors.push('🌑 No street lighting detected'); }
  else if (lampCount < 2)                { score+=1; factors.push(`Minimal lighting (${lampCount} lamps)`); }

  score += crowdScore;
  factors.push(crowdScore<=1 ? `👥 ${crowdLabel}` : `⚠️ ${crowdLabel}`);

  const hasUnpaved = surfaces.some(s=>['unpaved','dirt','gravel','ground','sand','mud','grass'].includes(s));
  const hasPaved   = surfaces.some(s=>['asphalt','concrete','paved'].includes(s));
  if (hasUnpaved && !hasPaved) { score+=2; factors.push('⚠️ Unpaved surface'); }
  else if (hasPaved)           { factors.push('Paved surface ✅'); }

  const isPrivate = roads.some(r => r.access==='private');
  if (isPrivate) { score+=2; factors.push('🚫 Private / restricted road'); }

  const tripleTheat = isDangerWindow() && noLights && crowdScore>=3;
  if (tripleTheat) score = Math.max(score, 9);

  let level, icon, label;
  if      (score>=7) { level='danger';   icon='🔴'; label='DANGEROUS — Night + Dark + Isolated'; }
  else if (score>=4) { level='moderate'; icon='🟠'; label='Caution — Some risk factors'; }
  else               { level='safe';     icon='🟢'; label='Safe — Well-lit or busy area'; }

  return { level, score, icon, label, factors, hwTypes, lampCount, crowdLabel, hasLights, noLights, isIsolated };
}

async function queryOverpassAndAssess(lat, lng) {
  if (lastOverpassLL && getDistance(lat,lng,lastOverpassLL.lat,lastOverpassLL.lng)<150) return;
  lastOverpassLL = { lat, lng };

  document.getElementById('overpass-status').innerHTML =
    '<span class="ov-spinner"></span> Querying OSM road & crowd data…';

  const roadQ = `[out:json][timeout:10];
(
  way["highway"](around:120,${lat},${lng});
  node["highway"="street_lamp"](around:100,${lat},${lng});
);
out tags;`;

  try {
    const [roadRes, crowdCount] = await Promise.all([
      fetch('https://overpass-api.de/api/interpreter',{
        method:'POST', body:'data='+encodeURIComponent(roadQ), signal:AbortSignal.timeout(10000)
      }),
      queryCrowdData(lat, lng)
    ]);

    if (!roadRes.ok) throw new Error();
    const roadData = await roadRes.json();
    const elements = roadData.elements || [];
    const result   = classifyRisk(elements, crowdCount);
    currentRisk    = result.level;
    updateRiskUI(result);

    const tagHtml = result.hwTypes.slice(0,5).map(t => {
      let cls = MAJOR_HW.has(t)?'rt-good':HIGH_RISK.has(t)?'rt-bad':isDangerWindow()?'rt-warn':'rt-info';
      return `<span class="road-tag ${cls}">${t}</span>`;
    }).join('');
    if (isDangerWindow()) {
      const lightTag = result.hasLights?`<span class="road-tag rt-good">💡 Lit</span>`:`<span class="road-tag rt-bad">🌑 Unlit</span>`;
      const crowdTag = `<span class="road-tag ${parseInt(crowdCount)>=4?'rt-good':parseInt(crowdCount)>=1?'rt-warn':'rt-bad'}">👥 ${result.crowdLabel.split(' ')[0]}</span>`;
      document.getElementById('road-tags').innerHTML = (tagHtml||'') + lightTag + crowdTag;
    } else {
      document.getElementById('road-tags').innerHTML = tagHtml||'<span class="road-tag rt-info">Roads detected</span>';
    }

    const roads = elements.filter(e=>e.type==='way');
    document.getElementById('overpass-status').textContent =
      `✅ ${roads.length} road(s) · ${result.lampCount} lamp(s) · ${crowdCount} crowd indicators`;

  } catch(e) {
    const fallbackLevel = isDangerWindow() ? 'danger' : 'safe';
    currentRisk = fallbackLevel;
    updateRiskUI({
      level:fallbackLevel, score:isDangerWindow()?8:0,
      icon:isDangerWindow()?'🔴':'🟢',
      label:isDangerWindow()?'No road data — assuming danger at night':'Daytime — assumed safe',
      factors:isDangerWindow()?['🌙 Night window active','⚠️ No road data','🌑 Cannot confirm lighting','⚠️ Cannot confirm crowd']:['☀️ Daytime — safe by default'],
      hwTypes:[], lampCount:0, crowdLabel:'Unknown', hasLights:false, noLights:true, isIsolated:false
    });
    document.getElementById('overpass-status').textContent = '⚠️ Road data unavailable';
  }
}

function updateRiskUI(result) {
  const { level, score, icon, label, factors } = result;
  const badge = document.getElementById('risk-badge');
  badge.className = `risk-badge risk-${level}`;
  document.getElementById('risk-title').textContent   = `${icon} ${label}`;
  document.getElementById('risk-reason').textContent  = isDangerWindow()?'Night risk assessment active':'Daytime — standard safety';
  const factEl = document.getElementById('risk-factors');
  factEl.innerHTML = factors.map(f=>`<div class="risk-factor-item"><span>▸</span>${f}</div>`).join('');

  const scoreSection = document.getElementById('risk-score-section');
  if (isDangerWindow()) {
    scoreSection.style.display = 'block';
    const pct  = Math.min(100,(score/10)*100);
    const fill = document.getElementById('risk-score-fill');
    fill.style.width      = pct+'%';
    fill.style.background = level==='safe'?'#22c55e':level==='moderate'?'#f97316':'#ef4444';
    document.getElementById('risk-score-num').textContent = `${score}/10`;
    document.getElementById('risk-score-num').style.color = level==='safe'?'#166534':level==='moderate'?'#92400e':'#991b1b';
  } else {
    scoreSection.style.display = 'none';
  }

  const overlay = document.getElementById('risk-overlay');
  overlay.className = `risk-overlay ${level}`;
  document.getElementById('risk-ov-ic').textContent  = icon;
  document.getElementById('risk-ov-txt').textContent = label;
  document.getElementById('risk-ov-sub').textContent = factors.slice(1,3).join(' · ')||'';

  if (level==='danger') {
    addAlert('danger',`🔴 DANGER: ${label}`);
    if (ECS.length>0) toast('warn','⚠️ Danger zone detected','Consider sending alert to contacts');
  }
}

async function startFullRouteScan() {
  if (segScanRunning || routeSegments.length===0) return;
  segScanRunning = true; segmentResults = [];
  const total = routeSegments.length;
  document.getElementById('seg-progress-label').innerHTML = '<span class="ov-spinner"></span> Scanning route via OpenStreetMap…';
  document.getElementById('danger-seg-list').innerHTML = '<div class="danger-seg safe"><span style="margin-right:5px;">🔍</span>Scanning route segments…</div>';

  for (let i=0; i<total; i++) {
    const pt  = routeSegments[i];
    const pct = Math.round(((i+1)/total)*100);
    document.getElementById('seg-fill').style.width = pct+'%';
    document.getElementById('seg-status').textContent = `Segment ${i+1} of ${total}`;
    try {
      const result = await fullSegmentAnalyze(pt.lat, pt.lng, i, total);
      segmentResults.push(result);
    } catch(_) {
      segmentResults.push({
        idx:i, level:isDangerWindow()?'danger':'safe',
        icon:isDangerWindow()?'🔴':'🟢',
        label:isDangerWindow()?'Danger (no data at night)':'Safe (daytime)',
        factors:['No OSM data'], segLabel:`Seg ${i+1}/${total}`,
        hwTypes:[], lampCount:0, crowdLabel:'Unknown', hasLights:false, noLights:true
      });
    }
    await sleep(1000);
  }

  document.getElementById('seg-progress-label').innerHTML = '✅ Route scan complete';
  document.getElementById('seg-status').textContent = `${total} segments analysed`;
  renderSegmentResults();
  segScanRunning = false;
}

async function fullSegmentAnalyze(lat, lng, idx, total) {
  const roadQ = `[out:json][timeout:10];
(
  way["highway"](around:150,${lat},${lng});
  node["highway"="street_lamp"](around:100,${lat},${lng});
);
out tags;`;

  const [roadRes, crowd] = await Promise.all([
    fetch('https://overpass-api.de/api/interpreter',{
      method:'POST', body:'data='+encodeURIComponent(roadQ), signal:AbortSignal.timeout(12000)
    }),
    queryCrowdData(lat, lng)
  ]);

  const json   = roadRes.ok ? await roadRes.json() : { elements:[] };
  const result = classifyRisk(json.elements||[], crowd);
  return { ...result, idx, segLabel:`Seg ${idx+1}/${total}`, crowdCount:crowd };
}

function renderSegmentResults() {
  const dangerous = segmentResults.filter(s=>s.level==='danger').length;
  const moderate  = segmentResults.filter(s=>s.level==='moderate').length;

  let overallLevel, overallIcon, overallLabel, overallClass;
  if (dangerous>0)     { overallLevel='danger';   overallIcon='🔴'; overallLabel='Dangerous Route'; overallClass='rfp-danger'; }
  else if (moderate>0) { overallLevel='moderate'; overallIcon='🟠'; overallLabel='Moderate Risk';   overallClass='rfp-mod'; }
  else                 { overallLevel='safe';     overallIcon='🟢'; overallLabel='Safe Route';      overallClass='rfp-safe'; }

  document.getElementById('rfp-title').textContent = `${DEST} — Route`;
  const badge = document.getElementById('rfp-overall-badge');
