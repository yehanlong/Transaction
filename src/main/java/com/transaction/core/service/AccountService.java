package com.transaction.core.service;

import com.transaction.core.dao.AccountDao;
import com.transaction.core.entity.AccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author kfm bg384656
 * @date 2019/9/2 12:21
 */
@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;


    public AccountEntity save(AccountEntity accountEntity){
        return accountDao.save(accountEntity);
    }
}
