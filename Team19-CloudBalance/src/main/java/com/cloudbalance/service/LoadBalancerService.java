package com.cloudbalance.service;

import com.cloudbalance.model.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoadBalancerService {


    
    public LoadBalancerService() {
        
    }

    
    public ApiResponse.AddRequestResult addRequests(int n) {
        

        
    }

   
    public List<ApiResponse.ServerInfo> getServerUsage() {
        List<ApiResponse.ServerInfo> result = new ArrayList<>();
       
        return result;
    }

    public ApiResponse.MaxUsageResult getMaxUsage() {
       
       
    }

    public ApiResponse.MedianResult getMedianLoad() {
       
    }

    public List<ApiResponse.ServerInfo> checkOverload() {
       
    }

    
    public ApiResponse.TrafficResult recordTraffic() {
        
    }

    public ApiResponse.PredictResult predictLoad() {
       
    }


    private int totalRequests() {
      
    }

    private void assignRequest() {
       
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
