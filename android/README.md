# 여행기록 Android 1.2.0

사진 중심 홈, 여행 목록, 날짜별 타임라인, 사진 모음, 국내/해외 방문 지도, 별점 조회, 즐겨찾기, 여행 예정 표시, 원화 경비/예산, 방문 통계와 사진 포함 PDF.

기록과 사진은 기기 안에 저장됩니다. 배경지도는 인터넷 연결이 필요합니다. 구독/유료 API/계정은 없습니다. 여행 예정은 방문 통계에서 제외합니다. 좌표를 모르는 도시를 도 대표 위치로 임의 표시하지 않습니다. 해외 도시는 기존 앱의 도시 좌표를 사용하며, 필요하면 여행 수정에서 직접 좌표를 지정할 수 있습니다.

## 기존 웹앱 기록 가져오기
1. 기존 웹앱의 관리 화면에서 JSON 백업을 저장합니다.
2. APK를 설치하고 보관함 → 백업 가져오기를 선택합니다.
3. 같은 여행 ID는 백업 내용으로 갱신되며, 다른 기존 여행은 유지됩니다. 앱 삭제/기기 변경 전에 JSON 백업을 보관하세요.

## 일상 기록
하단 일상 메뉴에서 여행 없이 음식·카페·일상을 기록합니다. 여러 사진, 장소·주소, 메뉴, 여러 메뉴의 단가·수량과 자동 합계, 별도 실제 결제금액, 별점, 재방문 여부를 선택해 저장합니다. 같은 장소 기록 보기, 검색과 가격·별점 필터, 선택 기록 PDF를 지원합니다. 여행 방문 통계와 분리되며 JSON 백업에는 여행·일상·사진이 함께 포함됩니다. 기존 앱을 삭제하지 않고 APK를 덮어 설치하면 기록을 유지합니다.

## PDF
보관함 → 여행 PDF 만들기에서 여행과 사진/경비/소개 포함 여부를 선택합니다. 미리보기를 확인하고 PDF 저장/인쇄에서 Android 인쇄 대상 **PDF로 저장**을 선택합니다. Android 인쇄 화면에서 필요한 출력 페이지 범위도 선택할 수 있습니다. 기록을 한 장씩 강제 분리하지 않으며 긴 글은 문단 단위로 이어집니다.

## Build
JDK 17, Gradle 8.11.1, Android SDK 35. The repository root retains the previous web app; this Android project lives in `android/` on branch `travel-apk`.

```bash
cd android
python3 scripts/restore-assets.py
gradle assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk`.
Android 8.0+ (API 26), target API 35, application ID `com.multi0819.travelarchive`.

Signing material in `signing/travel.jks.b64` is a **public development signing key** (alias travel, password android), supplied to reproduce updates to this personally shared APK. Use a new private key kept outside the repository for a Play Store production release. Do not overwrite this key for updates to already installed copies.

## Testing
`node --test scripts/*.test.cjs` from project root. `scripts/browser.cjs` is a workspace integration harness with explicit local paths for Chromium and fonts; adapt those paths when running elsewhere. `docs/verification.md` records validation and device testing limits.

Leaflet 1.9.4 bundled under BSD-2-Clause (`LEAFLET-LICENSE.txt`); OpenStreetMap attribution remains visible. Hero background is an AI-generated illustrative brand image, not a saved user photograph. Records are never sent to a server. The map tile provider receives tile requests while viewing a map: https://operations.osmfoundation.org/policies/tiles/.
