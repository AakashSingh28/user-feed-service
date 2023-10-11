package com.social.feed.respositories;

import com.social.feed.entities.UserFollowings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFollowingRepository extends JpaRepository<UserFollowings,Long> {

    @Query(value = "select * from user_followings where is_active=?1 and follower_id=?2" , nativeQuery = true)
    List<UserFollowings> findUserFollowingsByActiveAndFollowerId(boolean isActive, Long followerId);
}
