CloudBalance — Enterprise Server Load Balancing System

Problem Statement

Design an Enterprise Server Load Balancing System that distributes incoming requests efficiently across servers that supports priority handling, overload detection, dynamic scaling, failure recovery, and performance analytics.

The system is capable of intelligently routing a variable volume of incoming requests across a heterogeneous pool of servers each with a distinct capacity while ensuring no server exceeds a configurable utilisation threshold. When the existing server pool reaches saturation, the system autonomously provisions additional servers (auto-scaling) to absorb the overflow. Furthermore, the system must expose analytical capabilities including overload detection, median load computation, traffic snapshotting, and short-term load forecasting via moving average prediction.

Data Structures Used

1. HashMap<String, Integer> — Load Map & Capacity Map

Two HashMap instances form the backbone of the system's runtime state:
loadMap — maps each server name to its current number of assigned requests.
capacityMap — maps each server name to its maximum request capacity.
The system performs server load lookups and updates on every single request assignment. HashMap provides O(1) average-case time complexity for both get() and put(), making it far superior to a list-based approach (which would require O(n) search) for high-frequency operations.

2. ArrayList<Integer> — Traffic History

An ArrayList stores the chronological sequence of total load snapshots recorded via the Record Traffic operation.
Dynamic resizing — snapshots accumulate over time with no fixed upper bound, making a static array unsuitable.
Index-based access — the prediction algorithm requires retrieval of the last k elements via get(i), which is O(1) in an ArrayList.
Ordered insertion — preserves chronological order naturally via add().

3. ArrayList<String> / ArrayList<Integer> — Sorted Intermediate Lists

Temporary ArrayList instances are constructed whenever a sorted view of data is needed:
Sorting server names in numerical order (Server1 → Server10 → Server11) using a custom Comparator that strips non-numeric characters and compares integer suffixes.
Sorting load values in ascending order to compute the statistical median.
Intermediate lists are used instead of sorting the HashMap directly, which preserves the integrity of the original state across all operations.

4. Collections Utility

| Method                          | Used For                                      | Complexity |
|---------------------------------|-----------------------------------------------|------------|
| `Collections.sort(list, comparator)` | Numeric server name ordering & median preparation | O(n log n) |
| `Collections.max(values)`       | Finding peak load across all servers          | O(n)       |

