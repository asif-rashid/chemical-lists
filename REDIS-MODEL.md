# Redis Data Model

## Overview
The **Chemical List Management API** uses **Redis** as its primary and persistent data store.  
Redis is configured in **AOF (Append Only File)** mode for durability and deployed via **Azure Cache for Redis**.  

The design emphasizes simplicity, idempotency, and predictable key patterns to support fast lookups without requiring RediSearch or secondary indices.

---

## Key Design Principles

| Principle | Description |
|------------|-------------|
| **Simplicity** | Use basic Redis data types only (HASH, SET, STRING). |
| **Idempotency** | Repeated insertions of the same item are safe and do not produce duplicates. |
| **Read Optimization** | GET operations require no joins or cross-key lookups. |
| **Consistency** | Each list and its chemical members are stored under predictable key names. |
| **Isolation** | Each list is self-contained under its own namespace. |
| **Persistence** | AOF ensures data durability between restarts. |

---

## Redis Keys Overview

| Entity | Key Pattern | Type | Description |
|---------|--------------|------|-------------|
| Chemical List Metadata | `list:{listId}` | HASH | Stores metadata for a chemical list |
| List Members | `list:{listId}:chemicals` | SET | Contains all CIRI-IDs associated with a list |
| Label Index | `label:{label}` | STRING | Maps a unique label to its `listId` (used for uniqueness check) |
| ID Counter | `seq:list:id` | STRING | Auto-increment counter for generating numeric list IDs |
| Temporary Idempotency | `idem:{operationId}` | STRING (optional) | Tracks idempotent operations, TTL-based |

---

## Key Schema Details

### 1. Chemical List Metadata

**Key Pattern:**
```

list:{listId}

```

**Type:** `HASH`

**Example:**
```

HSET list:101 
name "Industrial Solvents" 
label "indsolv" 
description "List of regulated industrial solvents" 
createdBy "asif" 
createdDate "2025-11-11T18:30:00Z" 
lastUpdatedBy "asif" 
lastUpdated "2025-11-11T18:30:00Z"

```

**Operations:**
| Operation | Command | Notes |
|------------|----------|-------|
| Create new list | `INCR seq:list:id` → `HSET list:{id}` | Generates next numeric ID |
| Get list | `HGETALL list:{id}` | Returns metadata |
| Update list | `HSET list:{id} field value` | Partial updates supported |
| Delete list | `DEL list:{id}` | Also deletes associated SET |

---

### 2. Chemical Members

**Key Pattern:**
```

list:{listId}:chemicals

```

**Type:** `SET`

**Example:**
```

SADD list:101:chemicals "CIRI-000001" "CIRI-000002" "CIRI-000003"

```

**Operations:**
| Operation | Command | Notes |
|------------|----------|-------|
| Add single chemical | `SADD list:{id}:chemicals {ciriId}` | Idempotent |
| Batch add | `SADD list:{id}:chemicals {ciriId1} {ciriId2} ...` | Simple multi-add |
| Get all chemicals | `SMEMBERS list:{id}:chemicals` | Returns full set |
| Check existence | `SISMEMBER list:{id}:chemicals {ciriId}` | Boolean check |
| Delete chemical | `SREM list:{id}:chemicals {ciriId}` | Remove one member |
| Delete all | `DEL list:{id}:chemicals` | When deleting list |

**Characteristics:**
- Duplicate additions are silently ignored (idempotent by Redis SET behavior).
- No explicit ordering is maintained.
- All batch additions succeed atomically within a single Redis command.

---

### 3. Label Index

**Purpose:** Enforce uniqueness for list `label`.

**Key Pattern:**
```

label:{label}

```

**Type:** `STRING`

**Example:**
```

SETNX label:indsolv 101

```

**Operations:**
| Operation | Command | Notes |
|------------|----------|-------|
| Create index | `SETNX label:{label} {listId}` | Prevents duplicate labels |
| Resolve listId by label | `GET label:{label}` | Enables quick lookup |
| Remove on delete | `DEL label:{label}` | Cleanup during list deletion |

---

### 4. ID Generator

**Purpose:** Generate numeric, Redis-friendly sequential IDs.

**Key Pattern:**
```

seq:list:id

```

**Type:** `STRING`

**Example:**
```

INCR seq:list:id

```

**Result:**
Each new list gets an incrementing integer ID (`1`, `2`, `3`, …).

---

### 5. Idempotency Tracking (Optional)

**Key Pattern:**
```

idem:{operationId}

```

**Type:** `STRING` with TTL

**Use Case:**
Tracks repeated client submissions (e.g., retries of the same POST with `Idempotency-Key` header).

**Example:**
```

SETNX idem:abc123 true
EXPIRE idem:abc123 3600

```

---

## Data Access Patterns

| Use Case | Redis Command(s) | Notes |
|-----------|------------------|-------|
| Create new list | `INCR` → `SETNX` → `HSET` | Ensures unique label |
| Retrieve list | `HGETALL` | Fast metadata fetch |
| Retrieve all lists | `SCAN "list:*"` | Used for small deployments (no pagination) |
| Add chemical(s) | `SADD` | Handles both single and batch adds |
| Get all chemicals | `SMEMBERS` | Full list of members |
| Delete list | `DEL` (HASH + SET + label key) | Full cleanup |

---

## Example Redis Snapshot

```

127.0.0.1:6379> KEYS *

1. "list:101"
2. "list:101:chemicals"
3. "label:indsolv"
4. "seq:list:id"

127.0.0.1:6379> HGETALL list:101

1. "name"
2. "Industrial Solvents"
3. "label"
4. "indsolv"
5. "description"
6. "List of regulated industrial solvents"
7. "createdBy"
8. "asif"
9. "createdDate"
10. "2025-11-11T18:30:00Z"

127.0.0.1:6379> SMEMBERS list:101:chemicals

1. "CIRI-000001"
2. "CIRI-000002"
3. "CIRI-000003"

````

---

## Redis Configuration (Azure Cache)

| Setting | Value | Purpose |
|----------|--------|----------|
| **Persistence** | AOF (Append Only File) | Durable write-ahead log |
| **Eviction Policy** | `noeviction` | Prevent data loss |
| **Replication** | Optional | For HA setup |
| **MaxMemoryPolicy** | No eviction | Ensure data retention |
| **TLS** | Enabled | Secure connection |

Environment variables:
```bash
REDIS_HOST=mycache.redis.cache.windows.net
REDIS_PORT=6380
REDIS_PASSWORD=yourpassword
REDIS_SSL=true
````

---

## Summary

* **Data Model:** Simple, direct Redis keys (HASH for metadata, SET for members).
* **IDs:** Auto-incremented numeric IDs for Redis efficiency.
* **Uniqueness:** Enforced via `label:{label}` index key.
* **Persistence:** Achieved with AOF in Azure Cache.
* **Batch Adds:** Simple `SADD` command, inherently idempotent.
* **No TTLs:** Lists persist indefinitely until explicitly deleted.

---

## Future Extensions

* Add `SCAN` + cursor-based pagination if number of lists grows large.
* Implement event publication (`RedisStream`) for downstream sync if needed.
* Optionally add secondary indexing with **RediSearch** for label/name filtering.

