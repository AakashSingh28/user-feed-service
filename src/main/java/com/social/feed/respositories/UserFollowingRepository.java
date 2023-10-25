package com.social.feed.respositories;

import com.social.feed.entities.UserFollowings;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserFollowingRepository extends JpaRepository<UserFollowings,Long> {

    @Query(value = "select * from user_followings where is_active=?1 and follower_id=?2" , nativeQuery = true)
    List<UserFollowings> findUserFollowingsByActiveAndFollowerId(boolean isActive, Long followerId);

    @Modifying
    @Transactional
    @Query("UPDATE UserFollowings uf SET uf.isActive = :isActive WHERE uf.followerId = :followerId AND uf.followingId = :followingId")
    void updateIsActiveByFollowerIdAndFollowingId(Long followerId, Long followingId, boolean isActive);
}
