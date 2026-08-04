package com.lsf.ironbus.admin.web.controller;

import com.lsf.ironbus.admin.app.AdminDashboardService;
import com.lsf.ironbus.admin.web.response.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService service;

    @GetMapping
    public AdminDashboardResponse get() {
        return service.getDashboard();
    }
}