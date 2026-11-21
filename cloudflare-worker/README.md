# Early Enlightenment — Cloudflare Worker AI Setup Guide

This directory contains the reproducible source code for the **Cloudflare Workers AI** backend used by Early Enlightenment.

## Cloudflare Configuration Details
- **Worker Name:** `early-enlightenment-aicopy`
- **Workers AI Binding Name:** `AI`
- **Model:** `@cf/meta/llama-3.1-8b-instruct-fast`
- **Deployed URL:** `https://early-enlightenment-aicopy.jbridg2.workers.dev`

---

## Deploying Your Own Worker (Optional)

If a team member wants to deploy their own independent Worker instead of using the hosted endpoint:

1. **IMPORTANT — Choose a Unique Worker Name:**
   Open `wrangler.toml` in this folder and change `name = "early-enlightenment-aicopy"` to a unique name (e.g., `name = "early-enlightenment-peyton"` or `name = "early-enlightenment-taylor"`). **Do not use `early-enlightenment-aicopy`**, as that will overwrite the existing team deployment.

2. **Install Wrangler CLI:**
   ```bash
   npm install -g wrangler
   ```
3. **Login to Cloudflare Account:**
   ```bash
   wrangler login
   ```
4. **Deploy Your Worker:**
   ```bash
   wrangler deploy
   ```
5. **Copy Your Worker URL:** Copy your new unique URL (e.g., `https://early-enlightenment-yourname.workers.dev`) and update `url = URL("https://early-enlightenment-yourname.workers.dev")` in `MainActivity.kt`.
