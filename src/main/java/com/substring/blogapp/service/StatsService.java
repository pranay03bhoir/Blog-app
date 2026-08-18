package com.substring.blogapp.service;

import com.substring.blogapp.dto.DashboardStatsDto;

public interface StatsService {

    DashboardStatsDto getGlobalStats();

    DashboardStatsDto getUserStats(String userEmail);
}
