package com.cloudbalance.model;

import java.util.List;
import java.util.Map;

// ── Generic API response wrapper ──────────────────────────────────────────────
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

    public ApiResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data    = data;
    }

    public boolean isSuccess()  { return success; }
    public String  getMessage() { return message; }
    public Object  getData()    { return data; }


    // ── Nested payload types (static inner classes) ────────────────────────

    /** Single server's load/capacity snapshot */
    public static class ServerInfo {
        public String name;
        public int    load;
        public int    capacity;
        public double usagePct;
        public String status;   // "NORMAL" | "WARNING" | "OVERLOADED"

        public ServerInfo(String name, int load, int capacity) {
            this.name     = name;
            this.load     = load;
            this.capacity = capacity;
            this.usagePct = capacity > 0 ? (load * 100.0) / capacity : 0.0;
            if      (usagePct >= 90) this.status = "OVERLOADED";
            else if (usagePct >= 65) this.status = "WARNING";
            else                      this.status = "NORMAL";
        }
    }

    /** Result of an add-request operation */
    public static class AddRequestResult {
        public int     requested;
        public int     initiallyProcessed;
        public int     scaledProcessed;
        public int     totalProcessed;
        public int     unprocessed;
        public boolean autoScaled;
        public List<String> scaleLog;

        public AddRequestResult(int requested, int initiallyProcessed,
                                int scaledProcessed, List<String> scaleLog) {
            this.requested          = requested;
            this.initiallyProcessed = initiallyProcessed;
            this.scaledProcessed    = scaledProcessed;
            this.totalProcessed     = initiallyProcessed + scaledProcessed;
            this.unprocessed        = requested - this.totalProcessed;
            this.autoScaled         = scaledProcessed > 0;
            this.scaleLog           = scaleLog;
        }
    }

    /** Max-usage result */
    public static class MaxUsageResult {
        public int         maxRequests;
        public List<String> servers;

        public MaxUsageResult(int maxRequests, List<String> servers) {
            this.maxRequests = maxRequests;
            this.servers     = servers;
        }
    }

    /** Median-load result */
    public static class MedianResult {
        public double       median;
        public List<Integer> sortedLoads;

        public MedianResult(double median, List<Integer> sortedLoads) {
            this.median      = median;
            this.sortedLoads = sortedLoads;
        }
    }

    /** Traffic snapshot result */
    public static class TrafficResult {
        public int         currentTotal;
        public List<Integer> history;

        public TrafficResult(int currentTotal, List<Integer> history) {
            this.currentTotal = currentTotal;
            this.history      = history;
        }
    }

    /** Load prediction result */
    public static class PredictResult {
        public double      predictedLoad;
        public int         samplesUsed;
        public List<Integer> recentValues;

        public PredictResult(double predictedLoad, int samplesUsed, List<Integer> recentValues) {
            this.predictedLoad = predictedLoad;
            this.samplesUsed   = samplesUsed;
            this.recentValues  = recentValues;
        }
    }
}
