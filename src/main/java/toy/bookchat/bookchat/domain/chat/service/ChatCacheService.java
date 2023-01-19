package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.NotParticipatedException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatCacheService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    public ChatCacheService(UserRepository userRepository, ChatRoomRepository chatRoomRepository,
        ParticipantRepository participantRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
    }


    @Cacheable(cacheNames = "user")
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Cacheable(cacheNames = "chatroom")
    public ChatRoom findChatRoomByRoomSid(String roomSid) {
        return chatRoomRepository.findByRoomSid(roomSid).orElseThrow(
            ChatRoomNotFoundException::new);
    }

    @Cacheable(cacheNames = "participant")
    public Participant saveParticipantCache(Participant participant) {
        return participantRepository.save(participant);
    }

    @Cacheable(cacheNames = "participant")
    public Participant findParticipantByUserAndChatRoom(User user, ChatRoom chatRoom) {
        return participantRepository.findByUserAndChatRoom(user, chatRoom).orElseThrow(
            NotParticipatedException::new);
    }

    @CacheEvict(cacheNames = "participant")
    public void deleteParticipant(Participant participant) {
        participantRepository.delete(participant);
    }
    

}
