package com.tea.service;

import com.tea.dao.AdminDao;
import com.tea.entity.Admin;



public class AdminService {
    private final AdminDao adminDao = new AdminDao();
public Admin login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return null;
        }
        return adminDao.findByUsernameAndPassword(username.trim(), password);
    }
    public boolean register(String username, String password) {
         if (isBlank(username) || isBlank(password)) {
              throw new IllegalArgumentException("用户名/密码不能为空");
         }
          if (password.length() < 6) {
             throw new IllegalArgumentException("密码长度必须 ≥ 6 位");
          }
          if (adminDao.existsByUsername(username)) {
              throw new IllegalArgumentException("用户名已被占用");
          }
          Admin a = new Admin(username.trim(), password);
           return adminDao.insert(a) == 1;
           
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
