# Fix ArticleList filters/search + clean up Layout

## Context
In `ArticleList.jsx`, selecting a filter or running a search causes the selection to visually
"disappear" and "Clear Filters" appears to do nothing. Two compounding root causes:

1. The component returns **only** a full-page `<Spin>` while `loading` is true
   (`ArticleList.jsx:83-89`), which unmounts the entire filter bar on every fetch.
2. All `<Select>` inputs and the `<Search>` box are **uncontrolled** (no `value` bound to
   `filters`), so re-mounts reset them to placeholders and "Clear Filters" cannot push the
   cleared state back into the UI.

Separately, `Layout.jsx` has dead navigation (4 of 5 menu items link to `/`, with keys that
don't exist in the router) and a hardcoded `Badge count={5}` placeholder shown twice.

Decisions confirmed with the user: fix **both** ArticleList root causes; **remove** the dead
Layout menu items; **wire the badge to the real unread count**.

## Changes

### 1. `src/components/ArticleList.jsx`

**a. Make all filter inputs controlled** (bind `value` to `filters`):
- Each `<Select>` gets `value={filters.category}` / `type` / `source` / `impact` / `status`
  (`ArticleList.jsx:104-162`).
- `<Search>`: control its text. Add a `value={filters.q ?? ''}` plus an `onChange` that updates
  `filters.q` locally (without resetting page), keeping `onSearch={handleSearch}` for
  submit/enter. This makes it clear correctly when "Clear Filters" runs.
- Now `setFilters({ page: 0, size: 10 })` in "Clear Filters" (`line 166`) visibly resets every
  control because they read from `filters`.

**b. Stop unmounting the filter bar during loads.** Remove the early
`if (loading) return <Spin/>` block (`ArticleList.jsx:83-89`). Instead wrap the article list
region in `<Spin spinning={loading}>` so the filter Card stays mounted and only the list shows
the loading state. Keeps focus/selection stable and kills the blank-page flash.

**c. Normalize empty search.** In `handleSearch` (`line 74-76`) convert `''` to `null` so an
empty query isn't sent to the backend as `q=`.

**d. Remove dead import.** Drop `ReloadOutlined` from the `@ant-design/icons` import
(`line 3`) - it is never used.

### 2. `src/components/Layout.jsx`

**a. Remove dead menu items.** Keep only the Dashboard item (the only real route). Delete the
Articles, Unread, Profile[index.css](src/index.css), and Settings entries (`Layout.jsx:29-48`) and their now-unused icon
imports (`ReadOutlined`, `UserOutlined`, `SettingOutlined`).
Note: `BellOutlined` is still used by the header bell, so keep it.

**b. Wire the header unread badge to the real count.** Replace the hardcoded
`Badge count={5}` on the header bell (`line 111`) with state fetched from
`articleApi.getUnreadCount()` (the request interceptor injects `userId=1`). Response shape is
`{ unreadCount: number }` (confirmed in `UnreadCountDto.java`). Add a `useState` +
`useEffect` in `Layout` to load it on mount and read `response.data.unreadCount`.

## Reuse notes
- `articleApi.getUnreadCount` already exists (`src/api/client.js`) and the interceptor supplies
  `userId=1`, so no arg is required.
- No new dependencies; all components (`Spin`, `Badge`, `Select`, `Search`) already imported.

## Verification
1. `npm run dev` in `frontend/` (backend running on `localhost:8089`).
2. Select each filter (Category/Type/Source/Impact/Status) and run a search - confirm the
   selected value stays visible in the control and the list updates without a full-page flash.
3. Click "Clear Filters" - confirm all dropdowns and the search box reset and the full list
   returns.
4. Clear the search box (x) - confirm results return to unfiltered.
5. Confirm the sidebar shows only Dashboard, and the header bell badge shows the real unread
   count (not a hardcoded 5).
