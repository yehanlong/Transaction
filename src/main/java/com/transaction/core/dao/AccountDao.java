package com.transaction.core.dao;

import com.transaction.core.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:20
 */
public interface AccountDao extends JpaRepository<AccountEntity,Integer> {
}
