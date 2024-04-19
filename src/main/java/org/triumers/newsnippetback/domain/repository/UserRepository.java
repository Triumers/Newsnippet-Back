package org.triumers.newsnippetback.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.triumers.newsnippetback.domain.aggregate.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    Boolean existsByNickname(String nickname);

    Boolean existsByEmail(String email);

    User findById(int id);

    User findByEmail(String email);

    User findByNickname(String nickname);

    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.correctCnt > :correctCnt")
    int findRankByCorrectCnt(@Param("correctCnt") int correctCnt);

    @Query("SELECT u FROM User u ORDER BY u.correctCnt DESC")
    List<User> findLeague();
}
