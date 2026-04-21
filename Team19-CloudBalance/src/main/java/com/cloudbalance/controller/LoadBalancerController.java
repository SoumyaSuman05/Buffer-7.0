package com.cloudbalance.controller;

import com.cloudbalance.model.ApiResponse;
import com.cloudbalance.service.LoadBalancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LoadBalancerController
 *
 * Maps each original menu option to a REST endpoint:
 *
 *   1. POST   /api/add-request?n=50   → Add Request
 *   2. GET    /api/usage              → Show Usage
 *   3. GET    /api/max-usage          → Max Usage
 *   4. GET    /api/median             → Median Load
 *   5. GET    /api/overload           → Check Overload
 *   6. POST   /api/record-traffic     → Record Traffic
 *   7. GET    /api/predict            → Predict Load
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")   // allows the HTML frontend to call during dev
public class LoadBalancerController {

    private final LoadBalancerService service;

    public LoadBalancerController(LoadBalancerService service) {
        this.service = service;
    }

    // ── 1. Add Request ────────────────────────────────────────────────────
    @PostMapping("/add-request")
    public ResponseEntity<ApiResponse> addRequest(@RequestParam int n) {
        if (n < 1) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "n must be ≥ 1", null));
        }
        ApiResponse.AddRequestResult result = service.addRequests(n);
        return ResponseEntity.ok(
            new ApiResponse(true,
                "Processed " + result.totalProcessed + " of " + n + " requests",
                result));
    }

    // ── 2. Show Usage ─────────────────────────────────────────────────────
    @GetMapping("/usage")
    public ResponseEntity<ApiResponse> showUsage() {
        List<ApiResponse.ServerInfo> usage = service.getServerUsage();
        return ResponseEntity.ok(
            new ApiResponse(true, "Server usage fetched", usage));
    }

    // ── 3. Max Usage ──────────────────────────────────────────────────────
    @GetMapping("/max-usage")
    public ResponseEntity<ApiResponse> maxUsage() {
        ApiResponse.MaxUsageResult result = service.getMaxUsage();
        String msg = result.maxRequests == 0
            ? "No requests assigned yet"
            : "Max load: " + result.maxRequests + " on " + result.servers;
        return ResponseEntity.ok(new ApiResponse(true, msg, result));
    }

    // ── 4. Median Load ────────────────────────────────────────────────────
    @GetMapping("/median")
    public ResponseEntity<ApiResponse> medianLoad() {
        ApiResponse.MedianResult result = service.getMedianLoad();
        if (result.sortedLoads.isEmpty()) {
            return ResponseEntity.ok(
                new ApiResponse(false, "No data to calculate median", null));
        }
        return ResponseEntity.ok(
            new ApiResponse(true, "Median load: " + result.median, result));
    }

    // ── 5. Check Overload ─────────────────────────────────────────────────
    @GetMapping("/overload")
    public ResponseEntity<ApiResponse> checkOverload() {
        List<ApiResponse.ServerInfo> servers = service.checkOverload();
        long overloaded = servers.stream()
            .filter(s -> "OVERLOADED".equals(s.status)).count();
        String msg = overloaded == 0
            ? "All servers are within normal load"
            : overloaded + " server(s) overloaded";
        return ResponseEntity.ok(new ApiResponse(true, msg, servers));
    }

    // ── 6. Record Traffic ─────────────────────────────────────────────────
    @PostMapping("/record-traffic")
    public ResponseEntity<ApiResponse> recordTraffic() {
        ApiResponse.TrafficResult result = service.recordTraffic();
        return ResponseEntity.ok(
            new ApiResponse(true,
                "Snapshot recorded. Total load: " + result.currentTotal,
                result));
    }

    // ── 7. Predict Load ───────────────────────────────────────────────────
    @GetMapping("/predict")
    public ResponseEntity<ApiResponse> predictLoad() {
        ApiResponse.PredictResult result = service.predictLoad();
        if (result == null) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false,
                    "Not enough data. Record at least 2 traffic snapshots first.", null));
        }
        return ResponseEntity.ok(
            new ApiResponse(true,
                "Predicted next load: " + result.predictedLoad,
                result));
    }
}
