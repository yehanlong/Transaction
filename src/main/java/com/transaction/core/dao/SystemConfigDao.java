package com.transaction.core.dao;

import com.transaction.core.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemConfigDao extends JpaRepository<SystemConfig,Long> {
    SystemConfig getByPlatformAndEnabled(String platform,Boolean enabled);
    List<SystemConfig> getByEnabled(Boolean enabled);
}
