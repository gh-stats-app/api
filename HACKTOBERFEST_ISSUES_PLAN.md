### Hacktoberfest issue plan

This plan summarizes the open GitHub issues labeled `hacktoberfest` and groups the remaining achievement work by implementation complexity.

#### Current audit

- Found `58` open labeled issues.
- About `33` already have a matching or near-matching local achievement class in `src/main/java/ghstats/api/achievements/impl`.
- About `25` still appear missing, incomplete, or mismatched.
- The common implementation pattern is to add an `UnlockableAchievement` Spring component, inspect `List<GitCommit>`, and add focused tests under `src/test/java/ghstats/api/achievements/impl`.

#### First: audit already-implemented open issues

Before adding new work, verify and possibly close or reconcile already-present achievements such as:

- `Not a Web Scale`
- `Salvation`
- `Scribbler`
- `Back on the Rails`
- `Multilingual`
- `Flash`
- `Eraser`

Special mismatch:

- `#69 Achievement - Get` appears implemented as `ExactMatch` with description `Commit exactly at 00:00`. Decide whether to keep the local `ExactMatch` name/id or rename metadata to match `Get`.

#### Batch 1: no GitHub API/model changes

These can likely be implemented using current `GitCommit` fields: message, author, timestamp, SHA, and commit list ordering.

- `#8 Catchphrase` — `10+` commits with the same message.
- `#6 Blamer` — commit message mentions another author seen in the commit list.
- `#14 Combo Breaker` — someone commits after another user made `10+` commits in a row.
- `#76 Worker Bee` — `100+` commits, but merge detection should be clarified or added.
- `#12 Collision` — same SHA prefix as another commit; issue needs clarification for value of `N`.

#### Batch 2: meta-achievements / engine-level changes

These depend on achievement unlock results, not just raw commits.

- `#49 Munchkin` — one commit unlocks `5` achievements.
- `#62 Quest Complete` — user gets all achievements.
- `#70 Unpretending` — no achievements after `100` own commits.

These should be implemented after deciding whether meta-achievements belong inside `AchievementsCommand` or as a separate post-processing layer to avoid circular unlock logic.

#### Batch 3: extend GitHub file metadata

Current `GithubClient` only maps PR files into simple added/removed/modified filename lists. To support these issues, extend `PullRequestFileResponse`, `DiffResult`, and likely `GitCommit` with file metadata like `additions`, `deletions`, `changes`, `status`, `previous_filename`, and possibly `patch`.

- `#46 Massive` — more than `1000` lines added.
- `#47 Mover` — renamed/moved file without content changes.
- `#24 Fat Ass` — file size requires extra data beyond current PR file response.
- `#19 Easy Fix` — swap two lines; needs patch/content diff.
- `#15 Commenter` — only add a comment; needs patch/content analysis.
- `#34 Holy War` — tabs/spaces conversion; needs patch/content analysis.
- `#56 OCD` — only trailing spaces removed; needs patch/content analysis.

#### Batch 4: extend commit metadata

Add fields from GitHub commit responses, especially parent SHAs, to support:

- `#35 Hydra` — commit with `3+` parents.
- More accurate `#76 Worker Bee` — exclude merge commits using parent count instead of message heuristics.

#### Batch 5: repository-history achievements

These need repo-level history, not just commits/files from the current PR.

- `#1 All Things Die` — delete a file from initial commit after a year.
- `#3 Anniversary` — commit on project birthday.
- `#41 Loneliness` — only committer for a month.
- `#51 Necromancer` — commit to repo untouched for at least one month.
- `#73 What Happened Here?` — edit file untouched for a year.

Plan for these: add repository metadata/history fetching to `GithubClient`, then introduce a richer achievement input model than `List<GitCommit>`.

#### Batch 6: revert / semantic / ambiguous achievements

These need product-rule clarification or richer diff/commit relationship logic.

- `#57 Ooops` — commit and revert within `1` minute.
- `#72 Waste` — commit completely reverted by someone else.
- `#7 Borat` — misspell a word in commit message; requires dictionary/heuristic decision.

#### Verification plan

For each achievement:

- Add positive, negative, and edge-case tests.
- Verify ID/name/description produce the expected icon path.
- Run relevant achievement tests, then full `./gradlew test` before considering the batch complete.

Recommended next step: start with `Batch 1`, because it delivers several achievements without changing the GitHub integration model.