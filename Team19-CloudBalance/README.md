# CloudBalance — Enterprise Server Load Balancing System

## Problem Statement

Design an Enterprise Server Load Balancing System that distributes incoming requests efficiently across servers while supporting priority handling, overload detection, dynamic scaling, failure recovery, and performance analytics.

The system intelligently routes a variable volume of incoming requests across a heterogeneous pool of servers, each with a distinct capacity, ensuring no server exceeds a configurable utilisation threshold.

When the existing server pool reaches saturation, the system automatically provisions additional servers (auto-scaling) to absorb overflow. It also provides analytics such as overload detection, median load computation, traffic snapshotting, and short-term load prediction using moving averages.

---

## Data Structures Used

### 1. HashMap<String, Integer> — Load Map & Capacity Map

Two HashMap instances form the backbone of the system:

- **loadMap** — maps each server to its current number of assigned requests  
- **capacityMap** — maps each server to its maximum capacity  

**Why HashMap?**  
Provides **O(1)** average time complexity for `get()` and `put()`, making it ideal for high-frequency operations.

---

### 2. ArrayList<Integer> — Traffic History

Stores chronological snapshots of system load:

- Dynamic resizing (no fixed limit)  
- Fast access using `get(i)` → **O(1)**  
- Maintains order using `add()`  

---

### 3. ArrayList — Sorted Intermediate Lists

Used when sorting is required:

- Numeric server name ordering (Server1 → Server10 → Server11)  
- Sorting loads for median calculation  
- Preserves original data by avoiding direct HashMap sorting  

---

### 4. Collections Utility

The `Collections` class provides key operations:

| Method | Used For | Complexity |
|--------|----------|------------|
| `Collections.sort(list, comparator)` | Numeric ordering & median preparation | O(n log n) |
| `Collections.max(values)` | Finding peak load | O(n) |

---

## DSA Concepts Applied

### 1. Greedy Algorithm — Request Assignment

**Method:** `assignRequest()`

- Assigns each request to the server with the lowest utilisation below threshold  
- Ensures balanced distribution  

**Time Complexity:** O(n) per request  

---

### 2. Linear Search — Min & Max Load

**Methods:** `assignRequest()`, `maximumUsage()`

- Finds least loaded server  
- Finds maximum load server  

**Time Complexity:** O(n)  

---

### 3. Sorting — Ordering & Median

**Methods:** `displayServerUsage()`, `calculateMedian()`

- Custom comparator for numeric sorting  
- Sorting required before median calculation  

**Time Complexity:** O(n log n)  

---

### 4. Median Computation

**Method:** `calculateMedian()`

- Odd: `median = loads[n/2]`  
- Even: `median = (loads[n/2] + loads[n/2 - 1]) / 2.0`  

✔ Resistant to outliers → more accurate than mean  

---

### 5. Moving Average — Load Prediction

**Method:** `predictLoad()`

- Uses Simple Moving Average (SMA)  
- k = min(3, total snapshots)
- predicted = (last k values sum) / k

**Time Complexity:** O(1) 

---

### 6. Dynamic Scaling

**Method:** `autoScale()`

- Adds servers based on load spike:
27 → capacity 30
18 → capacity 20
else → capacity 10

- Each server uses 90% capacity  

**Time Complexity:** O(r/c)  

---

### 7. Hash-Based State Management

- All operations use HashMap  
- Enables **O(1)** read/write  

---

## Tech Stack

- **Backend:** Java 17, Spring Boot  
- **Frontend:** HTML, CSS, JavaScript  
- **Build Tool:** Maven  
- **Architecture:** REST API  

---
## Demo Video Link
https://drive.google.com/file/d/1Eazue03FlcxbBE7LR-9oWDoLCsE9ELzU/view?usp=sharing

## How to Run

```bash
mvn spring-boot:run
