package com.clothing.management.auth.mastertenant.repository;

import com.clothing.management.auth.mastertenant.entity.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterUserRepository extends JpaRepository<MasterUser, Integer> {
    @Query(value = "select * from user u where u.user_name = :userName and u.password = :password" , nativeQuery = true)
    MasterUser authenticate(@Param("userName") String userName, @Param("password") String password);
}
