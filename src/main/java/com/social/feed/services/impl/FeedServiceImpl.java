package com.social.feed.services.impl;

import com.social.feed.dtos.UserFeedsResponseDto;
import com.social.feed.dtos.UserResponseDto;
import com.social.feed.entities.UserDetails;
import com.social.feed.entities.UserFollowings;
import com.social.feed.exceptions.FeedServiceException;
import com.social.feed.exceptions.UserNotFoundException;
import com.social.feed.interactors.UserPostServiceInteract;
import com.social.feed.respositories.UserFollowingRepository;
import com.social.feed.respositories.UserRepository;
import com.social.feed.services.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserFollowingRepository userFollowingRepository;
    private final UserPostServiceInteract userPostServiceInteract;
    private final UserRepository userRepository;

    @Value("${feedLimit}")
    @Setter
    private int feedLimit;

    @Override
    public List<UserFeedsResponseDto> getFeedsForTheUser(String userId) {
        List<UserResponseDto> userPosts = new ArrayList<>();
        Optional<UserDetails> userDetails;

        userDetails = userRepository.findById(Long.valueOf(userId));

        if(userDetails.isEmpty()){
            throw new UserNotFoundException("Invalid User");
        }

        try {
            List<UserFollowings> userFollowings = userFollowingRepository.findUserFollowingsByActiveAndFollowerId(true, Long.valueOf(userId));
            if (userFollowings != null && userFollowings.size() > 0) {

                for (UserFollowings userFollowings_ : userFollowings) {

                    List<UserResponseDto> userPostResponseDtos = userPostServiceInteract.getPostsFromFollowingPeople(userFollowings_);
                    List<UserResponseDto> userEventResponseDtos=userPostServiceInteract.getEventsFromFollowingPeople(userFollowings_);

                    if (userPostResponseDtos != null ) {
                        userPosts.addAll(userPostResponseDtos);
                    }
                    if (userEventResponseDtos!=null) {
                        userPosts.addAll(userEventResponseDtos);
                    }
                }
            }
        }
        catch (FeedServiceException e) {
            log.error("An error occurred while fetching user feeds", e);
            throw new FeedServiceException("Error fetching user feeds", e);
        }
        return sortThePostsAccordingToScores(userPosts, userDetails.get().getUserLocation());
    }

    public List<UserFeedsResponseDto> sortThePostsAccordingToScores(List<UserResponseDto> userPosts, String userLocation) {
        // Step 1: Sort userPosts by ranking (post score) in descending order
        userPosts.sort(Comparator.comparingInt(UserResponseDto::getPostScore).reversed());

        // Step 2: Performing secondary sorting by location
        userPosts.sort((o1, o2) -> {
            int postScoreComparison = Integer.compare(o2.getPostScore(), o1.getPostScore());
            if (postScoreComparison != 0) {
                return postScoreComparison; // Sort by ranking first
            } else {
                // If ranking is the same, sort by location
                if (isLocationRelevant(o1, userLocation) && !isLocationRelevant(o2, userLocation)) {
                    return -1;
                } else if (!isLocationRelevant(o1, userLocation) && isLocationRelevant(o2, userLocation)) {
                    return 1;
                } else {
                    return 0; // Sort by location for posts with the same ranking
                }
            }
        });

        // Step 3: Limit the number of items in the feed (e.g., top 200 items)
        userPosts = userPosts.stream().limit(feedLimit).collect(Collectors.toList());


        return userPosts.stream()
                .map(this::buildFeedResponseDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<UserFeedsResponseDto> buildFeedResponseDto(UserResponseDto userResponseDto) {
        return Optional.ofNullable(userResponseDto)
                .map(responseDto -> {
                    UserFeedsResponseDto userFeedsResponseDto = new UserFeedsResponseDto();
                    userFeedsResponseDto.setFeed(responseDto);
                    return userFeedsResponseDto;
                });
    }


    private boolean isLocationRelevant(UserResponseDto responseDto, String userLocation) {
        if (userLocation == null && responseDto.getUserLocation() == null) {
            return false;
        }
        return userLocation.equalsIgnoreCase(responseDto.getUserLocation());
    }
}
