# Travel APK Implementation Plan

> For agentic workers: Use superpowers:executing-plans to implement inline with a final independent review.

**Goal:** Deliver a polished travel diary APK with legacy import, expenses, visit statistics and illustrated PDF reports.
**Architecture:** Portable model functions, IndexedDB transaction storage, modular vanilla UI and Leaflet map. Native Kotlin WebView wrapper owns file selection/export and printing.
**Tech Stack:** JavaScript, CSS, Leaflet 1.9.4, Kotlin, Android 35.
**Spec:** ../../design.md

## Global Constraints
- Korean UI; minSdk 26; targetSdk 35; no paid API.
- Existing records/photos merge through JSON; no destructive automatic migration.
- Expenses/budgets use KRW, planned trips excluded from visited statistics.
- Known city-office or saved coordinates; unknown locations never use a province center.
- Online map tiles with attribution; offline records and photos.

## Review Focus
- Invalid imported dates/coordinates must not corrupt existing records.
- Photo picker cancellation must preserve existing photos.
- Long paragraphs and photo groups must remain readable in multipage PDFs.
- Duplicate trip IDs merge, malformed payload imports nothing.
- Native keyboard/system bars must not cover controls.

### Task 1: Model and storage
Files: assets/model.js, assets/storage.js, scripts/model.test.cjs.
Interfaces: normalizeTrip(raw), prepareImport(payload), stats(trips), totalExpense(trip), matches(trip,query), validCoord(value).
- [ ] Write tests for legacy photo/coordinate import, malformed import rejection, planned visit exclusion, expense totals, text search and reversed dates.
- [ ] Run node --test scripts/model.test.cjs and observe missing model failure.
- [ ] Implement model and atomic IndexedDB import.
- [ ] Run all model tests green.

### Task 2: Diary, map and reports
Files: assets/index.html, styles.css, app.js, locations.js, report.js, native.js, bundled Leaflet.
Interfaces: TripStore.list/save/remove/import; TravelModel exports from task 1.
- [ ] Write browser scenarios for empty state, create/edit, entry photos/expenses, reload, backup merge and PDF selection.
- [ ] Observe missing app failure, implement responsive UI and report flows.
- [ ] Run real Chromium scenarios and inspect phone/tablet screenshots and multipage PDF.

### Task 3: Android delivery
Files: MainActivity.kt, AndroidManifest.xml, Gradle config, icon, signing material and README.
- [ ] Verify native wrapper handles local files, network allowlist, SAF, back and print.
- [ ] Compile fresh Kotlin classes, DEX, resources; sign APK.
- [ ] Verify application ID/version, APK signing, asset equality.
- [ ] Review final branch, fix important findings and rerun tests.
- [ ] Commit source to GitHub travel-apk branch; deliver saved APK.
