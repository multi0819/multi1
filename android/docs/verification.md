# Verification

Executed: Node unit tests and real headless Chromium 153 mobile (412×915) / tablet (820×1180) integration tests.

23 unit cases cover legacy photos and coordinates, invalid import atomicity, dates, expenses, planned visits, search, duplicate IDs, out-of-range days, concurrent export ownership and photo processing locks. Chromium verifies real IndexedDB CRUD/reload/photo persistence, JSON export, map markers, report selection, photos and responsive views. PDF layout inspected using Poppler; Android Kotlin compiled against API 35 and signed APK verified separately.

Final independent code review found four important issues; all reproduced RED then fixed GREEN: concurrent export cancellation, duplicate entry IDs, inaccessible imported days and overlapping photo batches. No deferred findings.

No Android emulator/device available here. Native SAF and system print dialogs, OEM keyboard/system bars and live map networking require final testing on the user's phone. Map tiles are intentionally online; no offline tile downloading.

## 1.1.0 daily records
Validated old version-1 IndexedDB upgrade with existing trip preserved; daily CRUD and multiple photos; optional prices/rating; revisit and same-place history; combined search/price filters; selected photo PDF; combined JSON backup; malformed dual import rejected atomically; legacy import preserves daily records; offline reload; mobile/tablet layouts. Independent review found no important issues. Favorite-place count duplication reproduced then fixed with regression test. Update APK versionCode 2 retains the original package and certificate. Native Kotlin unchanged from 1.0.0, whose compiled DEX is reused. Assets verified byte-for-byte against sources; APK signature verified. Native device limitations above remain.

## 1.2.0 menu prices
23 unit cases pass. Real browser integration validates multiple menu rows, unit prices and quantities, automatic complete sum, independently entered actual payment (discount example 18,000 vs 17,000), clearing old prices and actual payment without refill, row deletion/addition, photo PDF menu breakdown, item backup roundtrip, legacy single-price records retained as a row, menu search, offline and existing trip stats. Existing daily and travel browser suites pass. Mobile form/detail and generated PDF inspected. Blank prices remain null; partial menu sums display unrecorded; zero remains free. Update uses versionCode 3 with the original package/signing certificate and unchanged native Kotlin/DEX. Native phone dialogs still require device testing.

Review subtotal-limit issue with partially missing prices reproduced and fixed; regression passes.
