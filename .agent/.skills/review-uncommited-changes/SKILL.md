---
name: review_uncommitted_changes
description: Internal use. Performs raw diff analysis. Do not call directly unless explicitly requested.

---

## Prerequisites

- Project MUST have uncommitted changes
- If no changes detected → respond exactly:  
  "No code changes"

---

## Execution Rules (MANDATORY)

You MUST execute the following commands in order:

1. git status --porcelain
2. git --no-pager diff --no-ext-diff -- . ':!.skills'
3. git --no-pager diff --no-ext-diff --staged -- . ':!.skills'

Hard constraints:
- ALWAYS use `--no-pager`
- ALWAYS disable external diff via `-c diff.external=`
- NEVER run plain `git diff`
- NEVER rely on interactive output

---

## Ignore Rules

The following paths MUST be excluded from review:

- .skills/**
- build/**
- .gradle/**

These paths must NOT appear in:
- file list
- diff analysis
- issues

## Step 1: Detect Changed Files

- Parse git status --porcelain
- List all affected files:
    - Modified (M)
    - Added (A)
    - Deleted (D)
    - Untracked (??)

- If no files → return "No code changes"

---

## Step 2: Safe Diff Extraction (IMPORTANT)

### Primary attempt

Run:
git --no-pager diff --no-ext-diff -- . ':!.skills'
git --no-pager diff --no-ext-diff --staged -- . ':!.skills'

---

### Fallback Strategy (if diff is slow, stuck, or too large)

Retry using:

1. File list only:
   git --no-pager diff --name-only

2. Summary:
   git --no-pager diff --stat

3. Scoped diff (exclude heavy dirs):
   git --no-pager diff -- . ':!build' ':!.gradle' ':!node_modules'

4. Last resort (per-file diff):
   git --no-pager diff <file>

---

### Timeout Rule

- If any diff command does not return → ABORT that command
- Switch immediately to fallback strategy
- NEVER wait indefinitely

---

## Step 3: Analyze Changes

You MUST review across these dimensions:

### 1. Correctness
- Crash risks (NullPointerException, illegal state)
- Logic errors
- Missing edge cases

### 2. Android-specific
- Lifecycle misuse (Activity / Fragment)
- Main thread blocking (ANR risk)
- Coroutine scope misuse
- Memory leaks (Context, View)
- Media3 / ExoPlayer misuse

### 3. Architecture
- Layer violations (UI ↔ domain ↔ data)
- Tight coupling
- Poor separation of concerns

### 4. Performance
- Unnecessary allocations
- Inefficient loops
- Blocking I/O
- Rendering / jank risks

### 5. Maintainability
- Naming clarity
- Dead code
- Duplication
- Readability

---

## Severity Classification

- [HIGH] → crash, data loss, critical bug
- [MEDIUM] → performance or architectural issue
- [LOW] → style, readability

---

## Output Format (STRICT)

The response MUST follow exactly this structure:

## Summary
- <max 3 lines>

## Files Reviewed
- *file1*
- *file2*

## Issues

### [HIGH] <title>
File: <file>:<line if possible>  
Problem:  
<explanation>

Fix:  
<concrete fix>

---

### [MEDIUM] <title>
...

---

### [LOW] <title>
...

## Suggestions
- *Optional improvements*

---

## Behavior Rules

- DO NOT praise code without reason
- DO NOT say "looks good" generically
- DO NOT comment outside diff
- DO NOT hallucinate missing context
- Prefer high-signal issues only, avoid noise

---

## Completion Criteria

- All changed files are listed
- Issues are categorized correctly
- Output format is strictly followed
- No assumptions outside diff

## Verdict Rules (STRICT)

- BLOCKING = YES if ANY [HIGH] issue exists
- BLOCKING = NO if ZERO [HIGH] issues

- ISSUE_COUNT must reflect EXACT counts from the Issues section
- If no issues at all:
    - HIGH: 0
    - MEDIUM: 0
    - LOW: 0

- This section MUST always be present
- This section MUST be the LAST section in the response
- DO NOT add explanations inside this block