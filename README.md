# 80s & 90s Old Is Gold — Native Background Radio App

This is a mobile-friendly Android project for the existing Old Is Gold website.

Features:
- Existing website UI loaded in WebView
- Native Media3/ExoPlayer radio playback
- MediaSessionService for background playback
- Lock-screen/system media controls
- 9 existing Zeno radio streams
- Generated Old Gold logo used as app icon and media artwork
- GitHub Actions workflow to build APK without Android Studio on the phone

Official Android guidance recommends putting the player and MediaSession in a MediaSessionService for background playback.
