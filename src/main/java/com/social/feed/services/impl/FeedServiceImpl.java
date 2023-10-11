package com.social.feed.services.impl;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.dtos.UserPostResponseDto;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.services.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserFollowingRepository userFollowingRepository;
    private final UserPostServiceInteract userPostServiceInteract;

    @Override
    public List<UserFeedsResponseDto> getFeedsForTheUser(String userId) {
        List<UserPostResponseDto> userPosts = new ArrayList<>();
        try {
            List<UserFollowings> userFollowings = userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, Long.valueOf(userId));
            if (userFollowings != null) {
                for (UserFollowings userFollowings_ : userFollowings) {
                    List<UserPostResponseDto> userPostResponseDtos = userPostServiceInteract.getPostsFromFollowingPeople(userFollowings_);
                    if (userPostResponseDtos != null) {
                        userPosts.addAll(userPostResponseDtos);
                    } else {
                        log.error("No posts were found for following id -> {}", userFollowings_.getFollowingId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while fetching user feeds", e);
            throw new UserNotFoundException("Error fetching user feeds",e);
        }
        return sortThePostsAccordingToScores(userPosts);
    }

    private List<UserFeedsResponseDto> sortThePostsAccordingToScores(List<UserPostResponseDto> userPosts) {
        userPosts.sort((o1, o2) -> o2.getPostScore() - o1.getPostScore());
        return userPosts.stream()
                .map(this::buildFeedResponseDto)
                .filter(Optional::isPresent)
                .map(Optional::get).limit(200)
                .collect(Collectors.toList());
    }

    private Optional<UserFeedsResponseDto> buildFeedResponseDto(UserPostResponseDto userPostResponseDto) {
        return Optional.ofNullable(userPostResponseDto)
                .map(postDto -> {
                    UserFeedsResponseDto userFeedsResponseDto = new UserFeedsResponseDto();
                    userFeedsResponseDto.setPost(postDto.getPostContent());
                    return userFeedsResponseDto;
                });
    }
}
