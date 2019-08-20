package com.transaction.core.dao;

import com.transaction.core.entity.SMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SMSDao extends JpaRepository<SMS,Long> {
    @Query(nativeQuery = true, value = "select t.* from sms t where id = (select max(id) from sms)")
    SMS getOne();
}
