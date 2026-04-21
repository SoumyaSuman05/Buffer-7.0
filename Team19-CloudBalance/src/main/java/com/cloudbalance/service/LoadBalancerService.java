package com.cloudbalance.service;

import com.cloudbalance.model.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoadBalancerService {

    private final Map<String, Integer> loadMap     = new LinkedHashMap<>();
    private final Map<String, Integer> capacityMap = new LinkedHashMap<>();
    private final List<Integer>        trafficHistory = new ArrayList<>();
    
    public LoadBalancerService() {
        capacityMap.put("Server1",  30);
        capacityMap.put("Server2",  12);
        capacityMap.put("Server3",  15);
        capacityMap.put("Server4",  25);
        capacityMap.put("Server5",  27);
        capacityMap.put("Server6",  10);
        capacityMap.put("Server7",  30);
        capacityMap.put("Server8",  18);
        capacityMap.put("Server9",  29);
        capacityMap.put("Server10", 30);        
    }

    
    public ApiResponse.AddRequestResult addRequests(int n) {
        int processed = 0;

        for (int i = 0; i < n; i++) {
            int before = totalRequests();
            assignRequest();
            int after  = totalRequests();
            if (after > before) {
                processed++;
            } else {
                break;
            }
        }

        int remaining = n - processed;
        List<String> scaleLog = new ArrayList<>();
        int scaledProcessed   = 0;

        if (remaining > 0) {
            scaleLog.add("Existing servers at capacity. Scaling up for " + remaining + " requests...");
            scaledProcessed = autoScale(remaining, scaleLog);
        }

        return new ApiResponse.AddRequestResult(n, processed, scaledProcessed, scaleLog);      

    }

   
    public List<ApiResponse.ServerInfo> getServerUsage() {
        List<ApiResponse.ServerInfo> result = new ArrayList<>();

        for (String server : sortedServers()) {
            int load = loadMap.getOrDefault(server, 0);
            int cap  = capacityMap.get(server);
            result.add(new ApiResponse.ServerInfo(server, load, cap));
        }
        
        return result;
    }

    public ApiResponse.MaxUsageResult getMaxUsage() {
       
       
    }

    public ApiResponse.MedianResult getMedianLoad() {
       
    }

    public List<ApiResponse.ServerInfo> checkOverload() {
        return getServerUsage();       
    }

    
    public ApiResponse.TrafficResult recordTraffic() {
        
    }

    public ApiResponse.PredictResult predictLoad() {
       
    }


    private int totalRequests() {
        return loadMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    private void assignRequest() {
        String bestServer = null;
        double minUsage   = 100.0;

        for (String server : capacityMap.keySet()) {
            int    load     = loadMap.getOrDefault(server, 0);
            int    capacity = capacityMap.get(server);
            if (capacity <= 0) continue;

            double usage = (load * 100.0) / capacity;
            if (usage >= 90.0) continue;

            if (usage < minUsage) {
                minUsage   = usage;
                bestServer = server;
            }
        }
        if (bestServer != null) {
            loadMap.put(bestServer, loadMap.getOrDefault(bestServer, 0) + 1);
        }       
    }

    private int autoScale(int remaining, List<String> log) {
        int totalNewProcessed = 0;

        while (remaining > 0) {
            if (capacityMap.size() >= 100) {
                log.add("Hard limit reached. Cannot scale further.");
                break;
            }

            String newName = "Server" + (capacityMap.size() + 1);
            int    newCap;
            String spike = "";

            if (remaining > 27) {
                newCap = 30;
                spike  = " [CRITICAL SPIKE]";
            } else if (remaining > 18) {
                newCap = 20;
                spike  = " [HEAVY SPIKE]";
            } else {
                newCap = 10;
            }

            capacityMap.put(newName, newCap);
            log.add("Added " + newName + " with capacity " + newCap + spike);

            int usable  = (int)(newCap * 0.9);
            int canTake = Math.min(remaining, usable);

            loadMap.put(newName, canTake);
            remaining         -= canTake;
            totalNewProcessed += canTake;

            log.add(newName + " absorbed " + canTake + " requests · Remaining: " + remaining);
        }
        return totalNewProcessed;
    }

    private List<String> sortedServers() {
        List<String> servers = new ArrayList<>(capacityMap.keySet());
        servers.sort(Comparator.comparingInt(s -> Integer.parseInt(s.replaceAll("\\D", ""))));
        return servers;
    }
}
