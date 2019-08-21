package com.transaction.core.dao;

import com.transaction.core.entity.SymbolConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SymbolConfigDao extends JpaRepository<SymbolConfig,Long> {
    /**
     * 根据platform和enabled获取某个平台启用的交易配置
     * select * from symbol_config where platform = :1 and enabled = :2
     * @param platform
     * @param enabled
     * @return
     */
    List<SymbolConfig> getByPlatformAndEnabled(String platform,Boolean enabled);

    /**
     * 根据币种查找配置
     * @param platform
     * @param baseCoin
     * @param symbol1
     * @param symbol2
     * @return
     */
    @Query(value = "select * from symbol_config where platform = ?1 and base_coin = ?2 and symbol1 = ?3 and symbol2 = ?4 ",nativeQuery = true)
    SymbolConfig getBySymbol(String platform,String baseCoin,String symbol1,String symbol2);
}
