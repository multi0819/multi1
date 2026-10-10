# 여행기록 Android redesign

Goal: A polished Korean personal travel diary, retaining legacy JSON records and photos.

Cream paper, deep teal, restrained copper accents, SVG line icons, generous spacing and photo-led cards. Four bottom destinations: home, trips, map, archive. No fake records. Empty states lead to creating or importing records.

Trips retain IDs, dates, region/city, notes, cover, bookmarks and entries. New fields: status visited/planned, budget KRW and entry expense KRW. Planned trips are excluded from visit statistics. Entries support multiple photos, day/time, categories, ratings and paragraphs. New photos are resized locally; phone originals are untouched.

Map: bundled Leaflet with online OpenStreetMap tiles and visible attribution; saved valid coordinates first, existing city office coordinates second. No province-center fallback. Unknown coordinates can be set manually using latitude/longitude. Domestic/overseas filters and marker-to-trip navigation. Imported coordinates are preserved. No paid APIs or location permission.

Reports: select trips and sections, render continuous A4 text with headings and photos, use native print/save PDF. JSON export/import uses Android document picker. Import validates the complete payload before one transaction, merges matching IDs, never clears existing records. No remote storage.

Android minSdk 26, targetSdk 35; secure local WebView origin, safe-area and keyboard insets, native document/file selection. Map is online; records and photos are offline.
