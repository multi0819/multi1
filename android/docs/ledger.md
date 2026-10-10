# Implementation ledger

User delegated feature recommendations and implementation; design and plan implemented inline.

Task 1: model tests RED (missing model) → GREEN 7/7; legacy schema, photos and coordinates preserved; transactional merge.
Task 2: real Chromium test RED (missing UI) → GREEN mobile CRUD/photos/expense/reload/map/report/tablet; image-led design and SVG line icons; bundled Leaflet and offline records.
Task 3: Kotlin compile API35 green (deprecated system-bar compatibility APIs generate warnings); local APK signing and binary validation.
Final independent review: four Important findings. Each reproduced before fix: export ownership, duplicate entry IDs, unsupported days and overlapping photo batches. Unit suite GREEN 11/11. No deferred minor findings.

Device-only SAF/print and OEM insets remain final user phone verification; no emulator available. Currency explicitly KRW to avoid inventing exchange rates. Unknown map locations require explicit coordinates rather than fabricated province pins.
