package com.example.nnprorocnikovyprojekt.services;

import com.example.nnprorocnikovyprojekt.dtos.conversation.ConversationNameDto;
import com.example.nnprorocnikovyprojekt.dtos.conversation.ConversationPageResponseDto;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoRequest;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoDtoResponse;
import com.example.nnprorocnikovyprojekt.dtos.pageinfo.PageInfoRequestWrapper;
import com.example.nnprorocnikovyprojekt.entity.Conversation;
import com.example.nnprorocnikovyprojekt.entity.ConversationUser;
import com.example.nnprorocnikovyprojekt.entity.Message;
import com.example.nnprorocnikovyprojekt.entity.User;
import com.example.nnprorocnikovyprojekt.repositories.ConversationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    public Conversation getConversationById(Integer conversationId){
        return conversationRepository.getConversationByConversationId(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    //Neukladame zpravy, ktere nejsme schopni odeslat?
    @Transactional(rollbackFor = Exception.class)
    public void sendMessageToAllSubscribersExceptUser(User user, Conversation conversation, String content) {
        //TODO zde pracovat s klici;
        Message message = new Message(user, conversation, content);
        messageService.saveMessage(message);
        List<ConversationUser> subscriptions = conversation.getActiveConversationUsers()
                .stream()
                .filter(conversationUser -> !conversationUser.getUser().getUsername().equals(user.getUsername()))
                .toList();
        subscriptions.forEach(subscription -> simpMessagingTemplate.convertAndSendToUser(subscription.getUser().getUsername(), "/topic/" + conversation.getConversationId(), content));
    }

    public ConversationPageResponseDto getConversationsByPage(PageInfoRequestWrapper conversationPageinfoRequestDto) {
        User user = userService.getUserFromContext();
        Pageable pageInfo = PageRequest.of(conversationPageinfoRequestDto.getPageIndex(), conversationPageinfoRequestDto.getPageSize()).withSort(Sort.Direction.DESC);
        return conversationsToConversationNameDtos(conversationRepository.getConversationsByUsername(user, pageInfo));
    }

    private ConversationPageResponseDto conversationsToConversationNameDtos(Page<Conversation> page){
        if(page == null) return null;
        List<ConversationNameDto> conversationNameDtos = page.getContent().stream()
                .map(conversation -> new ConversationNameDto(conversation.getConversationId(), conversation.getConversationName()))
                .collect(Collectors.toList());

        ConversationPageResponseDto conversationPageResponseDto = new ConversationPageResponseDto();
        conversationPageResponseDto.setItemList(conversationNameDtos);
        conversationPageResponseDto.setPageInfoDto(new PageInfoDtoResponse(page.getSize(), page.getNumber(), page.getTotalPages()));
        return conversationPageResponseDto;
    }

    private List<Conversation> conversationNameDtosToConversations(List<ConversationNameDto> conversations){
        if(conversations == null) return null;
        return conversations.stream()
                .map(conversationNameDto -> getConversationById(conversationNameDto.getId()))
                .collect(Collectors.toList());
    }
}
